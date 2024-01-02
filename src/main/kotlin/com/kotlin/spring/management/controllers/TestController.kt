package com.kotlin.spring.management.controllers

import com.kotlin.spring.management.dto.user.UserDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class TestController() {

    companion object {
        private val logger = LoggerFactory.getLogger(TestController::class.java)
    }

    @GetMapping("/test")
    fun testPage(model:Model) : String {
        logger.info("Hello World!")

        val user = UserDTO(
            id= "userId",
            name = "userName",
            company = "company",
            position = "po",
            phone = "010101010",
            email = "123",
            inserted = null
        )


        model.addAttribute("model", "HELLO WORLD")
        model.addAttribute("model2", user)
        return "thymeleaf"
    }
}