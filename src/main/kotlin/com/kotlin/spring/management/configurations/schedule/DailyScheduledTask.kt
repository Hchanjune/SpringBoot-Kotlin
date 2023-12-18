package com.kotlin.spring.management.configurations.schedule

import com.kotlin.spring.management.services.user.UserCredentialsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyScheduledTask(
    private val userCredentialsService: UserCredentialsService
) {

    /** Acts at Every Midnight  */
    @Scheduled(cron = "0 0 0 * * ?")
    fun executeDailyTask() {
        // User Credential Check (No login logs recorded in the past 6months)
        userCredentialsService.processDormantAccounts()
    }


}