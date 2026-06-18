package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.RetrofitClient

object AppContainer {
    val companyRepository: CompanyRepository by lazy {
        CompanyRepository(RetrofitClient.apiService)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(RetrofitClient.apiService)
    }

    val jobRepository: JobRepository by lazy {
        JobRepository(RetrofitClient.apiService)
    }

    val applicationRepository: ApplicationRepository by lazy {
        ApplicationRepository(RetrofitClient.apiService)
    }

    val studentRepository: StudentRepository by lazy {
        StudentRepository(RetrofitClient.apiService)
    }

    val skillRepository: SkillRepository by lazy {
        SkillRepositoryImpl(RetrofitClient.apiService)
    }
}