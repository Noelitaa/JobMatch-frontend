package com.moviles.jobmatch.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CompanyUiState {
    data object Loading : CompanyUiState()
    data class Success(val company: Company) : CompanyUiState()
    data class Error(val message: String) : CompanyUiState()
}

class CompanyProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CompanyUiState>(CompanyUiState.Loading)
    val uiState: StateFlow<CompanyUiState> = _uiState.asStateFlow()

    fun loadCompanyProfile(companyId: String) {
        viewModelScope.launch {
            _uiState.value = CompanyUiState.Loading
            try {
                val response = RetrofitClient.apiService.getCompanyProfile(companyId)
                _uiState.value = CompanyUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = CompanyUiState.Error(e.message ?: "Error al cargar perfil")
            }
        }
    }
}