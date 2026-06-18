package com.moviles.jobmatch.data.remote.model

data class ContractListResponse(
    val idContract: Int,
    val idJob: Int,
    val jobTitle: String,
    val idStudent: String,
    val studentName: String,
    val companyName: String,
    val status: String,
    val createdAt: String,
    val acceptedAt: String? = null
)

data class ContractDetailResponse(
    val idContract: Int,
    val idApplication: Int,
    val idJob: Int,
    val jobTitle: String,
    val idStudent: String,
    val studentName: String,
    val studentEmail: String,
    val idCompany: String,
    val companyName: String,
    val status: String,
    val contractData: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val acceptedAt: String? = null
)

data class ContractData(
    val jobTitle: String? = null,
    val workType: String? = null,
    val companyName: String? = null,
    val companyEmail: String? = null,
    val companyOwnerName: String? = null,
    val studentName: String? = null,
    val studentEmail: String? = null,
    val studentUniversity: String? = null,
    val studentCareer: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val compensation: String? = null,
    val clauses: List<String>? = null
)
