package com.kotlin.spring.management.configurations.security

import com.kotlin.spring.management.configurations.security.userDetails.CustomUserDetails
import com.kotlin.spring.management.services.user.UserBasicService
import com.kotlin.spring.management.services.user.UserCredentialsService
import com.kotlin.spring.management.utils.LogUtils.LogUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val userCredentialsService: UserCredentialsService,
    private val logUtils: LogUtils
): AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // LoginLog Record
        logUtils.recordLoginSuccessLog(request, authentication.principal as CustomUserDetails)

        // PasswordChangeDateCheck
        val isUserPasswordExpired = userCredentialsService.isUserPasswordExpired((authentication.principal as CustomUserDetails).id).extractStatus()

        // Configure LoginSuccess Response URL
        if (isUserPasswordExpired){
            // Expired URL
            response.sendRedirect(request.contextPath + "/")
        } else {
            // Main
            response.sendRedirect(request.contextPath + "/")
        }

    }

}