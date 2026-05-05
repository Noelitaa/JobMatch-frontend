package com.moviles.jobmatch.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.repository.AppContainer
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.repository.ApiResult
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
            when (val result = AppContainer.companyRepository.getCompanyById(companyId)) {
                is ApiResult.Success -> {
                    _uiState.value = CompanyUiState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _uiState.value = CompanyUiState.Error(result.message)
                }
            }
        }
    }
}