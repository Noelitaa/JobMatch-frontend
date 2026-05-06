package com.moviles.jobmatch.data.remote.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val userId: String,
    val role: String,
    val fullName: String,
    val email: String,
    val token: String,
    val expiration: String
)
