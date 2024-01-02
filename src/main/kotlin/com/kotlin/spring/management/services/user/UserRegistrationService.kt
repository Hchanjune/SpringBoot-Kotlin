package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserRegistrationForm
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.springframework.transaction.annotation.Transactional

interface UserRegistrationService {
    @Transactional
    fun registerNewUser(
        processingUtil: ProcessingUtil,
        registrationForm: UserRegistrationForm
    ): ServiceResponse<Boolean>

    fun validateUserRegistrationForm(
        processingUtil: ProcessingUtil,
        registrationForm: UserRegistrationForm
    ): ServiceResponse<Boolean>
}