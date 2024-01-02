package com.kotlin.spring.management.configurations.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.logging.LogFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class CustomLoginRedirectionFilter : OncePerRequestFilter() {
    private val logger = LogFactory.getLog(CustomLoginRedirectionFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if ("/login" == httpRequest.requestURI) {
            if (authentication != null && authentication.isAuthenticated && authentication !is AnonymousAuthenticationToken) {
                logger.info("Redirecting to main ContextPath, The User is already authenticated.")
                httpResponse.sendRedirect("/")
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}