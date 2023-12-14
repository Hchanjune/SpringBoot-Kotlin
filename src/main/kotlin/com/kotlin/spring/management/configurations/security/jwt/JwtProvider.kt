package com.kotlin.spring.management.configurations.security.jwt

import com.kotlin.spring.management.configurations.security.userDetails.CustomUserDetailService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(private val customUserDetailsService: CustomUserDetailService) {

    private val logger = LoggerFactory.getLogger(JwtProvider::class.java)

    companion object {
        private const val ACCESS_TOKEN_KEY_STRING = "ehdgoanfrhkqorentksdlakfmrhekfgehfhrgksmsladalqhdngktkdnflskfkakstp"
        private const val REFRESH_TOKEN_KEY_STRING = "ehdgoanfrhkqorentksdlakfmrhekfgehfhrgksmsladalqhdngktkdnflskfkakstp"
        private const val ACCESS_TOKEN_EXPIRATION_TIME: Long = 1000 * 60 * 60 * 10 // 10 Hours
        private const val REFRESH_TOKEN_EXPIRATION_TIME: Long = 1000 * 60 * 60 * 10 // 10 Hours

        private const val ISSUER = "ISSUER - SPRINGBOOT - KOTLIN"
        private const val AUDIENCE = "AUDIENCE - SPRINGBOOT - KOTLIN"

        private val ACCESS_TOKEN_SECRET_KEY: SecretKey = Keys.hmacShaKeyFor(ACCESS_TOKEN_KEY_STRING.toByteArray())
        private val REFRESH_TOKEN_SECRET_KEY: SecretKey = Keys.hmacShaKeyFor(REFRESH_TOKEN_KEY_STRING.toByteArray())
    }

    fun generateAccessToken(id: String): String {
        val userObject = customUserDetailsService.getUserObjectById(id)
        return Jwts.builder()
            .header()
                .type("JWT")
                .and()
            .claims()
                .id(id)
                .add("company", userObject.company)
                .add("roles", userObject.roles)
                .add("ver", "Version Info")
                .add("apkVer", "Version Info")
                .issuer(ISSUER)
                .audience()
                    .add(AUDIENCE)
                    .and()
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .and()
            .signWith(ACCESS_TOKEN_SECRET_KEY)
            .compact()
    }

    fun generateRefreshToken(id: String): String {
        val userObject = customUserDetailsService.getUserObjectById(id)
        return Jwts.builder()
            .header()
                .type("JWT-RefreshToken")
                .and()
            .claims()
                .id(id)
            .issuer(ISSUER)
            .audience()
            .add(AUDIENCE)
            .and()
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .and()
            .signWith(REFRESH_TOKEN_SECRET_KEY)
            .compact()
    }

    fun validateAccessToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(ACCESS_TOKEN_SECRET_KEY)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: ExpiredJwtException) {
            throw JwtException("Expired Token")
        } catch (e: JwtException) {
            throw JwtException("Jwt Exception")
        } catch (e: Exception) {
            throw Exception()
        }
    }

    fun validateRefreshToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(REFRESH_TOKEN_SECRET_KEY)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: ExpiredJwtException) {
            throw JwtException("Expired Token")
        } catch (e: JwtException) {
            throw JwtException("Jwt Exception")
        } catch (e: Exception) {
            throw Exception()
        }
    }

    @Deprecated(message = "Method No LongerUsed", replaceWith = ReplaceWith("validateToken(token)"))
    fun isAccessTokenExpired(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(ACCESS_TOKEN_SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .payload
                .expiration
                .before(Date())
        } catch (e: ExpiredJwtException) {
            false
        }
    }

    @Deprecated(message = "Method No LongerUsed", replaceWith = ReplaceWith("validateToken(token)"))
    fun isRefreshTokenExpired(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(REFRESH_TOKEN_SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .payload
                .expiration
                .before(Date())
        } catch (e: ExpiredJwtException) {
            false
        }
    }

    fun extractPayloadsFromAccessToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(ACCESS_TOKEN_SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun extractPayloadsFromRefreshToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(REFRESH_TOKEN_SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun extractAccessTokenFromHttpRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7)
        }
        return null
    }

    fun extractRefreshTokenFromHttpRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("X-Refresh-Token")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7)
        }
        return null
    }

    fun extractAuthenticationFromAccessToken(token: String): Authentication {
        val claims = this.extractPayloadsFromAccessToken(token)
        val roles: List<String> = claims["roles"] as List<String>
        return UsernamePasswordAuthenticationToken(
            customUserDetailsService.loadUserByUsername(claims),
            "Password Secured",
            roles.map { SimpleGrantedAuthority(it) }.toMutableList()
        )
    }

    fun extractAuthenticationFromRefreshToken(token: String): Authentication {
        val claims = this.extractPayloadsFromRefreshToken(token)
        val roles: List<String> = claims["roles"] as List<String>
        return UsernamePasswordAuthenticationToken(
            customUserDetailsService.loadUserByUsername(claims),
            "Password Secured",
            roles.map { SimpleGrantedAuthority(it) }.toMutableList()
        )
    }



}