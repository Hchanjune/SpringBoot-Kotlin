package com.kotlin.spring.management.dto.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "비밀번호 변경 Form")
data class UserPasswordChangeForm(
    @Schema(description = "아이디")
    var id: String,
    @Schema(description = "현재 평문 비밀번호")
    var currentPassword: String,
    @Schema(description = "변경할 평문 비밀번호")
    var newPassword : String,
    @Schema(description = "변경할 평문 비밀번호 확인")
    var newPasswordCheck: String
)
