package com.moviles.jobmatch.data

data class Company(
    val id: String,
    val email: String,
    val companyName: String,
    val description: String?,
    val companyId: String?,
    val phone: String?,
    val avatarUrl: String?,
    val createdAt: String,
    val isActive: Boolean,
    val emailVerified: Boolean,
    val activeJobsCount: Int
)

