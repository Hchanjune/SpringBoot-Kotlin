package com.kotlin.spring.management.domains.common.apiResponse

import org.springframework.ui.Model
import org.springframework.web.servlet.ModelAndView

interface ResponseAdapter {
    fun appendModel(model: Model, modelName: String)
    fun toApi(): ResponseVo
}