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

data class RegisterStudentRequest(
    val fullName: String,
    val email: String,
    @com.google.gson.annotations.SerializedName("passwordHash")
    val password: String,
    val university: String,
    val career: String,
    val studentId: String? = null
)

data class RegisterResponse(
    val userId: String,
    val message: String
)
