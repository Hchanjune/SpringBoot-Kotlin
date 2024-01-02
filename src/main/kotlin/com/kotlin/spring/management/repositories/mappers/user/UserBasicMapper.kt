package com.kotlin.spring.management.repositories.mappers.user

import com.kotlin.spring.management.dto.user.UserDTO
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import java.time.LocalDateTime

@Mapper
interface UserBasicMapper {



/*    @Select(" SELECT id, name, company, position, phone, email, inserted, certification FROM users WHERE id = #{id}")
    fun selectUserById(id: String): UserDTO*/

}