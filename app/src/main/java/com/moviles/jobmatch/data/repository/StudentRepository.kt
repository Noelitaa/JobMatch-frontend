package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.StudentProfileResponse
import java.io.IOException

class StudentRepository(private val apiService: ApiService) {

    suspend fun getStudentProfile(studentId: String): ApiResult<StudentProfileResponse> {
        return try {
            val response = apiService.getStudentProfile(studentId)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 404 ->
                    ApiResult.Error("Perfil no encontrado", 404)
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
    suspend fun updateAvailability(
        studentId: String,
        availability: AvailabilityResponse
    ): ApiResult<AvailabilityResponse> {
        return try {
            val response = apiService.updateAvailability(studentId, availability)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 404 ->
                    ApiResult.Error("Estudiante no encontrado", 404)
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
