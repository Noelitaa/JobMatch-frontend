package com.moviles.jobmatch.data

data class Job(
    val id: Int,
    val title: String,
    val company: String,
    val fitPercentage: Int,
    val schedule: String,
    val distanceKm: Double,
    val paymentType: String,
    val amount: Int
)