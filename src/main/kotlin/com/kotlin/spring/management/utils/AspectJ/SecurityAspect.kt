package com.kotlin.spring.management.utils.AspectJ

import com.kotlin.spring.management.services.user.UserCredentialsService
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class SecurityAspect(
    userCredentialsService: UserCredentialsService
) {

    val logger = LoggerFactory.getLogger(SecurityAspect::class.java)

    @Before("@annotation(com.kotlin.spring.management.annotations.securityAnnotations.SecuritySample)")
    fun securitySample (){
        val attrs = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        val request: HttpServletRequest = attrs.request
        logger.info("This Function Is About Security Sample\nRequest URI : ${request.requestURI}")
    }


}