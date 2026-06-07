package com.moviles.jobmatch.navigation

import android.net.Uri

object AppDestinations {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val SEARCH_COMPANY = "inicio"
    const val JOBS_EXPLORE = "explorar"
    const val COMPANY_PROFILE = "company_profile"
    const val CREATE_JOB = "create_job/{companyId}"
    const val JOB_DETAIL = "job_detail"
    const val APPLICATIONS = "applications"
    const val MY_JOBS = "mis_trabajos"
    const val ALERTS = "alertas"
    const val PROFILE = "perfil"

    fun companyProfileRoute(companyId: String): String {
        return "$COMPANY_PROFILE/${Uri.encode(companyId)}"
    }

    fun createJobRoute(companyId: String): String {
        return "create_job/${Uri.encode(companyId)}"
    }
    fun jobDetailRoute(jobId: Int): String = "$JOB_DETAIL/$jobId"
    fun applicationsRoute(jobId: Int, jobTitle: String): String =
        "$APPLICATIONS/$jobId/${Uri.encode(jobTitle)}"
}