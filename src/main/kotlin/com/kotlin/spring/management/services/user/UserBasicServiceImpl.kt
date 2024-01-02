package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserDTO
import com.kotlin.spring.management.dto.user.UserPasswordChangeForm
import com.kotlin.spring.management.repositories.mappers.user.UserBasicMapper
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserBasicServiceImpl(
    private val userCredentialsService: UserCredentialsService,
    private val userBasicMapper: UserBasicMapper
) : UserBasicService {

    val logger = LoggerFactory.getLogger(UserBasicServiceImpl::class.java)




}