package com.kotlin.spring.management.configurations.filters

import com.kotlin.spring.management.configurations.security.jwt.JwtProvider
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter


class JsonWebTokenFilter(
    private val jwtProvider: JwtProvider
): OncePerRequestFilter() {
    // Refresh Token Request URI
    private val REFRESH_REQUEST_PATH = "/api/jwt/refresh"

    private val logger = LoggerFactory.getLogger(JsonWebTokenFilter::class.java)
    private val pathMatcher = AntPathMatcher()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Refresh Token
        if (isRefreshTokenRequest(request)) {
            handleRefreshTokenRequest(request, response)
            return
        }

        // Access Token
        if (pathMatcher.match("/api/**", request.servletPath) &&
            !pathMatcher.match("/api/login", request.servletPath)) {
            if (!processJwtAuthentication(request, response)) {
                return
            }
        }

        if (!response.isCommitted) {
            filterChain.doFilter(request, response)
        }
    }


    /**
     * AccessToken Verify
     */

    private fun processJwtAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Boolean {
        try {
            logger.info("JwtFilterChain Activated - RequestURI[${request.servletPath}]")
            val token = jwtProvider.extractAccessTokenFromHttpRequest(request)
            if (SecurityContextHolder.getContext().authentication == null) {
                if (token != null && jwtProvider.validateAccessToken(token)) {
                    SecurityContextHolder.getContext().authentication = jwtProvider.extractAuthenticationFromAccessToken(token)
                    logger.info("Request Successful")
                    return true
                }
            }
        } catch (e: ExpiredJwtException) {
            this.handleTokenExpiredException(response, e)
        } catch (e: JwtException) {
            this.handleJwtException(response, e)
        } catch (e: AuthenticationException) {
            this.handleAuthenticationException(response, e)
        } catch (e: Exception) {
            this.handleMiscellaneousExceptions(response, e)
        }
        return false
    }

    /**
     * Error Handlers
     */

    private fun handleJwtException(response: HttpServletResponse, exception: JwtException) {
        logger.info("Request Failed - JwtException")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write("{\"message\": \"Authorization error: Invalid token.\"}")
        response.writer.flush()
    }

    private fun handleAuthenticationException(response: HttpServletResponse, exception: AuthenticationException) {
        logger.info("Request Failed - AuthenticationException")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write("{\"message\": \"Authorization error: Invalid token.\"}")
        response.writer.flush()
    }

    private fun handleTokenExpiredException(response: HttpServletResponse, exception: ExpiredJwtException) {
        logger.info("Request Failed - ExpiredJwtException")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write("{\"message\": \"Authorization error: Expired token.\"}")
        response.writer.flush()
    }

    private fun handleMiscellaneousExceptions(response: HttpServletResponse, exception: Exception) {
        logger.info("Request Failed - MiscellaneousException")
        response.status = HttpServletResponse.SC_BAD_REQUEST
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write("{\"message\": \"Authorization error: Invalid token.\"}")
        response.writer.flush()
    }

    /**
     * Refresh Token Related functions
     * For Now, This Logic does not save The Token To DB, however Consider Saving Token To DB and improve the Security
     */

    private fun isRefreshTokenRequest(request: HttpServletRequest): Boolean {
        return request.servletPath == REFRESH_REQUEST_PATH
    }

    private fun handleRefreshTokenRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val refreshToken = jwtProvider.extractRefreshTokenFromHttpRequest(request)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        if (refreshToken != null && jwtProvider.validateRefreshToken(refreshToken)) {
            val authentication = jwtProvider.extractAuthenticationFromRefreshToken(refreshToken)
            val newAccessToken = jwtProvider.generateToken(authentication.name)
            response.status = HttpServletResponse.SC_OK
            response.writer.write("{\"accessToken\": \"$newAccessToken\"}")
        } else {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("{\"error\": \"Invalid refresh token\"}")
        }
        response.writer.flush()
    }

}