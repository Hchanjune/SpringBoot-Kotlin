package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserDTO
import com.kotlin.spring.management.dto.user.UserPasswordChangeForm
import com.kotlin.spring.management.repositories.mappers.user.UserBasicMapper
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserBasicService(
    private val userBasicMapper: UserBasicMapper,
    private val passwordEncoder: PasswordEncoder
) {

    val logger = LoggerFactory.getLogger(UserBasicService::class.java)

    fun authenticate(
        id: String,
        rawPassword: String
    ): ServiceResponse<Boolean> {
        if (!this.isUserExistsInDatabase(id).extractStatus(false)) {
            return ServiceResponse.simpleStatus(
                "User Authentication",
                { false },
                null,
                "아이디와 비밀번호를 다시 확인하세요."
            )
        }
        return ServiceResponse.simpleStatus(
            "User Authentication",
            { passwordEncoder.matches(rawPassword, this.getUserCredentialsById(id).extractData(false)) },
            "성공적으로 인증되었습니다.",
            "아이디와 비밀번호를 다시 확인하세요."
        )
    }

    fun isUserExistsInDatabase(id: String): ServiceResponse<Boolean> {
        return ServiceResponse.simpleStatus(
            "UserBasicService - isUserExistsInDatabase",
            { userBasicMapper.countUserId(id) > 0 },
            "$id 가 회원정보내에 존재합니다.",
            "$id 가 회원정보내에 존재하지 않습니다."
        )
    }

    fun getUserById(id: String): ServiceResponse<UserDTO> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserById",
            {
                userBasicMapper.selectUserById(id)?.apply {
                    this.roles = getUserRolesById(this.id).extractData()
                }
            },
            "유저 정보를 성공적으로 불러 왔습니다.",
            "유저 정보를 불러오는 도중 문제가 발생 하였습니다."
        )
    }

    fun getUserCredentialsById(id: String): ServiceResponse<String> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserCredentialsById",
            { userBasicMapper.selectUserCredentialsById(id) },
            "유저 비밀번호를 성공적으로 불러 왔습니다.",
            "유저 비밀번호를 불러오는 도중 문제가 발생 하였습니다."
        )

    }

    fun getUserRolesById(id: String): ServiceResponse<List<String>> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserRolesById",
            { userBasicMapper.selectUserRolesById(id) },
            "$id 유저의 권한 목록을 성공적으로 불러 왔습니다.",
            "$id 유저의 권한 목록을 불러오는데 실패 하였습니다."
        )
    }

    fun modifyUserPassword(passwordChangeForm: UserPasswordChangeForm, processingUtil: ProcessingUtil): ServiceResponse<Boolean> {
        var service = ServiceResponse<Boolean>("UserBasicService - modifyUserPassword")
        var encodedPassword: String;
        processingUtil.addFunction(
            "Authentication Check",
            { this.authenticate(passwordChangeForm.id, passwordChangeForm.currentPassword).extractStatus(false) },
            false
        ).let { authenticationCheck ->
            if (!authenticationCheck) {
                processingUtil.compile()
                return service.returnFalse<Boolean>("Authentication Check Failed")
            }
        }
        processingUtil.addFunction(
            "Double Check NewPassword ",
            {passwordChangeForm.newPassword == passwordChangeForm.newPasswordCheck},
            false
        ).let { passwordCheck ->
            if (!passwordCheck) {
                processingUtil.compile()
                return service.returnFalse<Boolean>("PasswordCheck Does Not Match New Password")
            } else {
                encodedPassword = processingUtil.addDataFunction(
                    "Encoding Password",
                    {passwordEncoder.encode(passwordChangeForm.newPassword)},
                    false
                )
                processingUtil.addFunction(
                    "Update Database",
                    { userBasicMapper.updateUserPassword(passwordChangeForm.id, encodedPassword) == 1 },
                    false
                ).let {updateDatabase ->
                    if (!updateDatabase) {
                        processingUtil.compile()
                        return service.returnFalse<Boolean>("Database Update Failed")
                    }
                    processingUtil.addFunction(
                        "Password Expiration Extend",
                        { this.extendUserPasswordExpiration(passwordChangeForm.id).extractStatus(false) },
                        false
                    )
                }
            }
        }
        return service.returnStatus(
            { processingUtil.compile() },
            "${passwordChangeForm.id} 유저의 비밀번호가 성공적으로 변경 되었습니다.",
            "${passwordChangeForm.id} 유저의 비밀번호 변경에 실패 하였습니다."
        )
    }

    fun extendUserPasswordExpiration(id: String): ServiceResponse<Boolean> {
        return ServiceResponse.simpleStatus(
            "UserBasicService - extendUserPasswordExpiration",
            {
                val process = userBasicMapper.upsertUserPasswordExpiration(id)
                process == 1 || process == 2 // MyBatis Integer Code {1= Insert, 2=Update}
            },
            "비밀번호 변경기간이 3개월 연장 되었습니다.",
            "비밀번호 변경기한 연장 중 문제가 발생하였습니다."
        )
    }



}