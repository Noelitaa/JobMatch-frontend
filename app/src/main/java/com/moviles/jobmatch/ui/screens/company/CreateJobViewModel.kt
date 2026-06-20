package com.moviles.jobmatch.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.CreateJobRequest
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CreateJobUiState {
    object Idle : CreateJobUiState()
    object Loading : CreateJobUiState()
    object Success : CreateJobUiState()
    data class Error(val message: String) : CreateJobUiState()
}

class CreateJobViewModel : ViewModel() {

    private val jobRepository = AppContainer.jobRepository

    private val _uiState = MutableStateFlow<CreateJobUiState>(CreateJobUiState.Idle)
    val uiState: StateFlow<CreateJobUiState> = _uiState

    fun createFixedTimeJob(
        title: String,
        description: String,
        payment: Double,
        paymentType: String,
        workDate: String,
        startTime: String,
        endTime: String,
        skillsRequired: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = CreateJobUiState.Loading
            val request = CreateJobRequest(
                title = title,
                description = description,
                type = "fixed-time",
                payment = payment,
                paymentType = paymentType.lowercase(),
                date = workDate,
                startTime = startTime,
                endTime = endTime,
                skillsRequired = skillsRequired.ifEmpty { null }
            )
            submit(request)
        }
    }

    fun createAutonomousJob(
        title: String,
        description: String,
        payment: Double,
        paymentType: String,
        startDate: String,
        endDate: String,
        deliverables: List<String>,
        skillsRequired: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = CreateJobUiState.Loading
            val request = CreateJobRequest(
                title = title,
                description = description,
                type = "autonomous",
                payment = payment,
                paymentType = paymentType.lowercase(),
                startDate = startDate,
                endDate = endDate,
                deliverables = deliverables.ifEmpty { null },
                skillsRequired = skillsRequired.ifEmpty { null }
            )
            submit(request)
        }
    }

    private suspend fun submit(request: CreateJobRequest) {
        when (val result = jobRepository.createJob(request)) {
            is ApiResult.Success<*> -> _uiState.value = CreateJobUiState.Success
            is ApiResult.Error -> _uiState.value = CreateJobUiState.Error(result.message)
        }
    }

    fun resetState() {
        _uiState.value = CreateJobUiState.Idle
    }
}
