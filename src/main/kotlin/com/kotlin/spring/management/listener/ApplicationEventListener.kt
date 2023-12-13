package com.kotlin.spring.management.listener

import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class ApplicationEventListener {


    /**
     * Application Context Refresh
     */
    @EventListener
    fun handleContextRefreshEvent(event: ContextRefreshedEvent) {

    }
}