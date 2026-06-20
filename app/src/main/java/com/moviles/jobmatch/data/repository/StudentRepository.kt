package com.moviles.jobmatch.data.repository

import android.content.Context
import android.net.Uri
import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.StudentProfileResponse
import com.moviles.jobmatch.data.remote.model.UpdateAvailabilityRequest
import com.moviles.jobmatch.data.remote.model.UpdateDescriptionRequest
import com.moviles.jobmatch.data.remote.model.UserProfileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    suspend fun updateAvatar(userId: String, imageUri: Uri, context: Context): ApiResult<UserProfileResponse> {
        return try {
            val stream = context.contentResolver.openInputStream(imageUri)
                ?: return ApiResult.Error("No se pudo abrir la imagen")
            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val extension = when (mimeType) {
                "image/png" -> ".png"
                "image/webp" -> ".webp"
                else -> ".jpg"
            }
            val bytes = stream.readBytes()
            stream.close()
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", "avatar$extension", requestBody)
            val response = apiService.updateAvatar(userId, part)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                else ->
                    ApiResult.Error("Error al actualizar avatar (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun updateDescription(userId: String, description: String): ApiResult<UserProfileResponse> {
        return try {
            val response = apiService.updateDescription(userId, UpdateDescriptionRequest(description))
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                else ->
                    ApiResult.Error("Error al actualizar descripción (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}
