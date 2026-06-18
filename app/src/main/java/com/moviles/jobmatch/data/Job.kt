package com.moviles.jobmatch.data

data class Job(
    val idJob: Int = 0,
    val idCompany: String,
    val title: String,
    val description: String = "",
    val type: String,
    val status: String = "open",
    val payment: Double,
    val paymentType: String,
    val workDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val deliverables: List<String>? = null,
    val createdAt: String = ""
)
