package com.moviles.jobmatch.data

import com.moviles.jobmatch.navigation.AppDestinations

object NotificationHandler {

    var pendingRoute: String? = null

    fun resolveRoute(type: String?, entityId: String?): String? = when (type) {
        "new_application" -> AppDestinations.JOBS_EXPLORE
        "contract_accepted" -> if (AuthSession.isCompany)
            AuthSession.currentUser?.userId?.let { AppDestinations.companyProfileRoute(it) }
        else
            AppDestinations.PROFILE
        "payment_registered" -> AppDestinations.PAYMENT_HISTORY
        else -> null
    }

    fun setFromPayload(type: String?, entityId: String?) {
        pendingRoute = resolveRoute(type, entityId)
    }

    fun consume(): String? {
        val route = pendingRoute
        pendingRoute = null
        return route
    }
}
