package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.remote.model.JobDetailResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class JobDetailUiState {
    data object Loading : JobDetailUiState()
    data class Success(val job: JobDetailResponse) : JobDetailUiState()
    data class Error(val message: String) : JobDetailUiState()
}

sealed class ApplyState {
    data object Idle : ApplyState()
    data object Loading : ApplyState()
    data object Success : ApplyState()
    data class Error(val message: String) : ApplyState()
    data object AlreadyApplied : ApplyState()
}

class JobDetailViewModel : ViewModel() {
    private val repository = AppContainer.jobRepository
    private val applicationRepository = AppContainer.applicationRepository

    private val _uiState = MutableStateFlow<JobDetailUiState>(JobDetailUiState.Loading)
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    private val _applyState = MutableStateFlow<ApplyState>(ApplyState.Idle)
    val applyState: StateFlow<ApplyState> = _applyState.asStateFlow()

    fun loadJobDetail(jobId: Int) {
        viewModelScope.launch {
            _uiState.value = JobDetailUiState.Loading
            when (val result = repository.getJobById(jobId)) {
                is ApiResult.Success -> {
                    _uiState.value = JobDetailUiState.Success(result.data)
                    // Check if student has already applied
                    if (AuthSession.isStudent) {
                        checkIfAlreadyApplied(jobId)
                    }
                }
                is ApiResult.Error -> _uiState.value = JobDetailUiState.Error(result.message)
            }
        }
    }

    private suspend fun checkIfAlreadyApplied(jobId: Int) {
        val studentId = AuthSession.currentUser?.userId ?: return
        when (val appsResult = applicationRepository.getApplicationsByJob(jobId)) {
            is ApiResult.Success -> {
                val hasApplied = appsResult.data.any { it.idStudent == studentId }
                if (hasApplied) {
                    _applyState.value = ApplyState.AlreadyApplied
                }
            }
            else -> {} // Silently fail for "AlreadyApplied" check
        }
    }

    fun applyToJob(jobId: Int) {
        if (_applyState.value is ApplyState.Loading || 
            _applyState.value is ApplyState.Success || 
            _applyState.value is ApplyState.AlreadyApplied) return
            
        viewModelScope.launch {
            _applyState.value = ApplyState.Loading
            when (val result = applicationRepository.applyToJob(jobId)) {
                is ApiResult.Success -> _applyState.value = ApplyState.Success
                is ApiResult.Error -> {
                    if (result.statusCode == 409) {
                        _applyState.value = ApplyState.AlreadyApplied
                    } else {
                        _applyState.value = ApplyState.Error(result.message)
                    }
                }
            }
        }
    }

    fun resetApplyState() {
        _applyState.value = ApplyState.Idle
    }
}
