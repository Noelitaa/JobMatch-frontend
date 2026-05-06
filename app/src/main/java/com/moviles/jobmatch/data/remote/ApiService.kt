package com.moviles.jobmatch.data.remote

import com.moviles.jobmatch.core.AppConstants
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.CompanySummary
import com.moviles.jobmatch.data.remote.model.LoginRequest
import com.moviles.jobmatch.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST(AppConstants.Api.Paths.AUTH_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("companies/{companyId}")
    suspend fun getCompanyProfile(@Path("companyId") companyId: String): Company

    @GET("companies")
    suspend fun getAllCompanies(): List<CompanySummary>
}