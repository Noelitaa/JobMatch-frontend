package com.moviles.jobmatch.data

data class Job(
    val idCompany: String,
    val title: String,
    val description: String,
    val type: String,
    val payment: Double,
    val paymentType: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val deliverables: List<String> = emptyList()
)