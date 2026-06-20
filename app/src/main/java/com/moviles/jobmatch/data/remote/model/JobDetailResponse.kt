package com.moviles.jobmatch.data.remote.model

data class CompanySummaryResponse(
    val id: String,
    val companyName: String?,
    val email: String,
    val phone: String?,
    val description: String?,
    val avatarUrl: String?
)

data class JobDetailResponse(
    val idJob: Int,
    val idCompany: String,
    val title: String,
    val description: String,
    val type: String,
    val status: String,
    val payment: Double,
    val paymentType: String,
    val workDate: String?,
    val startTime: String?,
    val endTime: String?,
    val startDate: String?,
    val endDate: String?,
    val deliverables: List<String>?,
    val createdAt: String,
    val updatedAt: String?,
    val company: CompanySummaryResponse
)
