package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.StudentProfileResponse
import com.moviles.jobmatch.data.remote.model.UpdateAvailabilityRequest
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
        request: UpdateAvailabilityRequest
    ): ApiResult<Unit> {
        return try {
            val response = apiService.updateAvailability(studentId, request)
            when {
                response.isSuccessful ->
                    ApiResult.Success(Unit)
                response.code() == 400 ->
                    ApiResult.Error("Invalid availability data", 400)
                response.code() == 404 ->
                    ApiResult.Error("Student not found", 404)
                response.code() == 401 ->
                    ApiResult.Error("Unauthorized", 401)
                else ->
                    ApiResult.Error("Server error (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("Could not connect to server")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unexpected error")
        }
    }
}
