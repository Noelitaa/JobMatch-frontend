package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.ApplicationResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ApplicationsUiState(
    val isLoading: Boolean = false,
    val applications: List<ApplicationResponse> = emptyList(),
    val errorMessage: String? = null,
    val updatingId: Int? = null
)

class ApplicationsViewModel : ViewModel() {
    private val repository = AppContainer.applicationRepository

    private val _uiState = MutableStateFlow(ApplicationsUiState())
    val uiState: StateFlow<ApplicationsUiState> = _uiState.asStateFlow()

    fun loadApplications(jobId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getApplicationsByJob(jobId)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    applications = result.data
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun updateStatus(applicationId: Int, status: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(updatingId = applicationId)
            when (val result = repository.updateApplicationStatus(applicationId, status)) {
                is ApiResult.Success -> {
                    val newStatus = result.data.status
                    _uiState.value = _uiState.value.copy(
                        updatingId = null,
                        applications = _uiState.value.applications.map {
                            if (it.idApplication == applicationId) it.copy(status = newStatus) else it
                        }
                    )
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    updatingId = null,
                    errorMessage = result.message
                )
            }
        }
    }
}
