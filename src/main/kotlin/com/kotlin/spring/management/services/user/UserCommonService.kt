package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserDTO

interface UserCommonService {

    fun isUserExistsInDatabase(id: String): ServiceResponse<Boolean>

    fun getUserById(id: String): ServiceResponse<UserDTO>

    fun getUserListAll(): ServiceResponse<List<UserDTO>>

}