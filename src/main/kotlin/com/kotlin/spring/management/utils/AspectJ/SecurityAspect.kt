package com.kotlin.spring.management.utils.AspectJ

import com.kotlin.spring.management.services.user.UserCredentialsService
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder

@Aspect
@Component
class SecurityAspect(
    userCredentialsService: UserCredentialsService
) {

    /**
     *
     *
     *
     */


/*    @Before("")
    fun CODE_A (){
        val attrs = RequestContextHolder.getRequestAttributes() as ServletServerHttpRequest
        val request: HttpServletRequest? = attrs?.servletRequest
    }*/


}