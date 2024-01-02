package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserDTO
import com.kotlin.spring.management.repositories.mappers.user.UserCommonMapper
import com.kotlin.spring.management.repositories.mappers.user.UserCredentialsMapper
import org.springframework.stereotype.Service

@Service
class UserCommonServiceImpl(
    private val userCommonMapper: UserCommonMapper,
    private val userCredentialsMapper: UserCredentialsMapper
): UserCommonService {

    override fun isUserExistsInDatabase(id: String): ServiceResponse<Boolean> {
        return ServiceResponse.simpleStatus(
            "UserBasicService - isUserExistsInDatabase",
            { userCommonMapper.countUserId(id) > 0 },
            "$id 가 회원정보내에 존재합니다.",
            "$id 가 회원정보내에 존재하지 않습니다."
        )
    }

    override fun getUserById(id: String): ServiceResponse<UserDTO> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserById",
            {
                userCommonMapper.selectUserById(id)?.apply {
                    this.roles = userCredentialsMapper.selectUserRolesById(this.id)
                }
            },
            "유저 $id 의 정보를 성공적으로 불러 왔습니다.",
            "유저 정보를 불러오는 도중 문제가 발생 하였습니다."
        )
    }

    override fun getUserListAll(): ServiceResponse<List<UserDTO>> {
        return ServiceResponse.generateData(
            "UserBasicService - getUserListAll",
            { userCommonMapper.selectUserListAll() },
            "유저 목록을 성공적으로 불러 왔습니다.",
            "유저 목록을 불러오는 도중 문제가 발생 하였습니다."
        )
    }

}