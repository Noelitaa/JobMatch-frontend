package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.CreateJobRequest
import com.moviles.jobmatch.data.remote.model.CreateJobResponse
import com.moviles.jobmatch.data.remote.model.JobDetailResponse
import com.moviles.jobmatch.data.remote.model.UpdateJobRequest
import java.io.IOException

class JobRepository(private val apiService: ApiService) {

    suspend fun getJobById(jobId: Int): ApiResult<JobDetailResponse> {
        return try {
            val response = apiService.getJobById(jobId)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else if (response.code() == 404) {
                ApiResult.Error("Oferta laboral no encontrada", 404)
            } else {
                ApiResult.Error("Error al cargar la oferta (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

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

    suspend fun createJob(request: CreateJobRequest): ApiResult<CreateJobResponse> {
        return try {
            val response = apiService.createJob(request)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al crear el empleo (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun updateJob(jobId: Int, request: UpdateJobRequest): ApiResult<JobDetailResponse> {
        return try {
            val response = apiService.updateJob(jobId, request)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else if (response.code() == 403) {
                ApiResult.Error("No tienes permiso para editar esta oferta", 403)
            } else if (response.code() == 400) {
                ApiResult.Error("No se puede editar: tiene postulantes aceptados o contrato activo", 400)
            } else {
                ApiResult.Error("Error al actualizar la oferta (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}