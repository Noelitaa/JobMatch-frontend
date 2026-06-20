package com.moviles.jobmatch.data

data class CompanySummary(
    val id: String,
    val companyName: String?,
    val email: String,
    val phone: String? = null,
    val description: String? = null,
    val avatarUrl: String? = null
)