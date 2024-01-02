package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse

interface UserLoginService{

    // API Login
    fun apiLoginService(
        id: String,
        rawPassword: String
    ): ServiceResponse<String>

}

