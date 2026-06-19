package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.CreatePaymentRequest
import com.moviles.jobmatch.data.remote.model.CreatePaymentResponse
import java.io.IOException

class PaymentRepository(private val apiService: ApiService) {

    suspend fun createPayment(
        request: CreatePaymentRequest
    ): ApiResult<CreatePaymentResponse> {
        return try {
            val response = apiService.createPayment(request)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 400 ->
                    ApiResult.Error("Invalid payment data", 400)
                response.code() == 401 ->
                    ApiResult.Error("Unauthorized", 401)
                response.code() == 404 ->
                    ApiResult.Error("Job or student not found", 404)
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