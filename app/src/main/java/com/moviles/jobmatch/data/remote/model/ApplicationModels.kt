package com.moviles.jobmatch.data.remote.model

data class ApplicationResponse(
    val idApplication: Int,
    val idJob: Int,
    val jobTitle: String,
    val idStudent: String,
    val studentName: String,
    val studentEmail: String,
    val studentUniversity: String?,
    val studentCareer: String?,
    val status: String,
    val createdAt: String
)

data class UpdateApplicationRequest(
    val status: String
)

data class UpdateApplicationResponse(
    val idApplication: Int,
    val status: String,
    val message: String
)
