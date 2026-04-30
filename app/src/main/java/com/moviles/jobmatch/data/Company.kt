package com.moviles.jobmatch.data

data class Company(
    val id: String,
    val email: String,
    val companyName: String,
    val description: String?,
    val companyId: String?,
    val phone: String?,
    val avatarUrl: String?,
    val createdAt: String,
    val isActive: Boolean,
    val emailVerified: Boolean,
    val activeJobsCount: Int
)

object MockCompanyData {
    val mockCompany = Company(
        id = "fed4ca8c7-649f-4677-1e7b-88dea6254cf1",
        email = "nuevaempresa@tech.com",
        companyName = "Nueva Tech Solutions",
        description = "Empresa líder en innovación tecnológica, ofrecemos soluciones digitales a medida para todo tipo de empresas",
        companyId = "3-102-789012",
        phone = "98765432",
        avatarUrl = null,
        createdAt = "2026-04-29T19:27:06.6887595",
        isActive = true,
        emailVerified = false,
        activeJobsCount = 0
    )
}