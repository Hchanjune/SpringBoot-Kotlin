package com.kotlin.spring.management.services.user

import com.kotlin.spring.management.domains.common.ServiceResponse
import com.kotlin.spring.management.dto.user.UserPasswordChangeForm
import com.kotlin.spring.management.repositories.mappers.user.UserCredentialsMapper
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserCredentialsServiceImpl(
    private val userCommonService: UserCommonService,
    private val userCredentialsMapper: UserCredentialsMapper,
    private val passwordEncoder: PasswordEncoder
)
    : UserCredentialsService {

    val logger = LoggerFactory.getLogger(UserCredentialsServiceImpl::class.java)



    override fun authenticateCredentials(
        id: String,
        rawPassword: String
    ): ServiceResponse<Boolean> {
        if (!userCommonService.isUserExistsInDatabase(id).extractStatus(false)) {
            return ServiceResponse.simpleStatus(
                "User Authentication",
                { false },
                null,
                "아이디와 비밀번호를 다시 확인하세요."
            )
        }
        return ServiceResponse.simpleStatus(
            "UserCredentialsService - User Authentication",
            { passwordEncoder.matches(rawPassword, this.getUserCredentialsById(id).extractData(false)) },
            "성공적으로 인증되었습니다.",
            "아이디와 비밀번호를 다시 확인하세요."
        )
    }

    override fun getUserCredentialsById(id: String): ServiceResponse<String> {
        return ServiceResponse.generateData(
            "UserCredentialsService - getUserCredentialsById",
            { userCredentialsMapper.selectUserCredentialsById(id) },
            "유저 비밀번호를 성공적으로 불러 왔습니다.",
            "유저 비밀번호를 불러오는 도중 문제가 발생 하였습니다."
        )
    }

    override fun getUserRolesById(id: String): ServiceResponse<List<String>> {
        return ServiceResponse.generateData(
            "UserCredentialsService - getUserRolesById",
            { userCredentialsMapper.selectUserRolesById(id) },
            "$id 유저의 권한 목록을 성공적으로 불러 왔습니다.",
            "$id 유저의 권한 목록을 불러오는데 실패 하였습니다."
        )
    }

    override fun modifyUserPassword(passwordChangeForm: UserPasswordChangeForm, processingUtil: ProcessingUtil): ServiceResponse<Boolean> {
        var service = ServiceResponse<Boolean>("UserCredentialsService - modifyUserPassword")
        var encodedPassword: String;
        processingUtil.addFunction(
            "Authentication Check",
            { this.authenticateCredentials(passwordChangeForm.id, passwordChangeForm.currentPassword).extractStatus(false) },
            false
        ).let { authenticationCheck ->
            if (!authenticationCheck) {
                processingUtil.compile()
                return service.returnFalse("Authentication Check Failed")
            }
        }
        processingUtil.addFunction(
            "Double Check NewPassword ",
            {passwordChangeForm.newPassword == passwordChangeForm.newPasswordCheck},
            false
        ).let { passwordCheck ->
            if (!passwordCheck) {
                processingUtil.compile()
                return service.returnFalse("PasswordCheck Does Not Match New Password")
            } else {
                encodedPassword = processingUtil.addDataFunction(
                    "Encoding Password",
                    {passwordEncoder.encode(passwordChangeForm.newPassword)},
                    false
                )
                processingUtil.addFunction(
                    "Update Database",
                    { userCredentialsMapper.updateUserPassword(passwordChangeForm.id, encodedPassword) == 1 },
                    false
                ).let {updateDatabase ->
                    if (!updateDatabase) {
                        processingUtil.compile()
                        return service.returnFalse("Database Update Failed")
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

    override fun extendUserPasswordExpiration(id: String): ServiceResponse<Boolean> {
        return ServiceResponse.simpleStatus(
            "UserCredentialsService - extendUserPasswordExpiration",
            {
                val process = userCredentialsMapper.upsertUserPasswordExpiration(id)
                process == 1 || process == 2 // MyBatis Integer Code {1= Insert, 2=Update}
            },
            "비밀번호 변경기간이 3개월 연장 되었습니다.",
            "비밀번호 변경기한 연장 중 문제가 발생하였습니다."
        )
    }

    override fun isUserPasswordExpired(id: String) : ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - isUserPasswordExpired")
        return response.returnStatus(
            { this.getUserPasswordExpirationDate(id).extractData().isBefore(LocalDateTime.now()) },
            "$id 유저의 비밀번호 만료 여부를 성공적으로 조회 하였습니다.",
            "비밀번호 만료 여부 조회 중 오류가 발생하였습니다."
        )
    }

    override fun getUserPasswordExpirationDate(id: String) : ServiceResponse<LocalDateTime> {
        val response = ServiceResponse<LocalDateTime>("UserCredentialsService - getUserPasswordExpirationDate")
        return response.returnData(
            {userCredentialsMapper.selectUserPasswordExpiration(id)},
            "$id 유저의 비밀번호 유효기간을 성공적으로 조회 하였습니다.",
            "비밀번호 유효기간 조회 중 오류가 발생하였습니다."
        )
    }


    override fun processDormantAccounts(): ServiceResponse<String> {
        val response = ServiceResponse<String>("UserCredentialsService - processDormantAccounts (DailySchedule)")
        val sixMonthAgo = LocalDateTime.now().minusMonths(6)
        val userList = userCommonService.getUserListAll().extractData()

        // Users That Have Logged In At Least Once
        val userIdList = userList
            .filter { it.lastLogin != null && it.lastLogin?.isBefore(sixMonthAgo) == true }
            .map { it.id }

        // Users That Have Never Logged In
        val userIdListNeverLoggedIn = userList
            .filter { it.lastLogin == null && it.inserted?.isBefore(sixMonthAgo) == true }
            .map { it.id }

        val combinedUserIdList: List<String> = (userIdList + userIdListNeverLoggedIn).distinct()

        for (id in combinedUserIdList){
            this.setUserCertification(id, false)
        }
        return response.returnData(
            { "총 ${combinedUserIdList.size} 명의 유저가 휴면 처리 되었습니다.\n자세한 아이디 목록은 다음과 같습니다.\n${combinedUserIdList}" },
            "휴면 계정이 정상적으로 처리 되었습니다.",
            "휴면 계정처리 도중 문제가 발생하였습니다."
        )
    }



    override fun setUserCertification(id: String, flag: Boolean): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - setUserCertification")
        return response.returnStatus(
            { userCredentialsMapper.updateUserCertification(id, flag) == 1},
            "$id 유저의 인증상태가 $flag 로 성공적으로 변경되었습니다.",
            "유저 인증상태 변경 중 오류가 발생하였습니다."
        )
    }

    @Transactional
    override fun setUserCertificationAvailable(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - setUserCertificationAvailable")
        return response.returnStatus(
            {
                val updateCertTrue = userCredentialsMapper.updateUserCertification(id, true) == 1
                val addRoleWorker = this.addRoleGuest(id).extractStatus()
                updateCertTrue && addRoleWorker
            },
            "$id 유저의 인증상태가 true 로변경, 권한이 ROLE_WORKER 가 성공적으로 추가되었습니다.",
            "유저 인증상태 변경 중 오류가 발생하였습니다."
        )
    }

    @Transactional
    override fun setUserCertificationUnavailable(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - setUserCertificationUnavailable")
        return response.returnStatus(
            {
                val removeRoles = this.removeAllRolesExceptGuest(id).extractStatus()
                val updateCertFalse = userCredentialsMapper.updateUserCertification(id, false) == 1
                removeRoles && updateCertFalse
            },
            "$id 유저의 인증상태가 false, 권한이 ROLE_GUEST 이외의 권한이 성공적으로 제거되었습니다.",
            "유저 인증상태 변경 중 오류가 발생하였습니다."
        )
    }






    override fun hasRoleAdmin(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - hasRoleAdmin")
        val role = "ROLE_ADMIN"
        return response.returnStatus(
            { userCredentialsMapper.countUserRole(id, role) == 1},
            "$id 유저의 ROLE_ADMIN 권한이 조회 되었습니다.",
            "유저 권한 조회 중 오류가 발생하였습니다."
        )
    }

    override fun hasRoleManager(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - hasRoleManager")
        val role = "ROLE_MANAGER"
        return response.returnStatus(
            { userCredentialsMapper.countUserRole(id, role) == 1},
            "$id 유저의 ROLE_MANAGER 권한이 조회 되었습니다.",
            "유저 권한 조회 중 오류가 발생하였습니다."
        )
    }

    override fun hasRoleWorker(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - hasRoleWorker")
        val role = "ROLE_WORKER"
        return response.returnStatus(
            { userCredentialsMapper.countUserRole(id, role) == 1},
            "$id 유저의 ROLE_WORKER 권한이 조회 되었습니다.",
            "유저 권한 조회 중 오류가 발생하였습니다."
        )
    }

    override fun hasRoleGuest(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - hasRoleGuest")
        val role = "ROLE_GUEST"
        return response.returnStatus(
            { userCredentialsMapper.countUserRole(id, role) == 1},
            "$id 유저의 ROLE_GUEST 권한이 조회 되었습니다.",
            "유저 권한 조회 중 오류가 발생하였습니다."
        )
    }






    override fun addRoleAdmin(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - addRoleAdmin")
        val role = "ROLE_ADMIN"
        if (this.hasRoleAdmin(id).extractStatus()) {
            return response.returnFalse("이미 Admin 권한을 가지고 있습니다.")
        }
        return response.returnStatus(
            { userCredentialsMapper.insertUserRole(id, role) == 1 },
            "$id 유저의 권한에 $role 이 성공적으로 추가되었습니다.",
            "유저 권한 추가 중 오류가 발생하였습니다."
        )
    }

    override fun addRoleManager(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - addRoleManager")
        val role = "ROLE_MANAGER"
        if (this.hasRoleManager(id).extractStatus()) {
            return response.returnFalse("이미 Manager 권한을 가지고 있습니다.")
        }
        return response.returnStatus(
            { userCredentialsMapper.insertUserRole(id, role) == 1 },
            "$id 유저의 권한에 $role 이 성공적으로 추가되었습니다.",
            "유저 권한 추가 중 오류가 발생하였습니다."
        )
    }

    override fun addRoleWorker(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - addRoleWorker")
        val role = "ROLE_WORKER"
        if (this.hasRoleWorker(id).extractStatus()) {
            return response.returnFalse("이미 Worker 권한을 가지고 있습니다.")
        }
        return response.returnStatus(
            { userCredentialsMapper.insertUserRole(id, role) == 1 },
            "$id 유저의 권한에 $role 이 성공적으로 추가되었습니다.",
            "유저 권한 추가 중 오류가 발생하였습니다."
        )
    }

    override fun addRoleGuest(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - addRoleGuest")
        val role = "ROLE_GUEST"
        if (this.hasRoleGuest(id).extractStatus()) {
            return response.returnFalse("이미 Guest 권한을 가지고 있습니다.")
        }
        return response.returnStatus(
            { userCredentialsMapper.insertUserRole(id, role) == 1 },
            "$id 유저의 권한에 $role 이 성공적으로 추가되었습니다.",
            "유저 권한 추가 중 오류가 발생하였습니다."
        )
    }






    override fun removeRoleAdmin(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - removeRoleAdmin")
        val role = "ROLE_ADMIN"
        return if (this.hasRoleAdmin(id).extractStatus()){
            response.returnStatus(
                { userCredentialsMapper.deleteUserRole(id, role) == 1 },
                "$id 유저의 권한목록에서 $role 이 성공적으로 제거되었습니다.",
                "유저 권한 제거 중 오류가 발생하였습니다."
            )
        } else {
            response.returnData({true})
        }
    }

    override fun removeRoleManager(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - removeRoleManager")
        val role = "ROLE_MANAGER"
        return if (this.hasRoleManager(id).extractStatus()) {
            response.returnStatus(
                { userCredentialsMapper.deleteUserRole(id, role) == 1 },
                "$id 유저의 권한목록에서 $role 이 성공적으로 제거되었습니다.",
                "이미 해당 권한이 없거나, 유저 권한 제거 중 오류가 발생하였습니다."
            )
        } else {
            response.returnData({true})
        }
    }

    override fun removeRoleWorker(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - removeRoleWorker")
        val role = "ROLE_WORKER"
        return if (this.hasRoleWorker(id).extractStatus()){
            response.returnStatus(
                { userCredentialsMapper.deleteUserRole(id, role) == 1 },
                "$id 유저의 권한목록에서 $role 이 성공적으로 제거되었습니다.",
                "이미 해당 권한이 없거나, 유저 권한 제거 중 오류가 발생하였습니다."
            )
        } else {
            response.returnData({true})
        }
    }

    override fun removeRoleGuest(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialsService - removeRoleGuest")
        val role = "ROLE_GUEST"
        return if (this.hasRoleGuest(id).extractStatus()) {
            response.returnStatus(
                { userCredentialsMapper.deleteUserRole(id, role) == 1 },
                "$id 유저의 권한목록에서 $role 이 성공적으로 제거되었습니다.",
                "이미 해당 권한이 없거나, 유저 권한 제거 중 오류가 발생하였습니다."
            )
        } else {
            response.returnData({true})
        }
    }

    override fun removeAllRolesExceptGuest(id: String): ServiceResponse<Boolean> {
        val response = ServiceResponse<Boolean>("UserCredentialService - removeAllRolesExceptGuest")
        val removeAdmin = this.removeRoleAdmin(id).extractStatus()
        val removeManager = this.removeRoleManager(id).extractStatus()
        val removeWorker = this.removeRoleWorker(id).extractStatus()
        return if (removeAdmin && removeManager && removeWorker) {
            response.returnData({true})
        } else {
            response.returnFalse()
        }
    }


}