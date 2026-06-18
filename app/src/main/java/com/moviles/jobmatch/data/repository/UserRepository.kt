package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.DeleteUserRequest
import java.io.IOException

class UserRepository(private val apiService: ApiService) {

    suspend fun deleteAccount(userId: String, password: String): ApiResult<Unit> {
        return try {
            val response = apiService.deleteUser(userId, DeleteUserRequest(password))
            when {
                response.isSuccessful -> ApiResult.Success(Unit)
                response.code() == 401 -> ApiResult.Error("Credenciales incorrectas", 401)
                response.code() == 403 -> ApiResult.Error("No tienes permiso para realizar esta acción", 403)
                response.code() == 404 -> ApiResult.Error("Usuario no encontrado", 404)
                response.code() == 400 -> ApiResult.Error("Contraseña incorrecta", 400)
                else -> ApiResult.Error("Error del servidor (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}
