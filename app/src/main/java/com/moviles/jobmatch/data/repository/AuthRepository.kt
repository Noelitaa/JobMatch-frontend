package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.LoginRequest
import com.moviles.jobmatch.data.remote.model.LoginResponse
import com.moviles.jobmatch.data.remote.model.RegisterResponse
import com.moviles.jobmatch.data.remote.model.RegisterCompanyRequest
import com.moviles.jobmatch.data.remote.model.RegisterStudentRequest
import java.io.IOException

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            when {
                response.isSuccessful && response.body() != null -> {
                    ApiResult.Success(response.body()!!)
                }
                response.code() == 401 -> {
                    ApiResult.Error("Credenciales incorrectas", 401)
                }
                response.code() == 400 -> {
                    ApiResult.Error("Datos inválidos", 400)
                }
                else -> {
                    ApiResult.Error("Error del servidor (${response.code()})", response.code())
                }
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun registerStudent(
        fullName: String,
        email: String,
        password: String,
        university: String,
        career: String,
        studentId: String
    ): ApiResult<RegisterResponse> {
        return try {
            val response = apiService.registerStudent(
                RegisterStudentRequest(fullName, email, password, university, career, studentId)
            )
            when {
                response.isSuccessful && response.body() != null -> {
                    ApiResult.Success(response.body()!!)
                }
                response.code() == 409 -> {
                    ApiResult.Error("Este correo ya está registrado", 409)
                }
                response.code() == 400 -> {
                    ApiResult.Error("Datos inválidos", 400)
                }
                else -> {
                    ApiResult.Error("Error del servidor (${response.code()})", response.code())
                }
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun registerCompany(
        companyName: String,
        taxId: String,
        email: String,
        phone: String,
        description: String,
        password: String
    ): ApiResult<RegisterResponse> {
        return try {
            val response = apiService.registerCompany(
                RegisterCompanyRequest(companyName, taxId, email, phone, description, password)
            )
            when {
                response.isSuccessful && response.body() != null -> {
                    ApiResult.Success(response.body()!!)
                }
                response.code() == 409 -> {
                    ApiResult.Error("Este correo ya está registrado", 409)
                }
                response.code() == 400 -> {
                    ApiResult.Error("Datos inválidos", 400)
                }
                else -> {
                    ApiResult.Error("Error del servidor (${response.code()})", response.code())
                }
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}
