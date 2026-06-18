package com.moviles.jobmatch.data.remote.model

data class CreateJobRequest(
    val title: String,
    val description: String,
    val type: String,
    val payment: Double,
    val paymentType: String,
    // fixed-time fields
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    // autonomous fields
    val startDate: String? = null,
    val endDate: String? = null,
    val deliverables: List<String>? = null,
    // common
    val skillsRequired: List<String>? = null
)

data class UpdateJobRequest(
    val title: String? = null,
    val description: String? = null,
    val payment: Double? = null,
    val paymentType: String? = null,
    val workDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val deliverables: List<String>? = null
)

data class CreateJobResponse(
    val idJob: Int,
    val idCompany: String,
    val title: String,
    val description: String,
    val type: String,
    val status: String,
    val payment: Double,
    val paymentType: String,
    val workDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val createdAt: String
)