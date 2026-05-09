package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.data.remote.ApiService
import java.io.IOException

class JobRepository(private val apiService: ApiService) {

    suspend fun getJobs(): ApiResult<List<Job>> {
        return try {
            val response = apiService.getAllJobs()
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al cargar empleos (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}