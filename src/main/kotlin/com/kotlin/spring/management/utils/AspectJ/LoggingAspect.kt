package com.kotlin.spring.management.utils.AspectJ

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {

    val logger = LoggerFactory.getLogger(LoggingAspect::class.java)


    @Before("execution(* com.kotlin.spring.management.*.*(..))")
    fun logBeforeMethod(joinPoint: JoinPoint){
        println("Method Call: ${joinPoint.signature.name}")
        logger.info("Method Call: ${joinPoint.signature.name}")
    }

}