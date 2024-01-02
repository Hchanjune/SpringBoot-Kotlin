package com.kotlin.spring.management.repositories.mappers.user

import org.apache.ibatis.annotations.*
import java.time.LocalDateTime

@Mapper
interface UserCredentialsMapper {

    @Select("SELECT password FROM user_credentials WHERE id = #{id}")
    fun selectUserCredentialsById(id: String): String

    @Select(" SELECT role FROM user_roles WHERE id = #{id}")
    fun selectUserRolesById(id: String): List<String>

    @Update("UPDATE user_credentials SET password = #{encodedPassword} WHERE id = #{id}")
    fun updateUserPassword(@Param("id") id: String, @Param("encodedPassword") encodedPassword: String): Int

    @Update("INSERT INTO user_password_expiration (id, expirationDate) " +
            "VALUES (#{id}, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 MONTH)) " +
            "ON DUPLICATE KEY UPDATE " +
            "expirationDate = DATE_ADD(expirationDate, INTERVAL 3 MONTH)")
    fun upsertUserPasswordExpiration(id: String): Int

    @Select("SELECT expirationDate FROM user_password_expiration WHERE id = #{id}")
    fun selectUserPasswordExpiration(id: String): LocalDateTime

    @Update("UPDATE users SET certification = #{flag} WHERE id = #{id}")
    fun updateUserCertification(@Param("id") id: String, @Param("flag") flag: Boolean): Int

    @Insert("INSERT INTO user_roles (id, authority) VALUES(#{id}, #{role})")
    fun insertUserRole(@Param("id") id: String, @Param("role") role: String): Int

    @Select("SELECT COUNT(*) FROM user_roles WHERE authority = #{role} AND id = #{id}")
    fun countUserRole(@Param("id") id: String, @Param("role") role: String): Int

    @Delete("DELETE FROM user_roles WHERE authority = #{role} AND id = #{id}")
    fun deleteUserRole(@Param("id") id: String, @Param("role") role: String): Int














}