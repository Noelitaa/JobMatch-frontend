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
    val workDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val deliverables: List<String> = emptyList()
)
