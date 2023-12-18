package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserPasswordChangeForm
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import java.time.LocalDateTime

interface UserCredentialsService {

    // 인증
    fun authenticateCredentials(id: String, rawPassword: String): ServiceResponse<Boolean>

    // 유저 비밀번호
    fun getUserCredentialsById(id: String): ServiceResponse<String>

    // 유저 권한
    fun getUserRolesById(id: String): ServiceResponse<List<String>>

    // 유저 비밀번호 변경
    fun modifyUserPassword(passwordChangeForm: UserPasswordChangeForm, processingUtil: ProcessingUtil): ServiceResponse<Boolean>

    // 유저 비밀번호 만료기간 연장
    fun extendUserPasswordExpiration(id: String): ServiceResponse<Boolean>

    // 유저 비밀번호 만료 여부 확인
    fun isUserPasswordExpired(id: String): ServiceResponse<Boolean>

    // 유저 비밀번호 만료일자
    fun getUserPasswordExpirationDate(id: String): ServiceResponse<LocalDateTime>

    // 유저 인증 상태 변경
    fun setUserCertification(id: String, flag: Boolean): ServiceResponse<Boolean>

    // 유저 인증상태 true + 권한 Worker 이상으로 변경
    fun setUserCertificationAvailable(id: String): ServiceResponse<Boolean>

    // 유저 인증상태 false + 권한 guest 변경
    fun setUserCertificationUnavailable(id: String): ServiceResponse<Boolean>

    fun processDormantAccounts(): ServiceResponse<String>


    fun hasRoleAdmin(id: String): ServiceResponse<Boolean>
    fun hasRoleManager(id: String): ServiceResponse<Boolean>
    fun hasRoleWorker(id: String): ServiceResponse<Boolean>
    fun hasRoleGuest(id: String): ServiceResponse<Boolean>


    fun addRoleAdmin(id: String): ServiceResponse<Boolean>
    fun addRoleManager(id: String): ServiceResponse<Boolean>
    fun addRoleWorker(id: String): ServiceResponse<Boolean>
    fun addRoleGuest(id: String): ServiceResponse<Boolean>


    fun removeRoleAdmin(id: String): ServiceResponse<Boolean>
    fun removeRoleManager(id: String): ServiceResponse<Boolean>
    fun removeRoleWorker(id: String): ServiceResponse<Boolean>
    fun removeRoleGuest(id: String): ServiceResponse<Boolean>
    fun removeAllRolesExceptGuest(id: String): ServiceResponse<Boolean>
















}