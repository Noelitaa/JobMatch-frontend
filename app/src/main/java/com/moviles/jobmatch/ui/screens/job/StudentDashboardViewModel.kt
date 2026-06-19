package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentDashboardUiState(
    val isLoading: Boolean = false,
    val recommendedJobs: List<Job> = emptyList(),
    val errorMessage: String? = null,
    val studentName: String = ""
)

class StudentDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        val name = AuthSession.currentUser?.fullName ?: "Estudiante"
        _uiState.update { it.copy(studentName = name) }
        loadRecommended()
    }

    fun loadRecommended() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = AppContainer.jobRepository.getRecommendedJobs()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(isLoading = false, recommendedJobs = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }
}
