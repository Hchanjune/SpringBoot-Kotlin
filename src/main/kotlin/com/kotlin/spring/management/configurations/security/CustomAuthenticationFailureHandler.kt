package com.kotlin.spring.management.configurations.security

import com.kotlin.spring.management.services.user.UserBasicService
import com.kotlin.spring.management.services.user.UserCommonService
import com.kotlin.spring.management.utils.LogUtils.LogUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationFailureHandler(
    private val logUtils: LogUtils,
    private val userCommonService: UserCommonService
): AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException?
    ) {
        val attemptedId: String = request.getParameter("username")
        when (userCommonService.isUserExistsInDatabase(attemptedId).extractData()){
            true -> {
                logUtils.recordLoginFailureLog(request, userCommonService.getUserById(attemptedId).extractData())
            }
            false -> {
                logUtils.recordAnonymousLoginAttempt(request, attemptedId)
            }
        }
        response.sendRedirect("/login")
    }
}