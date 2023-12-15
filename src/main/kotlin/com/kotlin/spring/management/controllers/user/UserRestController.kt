package com.kotlin.spring.management.controllers.user

import com.kotlin.spring.management.domains.common.apiResponse.ResponseVo
import com.kotlin.spring.management.services.user.UserBasicService
import com.kotlin.spring.management.utils.ResponseEntityGenerator.ResponseEntityGenerator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "UserRestController", description = "deals with basic user options")
@RestController
@RequestMapping("/api/user")
class UserRestController(
    private val userBasicService: UserBasicService
) {

    @Operation(summary = "GetUserInfoById", description = "ID에 맞는 유저 정보를 불러옵니다.")
    @Parameter(name = "id", description = "유저 아이디")
    @GetMapping("/get/userInfo/{id}")
    fun getUserInfo(@PathVariable id: String): ResponseEntity<ResponseVo> {
        return ResponseEntityGenerator.generateResponse(userBasicService.getUserById(id).toApi())
    }


}
