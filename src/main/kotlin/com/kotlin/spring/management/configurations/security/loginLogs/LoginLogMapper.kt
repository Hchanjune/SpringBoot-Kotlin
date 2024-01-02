package com.kotlin.spring.management.configurations.security.loginLogs

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime

@Mapper
interface LoginLogMapper {
    @Insert("INSERT INTO user_login_logs (id, failureId, ip, device, status, company) VALUES(#{id}, #{failureId}, #{ip}, #{device}, #{status}, #{company})")
    fun insertLogInRecord(logInLog: LogInLogDTO): Int

    @Select("SELECT COALESCE(MAX(timestamp), CURRENT_TIMESTAMP) FROM user_login_logs WHERE id = #{id}")
    fun selectUserLastLogin(id: String): LocalDateTime
}