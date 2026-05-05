package com.moviles.jobmatch.data.repository


object AppContainer {
    val companyRepository: CompanyRepository by lazy {
        CompanyRepository()
    }
}