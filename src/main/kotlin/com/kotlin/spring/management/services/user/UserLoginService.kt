package com.kotlin.spring.management.services.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlin.spring.management.configurations.security.jwt.JwtProvider
import com.kotlin.spring.management.domains.common.ServiceResponse
import org.springframework.stereotype.Service

@Service
class UserLoginService(
    private val userBasicService: UserBasicService,
    private val jwtProvider: JwtProvider
) {


    fun apiLoginService(
        id: String,
        rawPassword: String
    ): ServiceResponse<String> {
        return if (userBasicService.authenticate(id, rawPassword).extractStatus()) {
            val jwtTokenMap: MutableMap<String, String> = mutableMapOf()
            jwtTokenMap["accessToken"] = jwtProvider.generateAccessToken(id)
            jwtTokenMap["refreshToken"] = jwtProvider.generateRefreshToken(id)

            val objectMapper = ObjectMapper()
            val returnToken = objectMapper.writeValueAsString(jwtTokenMap)

            ServiceResponse.generateData(
                "JWT Generation",
                { returnToken },
                "Login Successful, JWT generated without String Bearer"
            )
        } else {
            ServiceResponse.simpleError("Invalid Credentials")
        }
    }














}