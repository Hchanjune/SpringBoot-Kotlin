package com.kotlin.spring.management.configurations.security.loginLogs

import com.kotlin.spring.management.domains.common.ServiceResponse
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LoginLogService(private val loginLogMapper: LoginLogMapper) {

    fun recordLoginLogs(logInLog: LogInLogDTO): ServiceResponse<Boolean>{
        return ServiceResponse.simpleStatus(
            "LoginLog-Service - RecordLoginLogs",
            {
                loginLogMapper.insertLogInRecord(logInLog) == 1
            },
            "로그인 로그가 성공적으로 기록되었습니다.",
            "로그인 로그 기록 중 오류가 발생하였습니다."
        )
    }

    fun getUserLastLogin(id: String): ServiceResponse<LocalDateTime> {
        return ServiceResponse.generateData(
            "LoginLogService - getUserLastLogin",
            { loginLogMapper.selectUserLastLogin(id) },
            "$id 의 마지막 로그인 기록을 불러왔습니다.",
            "$id 의 마지막 로그인 기록을 불러오는데에 실패했습니다."
        )
    }


}