package com.moviles.jobmatch.data.remote

import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.CompanySummary
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("companies/{companyId}")
    suspend fun getCompanyProfile(@Path("companyId") companyId: String): Company

    @GET("companies")
    suspend fun getAllCompanies(): List<CompanySummary>
}