package com.kotlin.spring.management.repositories.mappers.user

import com.kotlin.spring.management.dto.user.UserDTO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.time.LocalDateTime

@Mapper
interface UserBasicMapper {

    @Select("SELECT usr.id, usr.name, usr.company, usr.position, usr.phone, usr.email, usr.inserted, usr.certification, " +
            "COALESCE(MAX(log.timestamp), CURRENT_TIMESTAMP) AS lastLogin " +
            "FROM users usr " +
            "LEFT JOIN user_login_logs log ON usr.id = log.id " +
            "GROUP BY usr.id, usr.name, usr.company, usr.position, usr.phone, usr.email, usr.inserted, usr.certification")
    fun selectUserListAll(): List<UserDTO>

    @Select(" SELECT COUNT(id) FROM users WHERE id = #{id}")
    fun countUserId(id: String): Int

    @Select("SELECT usr.id, usr.name, usr.company, usr.position, usr.phone, usr.email, usr.inserted, usr.certification, " +
            "COALESCE(MAX(log.timestamp), CURRENT_TIMESTAMP) AS lastLogin " +
            "FROM users usr " +
            "LEFT JOIN user_login_logs log ON usr.id = log.id " +
            "WHERE usr.id = #{id} " +
            "GROUP BY usr.id, usr.name, usr.company, usr.position, usr.phone, usr.email, usr.inserted, usr.certification")
    fun selectUserById(id: String): UserDTO

/*    @Select(" SELECT id, name, company, position, phone, email, inserted, certification FROM users WHERE id = #{id}")
    fun selectUserById(id: String): UserDTO*/

}