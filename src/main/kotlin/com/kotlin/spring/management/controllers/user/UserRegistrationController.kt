package com.kotlin.spring.management.controllers.user

import com.kotlin.spring.management.domains.common.apiResponse.ResponseVo
import com.kotlin.spring.management.dto.user.UserRegistrationForm
import com.kotlin.spring.management.services.user.UserBasicService
import com.kotlin.spring.management.services.user.UserCommonService
import com.kotlin.spring.management.services.user.UserRegistrationServiceImpl
import com.kotlin.spring.management.utils.ProcessingUtil.ProcessingUtil
import com.kotlin.spring.management.utils.ResponseEntityGenerator.ResponseEntityGenerator
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/user/register")
class UserRegistrationController(
    private val userCommonService: UserCommonService,
    private val userRegistrationService: UserRegistrationServiceImpl
) {


    @GetMapping
    fun registrationPage(): String {
        return "signIn"
    }

    @PostMapping("/register")
    fun registerUserAction(
        @ModelAttribute registrationForm: UserRegistrationForm,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttr: RedirectAttributes
    ): String {
        val processingUtil = ProcessingUtil("User Register Process")
        val process = userRegistrationService.registerNewUser(processingUtil, registrationForm)
        redirectAttr.addFlashAttribute("resultMessage", process.message)
        return "redirect:/loginPage"
    }

    @GetMapping("/exists/{id}")
    @ResponseBody
    fun isIdDuplicated(
        @PathVariable(value = "id") id: String,
    ): ResponseEntity<ResponseVo> {
        val response = userCommonService.isUserExistsInDatabase(id).toApi()
        return ResponseEntityGenerator.generateResponse(response)
    }

}