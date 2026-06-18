package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.ApplicationResponse
import com.moviles.jobmatch.data.remote.model.CreateApplicationRequest
import com.moviles.jobmatch.data.remote.model.CreateApplicationResponse
import com.moviles.jobmatch.data.remote.model.UpdateApplicationRequest
import com.moviles.jobmatch.data.remote.model.UpdateApplicationResponse
import java.io.IOException

class ApplicationRepository(private val apiService: ApiService) {

    suspend fun applyToJob(jobId: Int): ApiResult<CreateApplicationResponse> {
        return try {
            val response = apiService.applyToJob(CreateApplicationRequest(jobId))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                val message = when (response.code()) {
                    403 -> "Only students can apply for job offers"
                    404 -> "Job offer not found"
                    409 -> "You have already applied to this job"
                    else -> "Failed to submit application (${response.code()})"
                }
                ApiResult.Error(message, response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("Could not connect to server")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unexpected error")
        }
    }

    suspend fun getApplicationsByJob(jobId: Int): ApiResult<List<ApplicationResponse>> {
        return try {
            val response = apiService.getApplicationsByJob(jobId)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al cargar postulantes (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun updateApplicationStatus(
        applicationId: Int,
        status: String
    ): ApiResult<UpdateApplicationResponse> {
        return try {
            val response = apiService.updateApplicationStatus(
                applicationId,
                UpdateApplicationRequest(status)
            )
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al actualizar postulación (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}
