package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.JobDetailResponse
import com.moviles.jobmatch.data.remote.model.UpdateJobRequest
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EditJobUiState {
    object Idle : EditJobUiState()
    object Loading : EditJobUiState()
    data class JobLoaded(val job: JobDetailResponse) : EditJobUiState()
    object SaveSuccess : EditJobUiState()
    data class Error(val message: String) : EditJobUiState()
}

class EditJobViewModel : ViewModel() {
    private val repository = AppContainer.jobRepository

    private val _uiState = MutableStateFlow<EditJobUiState>(EditJobUiState.Idle)
    val uiState: StateFlow<EditJobUiState> = _uiState

    fun loadJob(jobId: Int) {
        viewModelScope.launch {
            _uiState.value = EditJobUiState.Loading
            when (val result = repository.getJobById(jobId)) {
                is ApiResult.Success -> _uiState.value = EditJobUiState.JobLoaded(result.data)
                is ApiResult.Error -> _uiState.value = EditJobUiState.Error(result.message)
            }
        }
    }

    fun saveJob(
        jobId: Int,
        title: String,
        description: String,
        payment: Double,
        paymentType: String,
        workDate: String,
        startTime: String,
        endTime: String,
        deliverables: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = EditJobUiState.Loading
            val request = UpdateJobRequest(
                title = title,
                description = description,
                payment = payment,
                paymentType = paymentType,
                workDate = workDate,
                startTime = "$startTime:00",
                endTime = "$endTime:00",
                deliverables = deliverables.ifEmpty { null }
            )
            when (val result = repository.updateJob(jobId, request)) {
                is ApiResult.Success -> _uiState.value = EditJobUiState.SaveSuccess
                is ApiResult.Error -> _uiState.value = EditJobUiState.Error(result.message)
            }
        }
    }

    fun resetState(job: JobDetailResponse) {
        _uiState.value = EditJobUiState.JobLoaded(job)
    }
}
