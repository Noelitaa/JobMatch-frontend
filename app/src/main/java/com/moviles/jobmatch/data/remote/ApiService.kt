package com.moviles.jobmatch.data.remote

import androidx.compose.ui.graphics.vector.Path
import com.moviles.jobmatch.data.Company
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("companies/{companyId}")
    suspend fun getCompanyProfile(
        @Path("companyId") companyId: String
    ): Company
}