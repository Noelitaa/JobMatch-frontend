package com.moviles.jobmatch.navigation

object AppDestinations {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val SEARCH_COMPANY = "search_company"
    const val COMPANY_PROFILE = "company_profile/{companyId}"

    const val CREATE_JOB = "create_job/{companyId}"

    fun companyProfileRoute(companyId: String) = "company_profile/$companyId"
    fun createJobRoute(companyId: String) = "create_job/$companyId"
}