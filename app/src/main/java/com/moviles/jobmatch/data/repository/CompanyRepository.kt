package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.CompanySummary
import com.moviles.jobmatch.data.remote.ApiService

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val statusCode: Int? = null) : ApiResult<Nothing>()
}

class CompanyRepository(private val apiService: ApiService) {

    suspend fun getCompanyById(companyId: String): ApiResult<Company> {
        return try {
            val response = apiService.getCompanyProfile(companyId)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error al cargar la empresa")
        }
    }

    suspend fun getAllCompanies(): ApiResult<List<CompanySummary>> {
        return try {
            val response = apiService.getAllCompanies()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error al cargar las empresas")
        }
    }
}