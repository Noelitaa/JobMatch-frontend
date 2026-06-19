package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.PaymentResponse
import java.io.IOException

class PaymentRepository(private val apiService: ApiService) {

    suspend fun getPaymentHistory(
        startDate: String? = null,
        endDate: String? = null
    ): ApiResult<List<PaymentResponse>> {
        return try {
            val response = apiService.getPaymentHistory(startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                val message = parseErrorBody(response) ?: "Error al cargar historial (${response.code()})"
                ApiResult.Error(message, response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    private fun parseErrorBody(response: retrofit2.Response<*>): String? {
        return try {
            val body = response.errorBody()?.string() ?: return null
            org.json.JSONObject(body).optString("message").takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
