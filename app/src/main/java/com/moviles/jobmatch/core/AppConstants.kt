// core/AppConstants.kt
package com.moviles.jobmatch.core

object AppConstants {

    const val BASE_URL = "http://localhost:5293/"

    object Api {
        object Paths {
            const val COMPANY_PROFILE = "companies/{companyId}"
            const val AUTH_LOGIN = "auth/login"
            const val AUTH_REGISTER_STUDENT = "auth/register"
            const val AUTH_REGISTER_COMPANY = "auth/register/company"
            const val STUDENT_PROFILE_PATH = "students/{studentId}"
        }
    }
}