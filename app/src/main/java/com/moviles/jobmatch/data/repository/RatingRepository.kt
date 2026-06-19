package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.CreateRatingRequest
import com.moviles.jobmatch.data.remote.model.RatingResponse
import com.moviles.jobmatch.data.remote.model.ReceivedRatingResponse
import java.io.IOException

class RatingRepository(private val apiService: ApiService) {

    suspend fun submitRating(request: CreateRatingRequest): ApiResult<RatingResponse> {
        return try {
            val response = apiService.submitRating(request)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 409 ->
                    ApiResult.Error("Ya calificaste este contrato.", 409)
                response.code() == 403 ->
                    ApiResult.Error("No tienes permiso para calificar este contrato.", 403)
                response.code() == 404 ->
                    ApiResult.Error("Contrato no encontrado.", 404)
                response.code() == 400 ->
                    ApiResult.Error("Datos de calificación inválidos.", 400)
                else ->
                    ApiResult.Error("Error al enviar calificación (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor.")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado.")
        }
    }

    suspend fun getMyRatings(): ApiResult<List<ReceivedRatingResponse>> {
        return try {
            val response = apiService.getMyRatings()
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.isSuccessful ->
                    ApiResult.Success(emptyList())
                response.code() == 401 ->
                    ApiResult.Error("No autorizado.", 401)
                else ->
                    ApiResult.Error("Error al obtener calificaciones (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor.")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado.")
        }
    }
}
