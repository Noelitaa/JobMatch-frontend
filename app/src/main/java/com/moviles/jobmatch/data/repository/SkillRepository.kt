package com.moviles.jobmatch.data.repository

import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.AddSkillRequest
import java.io.IOException

interface SkillRepository {
    suspend fun getAllSkills(): ApiResult<List<String>>
    suspend fun addSkillToStudent(studentId: String, skillName: String): ApiResult<Unit>
    suspend fun removeSkillFromStudent(studentId: String, skillId: String): ApiResult<Unit>
}

class SkillRepositoryImpl(private val apiService: ApiService) : SkillRepository {

    override suspend fun getAllSkills(): ApiResult<List<String>> {
        return try {
            val response = apiService.getAllSkills()
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al cargar habilidades (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    override suspend fun addSkillToStudent(studentId: String, skillName: String): ApiResult<Unit> {
        return try {
            val response = apiService.addSkillToStudent(studentId, AddSkillRequest(skillName))
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else if (response.code() == 409) {
                ApiResult.Error("La habilidad '$skillName' ya está registrada", 409)
            } else {
                ApiResult.Error("Error al agregar habilidad (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }

    override suspend fun removeSkillFromStudent(studentId: String, skillId: String): ApiResult<Unit> {
        return try {
            val response = apiService.removeSkillFromStudent(studentId, skillId)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("Error al eliminar habilidad (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar al servidor")
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error inesperado")
        }
    }
}
