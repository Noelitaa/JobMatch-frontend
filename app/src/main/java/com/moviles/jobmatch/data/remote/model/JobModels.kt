package com.moviles.jobmatch.data.remote.model

data class CreateJobRequest(
    val companyId: String,
    val title: String,
    val description: String,
    val type: String = "fixed-time",
    val payment: Double,
    val paymentType: String,
    val date: String,
    val startTime: String,
    val endTime: String,
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
    val workDate: String,
    val startTime: String,
    val endTime: String,
    val createdAt: String
)