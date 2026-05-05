package com.moviles.jobmatch.navigation

object AppDestinations {
    const val SEARCH_COMPANY = "search_company"
    const val COMPANY_PROFILE = "company_profile/{companyId}"

    fun companyProfileRoute(companyId: String): String {
        return "company_profile/$companyId"
    }
}