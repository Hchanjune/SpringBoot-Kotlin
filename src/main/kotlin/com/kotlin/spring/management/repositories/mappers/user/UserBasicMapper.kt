package com.kotlin.spring.management.repositories.mappers.user

import com.kotlin.spring.management.dto.user.UserDTO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

@Mapper
interface UserBasicMapper {

    @Select("SELECT password FROM user_credentials WHERE id = #{id}")
    fun selectUserCredentialsById(id: String): String

    @Select(" SELECT COUNT(id) FROM users WHERE id = #{id}")
    fun countUserId(id: String): Int

    @Select(" SELECT role FROM user_roles WHERE id = #{id}")
    fun selectUserRolesById(id: String): List<String>

    @Select(" SELECT id, name, company, position, phone, email, inserted, certification FROM users WHERE id = #{id}")
    fun selectUserById(id: String): UserDTO



    @Update("UPDATE user_credentials SET password = #{encodedPassword} WHERE id = #{id}")
    fun updateUserPassword(@Param("id") id: String, @Param("encodedPassword") encodedPassword: String): Int


    @Update("INSERT INTO user_password_expiration (id, expirationDate) " +
            "VALUES (#{id}, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 MONTH)) " +
            "ON DUPLICATE KEY UPDATE " +
            "expirationDate = DATE_ADD(expirationDate, INTERVAL 3 MONTH)")
    fun upsertUserPasswordExpiration(id: String): Int

}