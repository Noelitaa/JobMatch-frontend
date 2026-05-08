package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.RetrofitClient
import com.moviles.jobmatch.data.remote.model.CreateJobRequest
import com.moviles.jobmatch.data.remote.model.CreateJobResponse
import java.io.IOException

class JobRepository {
    suspend fun createJob(request: CreateJobRequest): ApiResult<CreateJobResponse> {
        return try {
            val response = RetrofitClient.apiService.createJob(request)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 400 ->
                    ApiResult.Error("Datos inválidos", 400)
                response.code() == 401 ->
                    ApiResult.Error("No autorizado", 401)
                else ->
                    ApiResult.Error("Error del servidor (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}