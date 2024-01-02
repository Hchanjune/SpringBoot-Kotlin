package com.kotlin.spring.management.configurations.security.loginLogs

import com.kotlin.spring.management.annotations.systemCommon.NoArgsConstructor
import java.time.LocalDateTime

@NoArgsConstructor
data class LogInLogDTO(
    var record: Int? = null,
    var id: String? = null,
    var failureId: String? = null,
    var timestamp: LocalDateTime? = LocalDateTime.now(),
    var ip: String,
    var device: String,
    var status: String,
    var company: String? = null
)