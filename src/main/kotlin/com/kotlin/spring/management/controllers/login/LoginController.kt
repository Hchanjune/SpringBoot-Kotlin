package com.kotlin.spring.management.controllers.login

import com.kotlin.spring.management.annotations.securityAnnotations.SecuritySample
import com.kotlin.spring.management.domains.common.apiResponse.ResponseVo
import com.kotlin.spring.management.domains.common.login.LoginRequest
import com.kotlin.spring.management.services.user.UserLoginServiceImpl
import com.kotlin.spring.management.utils.ResponseEntityGenerator.ResponseEntityGenerator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Tag(name = "LoginController", description = "LoginController")
@Controller
class LoginController(private val userLoginService: UserLoginServiceImpl) {
    private val logger = LoggerFactory.getLogger(LoginController::class.java)


    // Simple Login Page View
    @SecuritySample
    @GetMapping("login")
    fun loginPage(): String {
        return "loginPage"
    }


    // When 3 Month Passed From The Last Password Change Date
    @GetMapping ("securePassword")
    fun securePasswordPage(
        request: HttpServletRequest,
        redirectAttr: RedirectAttributes
    ): String {
        val flag = request.session.getAttribute("securePasswordRedirect") as Boolean
        if (flag == null || !flag) {
            redirectAttr.addFlashAttribute("resultMessage", "비정상적인 접근입니다.")
            return "redirect:/"
        }
        request.session.removeAttribute("securePasswordRedirect")
        request.session.setAttribute("extendPasswordExpiration", true)
        return "securePassword"
    }
















    /**
     *  이하 REST API
     */

    @Operation(summary = "API 로그인", description = "ID 와 PASSWORD 를 입력하여 로그인을 요청합니다. JWT 를 리턴받습니다.")
    @PostMapping(path = ["/api/login"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun apiLoginRequest(
        @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<ResponseVo> {
        logger.info(loginRequest.toString())
        val response: ResponseVo = userLoginService.apiLoginService(loginRequest.id, loginRequest.password).toApi()
        return ResponseEntityGenerator.generateResponse(response)
    }

    @Operation(summary = "Token Validation Check", description = "Jwt Token 이 정상일시 success 를 리턴합니다.")
    @PostMapping(path = ["/api/validate"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun apiTokenValidationCheck(): ResponseEntity<ResponseVo> {
        val response = ResponseVo(
            status = "success",
            message = "Verified Token",
            null,
            null
        )
        return ResponseEntityGenerator.generateResponse(response)
    }






}