package com.kotlin.spring.management.dto.user

import com.kotlin.spring.management.annotations.NoArgsConstructor
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@NoArgsConstructor
data class UserDTO(
    var id: String = "",
    var name: String = "",
    var company: String? = null,
    var position: String? = null,
    var phone: String = "",
    var email: String = "",
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var inserted: LocalDateTime? = null,
    var certification: Boolean = false,
    var roles: List<String> = listOf(""),
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var lastLogin: LocalDateTime? = null
)
