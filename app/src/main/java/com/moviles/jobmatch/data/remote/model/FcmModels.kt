package com.moviles.jobmatch.data.remote.model

data class RegisterFcmTokenRequest(
    val token: String,
    val deviceInfo: String? = null
)
