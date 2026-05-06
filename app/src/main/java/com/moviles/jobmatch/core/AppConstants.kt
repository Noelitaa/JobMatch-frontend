// core/AppConstants.kt
package com.moviles.jobmatch.core

object AppConstants {

    const val BASE_URL = "http://0.0.0.0:5293/"

    object Api {
        object Paths {
            const val COMPANY_PROFILE = "companies/{companyId}"
            const val AUTH_LOGIN = "auth/login"
        }
    }
}