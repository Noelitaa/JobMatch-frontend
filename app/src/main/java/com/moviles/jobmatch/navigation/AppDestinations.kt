package com.moviles.jobmatch.navigation

object AppDestinations {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val SEARCH_COMPANY = "search_company"
    const val COMPANY_PROFILE = "company_profile/{companyId}"
    const val MAIN_TABS = "main_tabs"

    fun companyProfileRoute(companyId: String): String {
        return "company_profile/$companyId"
    }
}