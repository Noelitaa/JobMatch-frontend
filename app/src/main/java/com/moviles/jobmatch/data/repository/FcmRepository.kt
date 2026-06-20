package com.moviles.jobmatch.data.repository

import android.util.Log
import com.moviles.jobmatch.data.remote.ApiService
import com.moviles.jobmatch.data.remote.model.RegisterFcmTokenRequest

class FcmRepository(private val apiService: ApiService) {

    suspend fun registerToken(token: String, deviceInfo: String? = null) {
        try {
            apiService.registerFcmToken(RegisterFcmTokenRequest(token, deviceInfo))
        } catch (e: Exception) {
            Log.e("FcmRepository", "Failed to register FCM token", e)
        }
    }

    suspend fun deleteToken(token: String) {
        try {
            apiService.deleteFcmToken(token)
        } catch (e: Exception) {
            Log.e("FcmRepository", "Failed to delete FCM token", e)
        }
    }
}
