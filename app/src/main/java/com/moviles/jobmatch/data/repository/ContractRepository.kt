package com.moviles.jobmatch.data.repository

import com.google.gson.Gson
import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.ContractAcceptResponse
import com.moviles.jobmatch.data.remote.model.ContractData
import com.moviles.jobmatch.data.remote.model.ContractDetailResponse
import com.moviles.jobmatch.data.remote.model.ContractListResponse
import java.io.IOException

class ContractRepository(private val apiService: ApiService) {

    private val gson = Gson()

    suspend fun getStudentContracts(): ApiResult<List<ContractListResponse>> {
        return try {
            val response = apiService.getStudentContracts()
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 401 ->
                    ApiResult.Error("No autorizado", 401)
                response.code() == 403 ->
                    ApiResult.Error("Acceso denegado", 403)
                else ->
                    ApiResult.Error("Error al cargar contratos (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun getContractById(contractId: Int): ApiResult<ContractDetailResponse> {
        return try {
            val response = apiService.getContractById(contractId)
            when {
                response.isSuccessful && response.body() != null -> {
                    val raw = response.body()!!
                    val parsed = runCatching {
                        gson.fromJson(raw.contractData, ContractData::class.java)
                    }.getOrNull()
                    ApiResult.Success(raw.copy(parsedContractData = parsed))
                }
                response.code() == 404 ->
                    ApiResult.Error("Contrato no encontrado", 404)
                response.code() == 401 ->
                    ApiResult.Error("No autorizado", 401)
                else ->
                    ApiResult.Error("Error al cargar detalle (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun getCompanyContracts(status: String? = null): ApiResult<List<ContractListResponse>> {
        return try {
            val response = apiService.getCompanyContracts(status)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 401 ->
                    ApiResult.Error("No autorizado", 401)
                response.code() == 403 ->
                    ApiResult.Error("Acceso denegado", 403)
                else ->
                    ApiResult.Error("Error al cargar contratos (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    suspend fun acceptContract(contractId: Int): ApiResult<ContractAcceptResponse> {
        return try {
            val response = apiService.acceptContract(contractId)
            when {
                response.isSuccessful && response.body() != null ->
                    ApiResult.Success(response.body()!!)
                response.code() == 404 ->
                    ApiResult.Error("Contrato no encontrado", 404)
                response.code() == 401 ->
                    ApiResult.Error("No autorizado", 401)
                response.code() == 403 ->
                    ApiResult.Error("No tenés permiso para aceptar este contrato", 403)
                response.code() == 400 ->
                    ApiResult.Error("El contrato ya fue aceptado o no está pendiente", 400)
                else ->
                    ApiResult.Error("Error al aceptar el contrato (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}
