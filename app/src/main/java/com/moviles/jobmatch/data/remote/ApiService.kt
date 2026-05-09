package com.moviles.jobmatch.data.remote

import com.moviles.jobmatch.core.AppConstants
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.CompanySummary
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.data.remote.model.LoginRequest
import com.moviles.jobmatch.data.remote.model.LoginResponse
import com.moviles.jobmatch.data.remote.model.RegisterResponse
import com.moviles.jobmatch.data.remote.model.RegisterCompanyRequest
import com.moviles.jobmatch.data.remote.model.RegisterStudentRequest
import com.moviles.jobmatch.data.remote.model.CreateJobRequest
import com.moviles.jobmatch.data.remote.model.CreateJobResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(AppConstants.Api.Paths.AUTH_REGISTER_STUDENT)
    suspend fun registerStudent(@Body request: RegisterStudentRequest): Response<RegisterResponse>

    @POST(AppConstants.Api.Paths.AUTH_REGISTER_COMPANY)
    suspend fun registerCompany(@Body request: RegisterCompanyRequest): Response<RegisterResponse>

    @GET("companies/{companyId}")
    suspend fun getCompanyProfile(@Path("companyId") companyId: String): Company

    @GET("companies")
    suspend fun getAllCompanies(): List<CompanySummary>

    @GET("jobs")
    suspend fun getAllJobs(): Response<List<Job>>

    @POST("jobs")
    suspend fun createJob(@Body request: CreateJobRequest): Response<CreateJobResponse>
}