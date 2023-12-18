package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserDTO
import com.kotlin.spring.management.dto.user.UserPasswordChangeForm
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import java.time.LocalDateTime

interface UserBasicService {

    fun getUserById(id: String): ServiceResponse<UserDTO>

    fun getUserListAll(): ServiceResponse<List<UserDTO>>

    fun isUserExistsInDatabase(id: String): ServiceResponse<Boolean>

}