package com.kotlin.spring.management.configurations.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider(private val customUserDetailService: CustomUserDetailService, private val passwordEncoder: PasswordEncoder) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        val id : String = authentication.name
        val password : String = authentication.credentials.toString()
        val userDetails : UserDetails = customUserDetailService.loadUserByUsername(id)

        if (!passwordEncoder.matches(password, userDetails.password)) throw BadCredentialsException("Bad Credentials")

        return UsernamePasswordAuthenticationToken(
            userDetails,
            password,
            userDetails.authorities
        )
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}