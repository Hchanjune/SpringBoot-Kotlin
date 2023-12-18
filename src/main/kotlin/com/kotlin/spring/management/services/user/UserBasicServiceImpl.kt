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

    override fun isUserExistsInDatabase(id: String): ServiceResponse<Boolean> {
        return ServiceResponse.simpleStatus(
            "UserBasicService - isUserExistsInDatabase",
            { userBasicMapper.countUserId(id) > 0 },
            "$id 가 회원정보내에 존재합니다.",
            "$id 가 회원정보내에 존재하지 않습니다."
        )
    }

    override fun getUserById(id: String): ServiceResponse<UserDTO> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserById",
            {
                userBasicMapper.selectUserById(id)?.apply {
                    this.roles = userCredentialsService.getUserRolesById(this.id).extractData()
                }
            },
            "유저 $id 의 정보를 성공적으로 불러 왔습니다.",
            "유저 정보를 불러오는 도중 문제가 발생 하였습니다."
        )
    }

    override fun getUserListAll(): ServiceResponse<List<UserDTO>> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserListAll",
            { userBasicMapper.selectUserListAll() },
            "유저 목록을 성공적으로 불러 왔습니다.",
            "유저 목록을 불러오는 도중 문제가 발생 하였습니다."
        )
    }


}