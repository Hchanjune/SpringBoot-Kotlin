package com.kotlin.spring.management

import com.kotlin.spring.management.dto.user.UserDTO
import com.kotlin.spring.management.dto.user.UserPasswordChangeForm
import com.kotlin.spring.management.dto.user.UserRegistrationForm
import com.kotlin.spring.management.repositories.mappers.user.UserBasicMapper
import com.kotlin.spring.management.services.user.UserBasicService
import com.kotlin.spring.management.services.user.UserCredentialsService
import com.kotlin.spring.management.services.user.UserRegistrationService
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ManagementApplicationTests() {


    @Autowired
    private lateinit var userRegistrationService: UserRegistrationService

    @Autowired
    private lateinit var userCredentialsService: UserCredentialsService

    @Autowired
    private lateinit var userBasicService: UserBasicService

    @Autowired
    private lateinit var userBasicMapper: UserBasicMapper

    @Test
    fun contextLoads() {

        val registrationForm: UserRegistrationForm = UserRegistrationForm(
            id = "admin5",
            password = "rkskekfkakqktk1@",
            passwordCheck = "rkskekfkakqktk1@",
            name = "어드민",
            company = "no",
            position = "no",
            phone = "01020002269",
            email = "example@example.com"
        )

        val processingUtil = ProcessingUtil("User Register Process")
        userRegistrationService.registerNewUser(processingUtil, registrationForm)
    }

    @Test
    fun test() {
        var userData: UserDTO = userBasicService.getUserById("admin").extractData()
    }

    @Test
    fun test2(){
        var userData: UserDTO = userBasicMapper.selectUserById("admin")
        println(userData.id)
    }

    @Test
    fun passwordChange() {
        val processingUtil = ProcessingUtil("User Update Password")
        val userPasswordChangeForm = UserPasswordChangeForm ("admin1123", "wjsguscks1@", "wjsguscks1@", "wjsguscks1@")
        userCredentialsService.modifyUserPassword(userPasswordChangeForm, processingUtil).extractStatus()
    }

    @Test
    fun extend(){
        userCredentialsService.extendUserPasswordExpiration("admin").extractStatus()
    }



}
