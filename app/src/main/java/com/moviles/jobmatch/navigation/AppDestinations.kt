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
    const val EDIT_JOB = "edit_job"
    const val APPLICATIONS = "applications"
    const val COMPANY_DASHBOARD = "company_dashboard"
    const val MY_JOBS = "mis_trabajos"
    const val ALERTS = "alertas"
    const val PROFILE = "perfil"
    const val STUDENT_PROFILE = "student_profile"
    const val STUDENT_PUBLIC_PROFILE = "student_public_profile"
    const val APPLICATION_DETAIL = "application_detail"

    fun applicationDetailRoute(
        applicationId: Int,
        jobId: Int,
        jobTitle: String,
        studentId: String,
        studentName: String,
        studentEmail: String,
        status: String,
        createdAt: String
    ): String = "$APPLICATION_DETAIL/$applicationId/$jobId/${Uri.encode(jobTitle)}/${Uri.encode(studentId)}/${Uri.encode(studentName)}/${Uri.encode(studentEmail)}/${Uri.encode(status)}/${Uri.encode(createdAt)}"

    fun studentPublicProfileRoute(studentId: String): String =
        "$STUDENT_PUBLIC_PROFILE/${Uri.encode(studentId)}"

    const val AVAILABILITY = "availability"

    fun companyProfileRoute(companyId: String): String {
        return "$COMPANY_PROFILE/${Uri.encode(companyId)}"
    }

    fun createJobRoute(companyId: String): String {
        return "create_job/${Uri.encode(companyId)}"
    }
    fun jobDetailRoute(jobId: Int): String = "$JOB_DETAIL/$jobId"
    fun editJobRoute(jobId: Int): String = "$EDIT_JOB/$jobId"
    fun applicationsRoute(jobId: Int, jobTitle: String): String =
        "$APPLICATIONS/$jobId/${Uri.encode(jobTitle)}"
}