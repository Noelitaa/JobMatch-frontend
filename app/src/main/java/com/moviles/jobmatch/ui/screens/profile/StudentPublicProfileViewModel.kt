package com.moviles.jobmatch.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.StudentProfileResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentPublicProfileUiState(
    val isLoading: Boolean = false,
    val student: StudentProfileResponse? = null,
    val errorMessage: String? = null
)

class StudentPublicProfileViewModel(
    private val studentId: String,
    private val repository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentPublicProfileUiState(isLoading = true))
    val uiState: StateFlow<StudentPublicProfileUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = StudentPublicProfileUiState(isLoading = true)
            when (val result = repository.getStudentProfile(studentId)) {
                is ApiResult.Success -> _uiState.value = StudentPublicProfileUiState(student = result.data)
                is ApiResult.Error   -> _uiState.value = StudentPublicProfileUiState(errorMessage = result.message)
            }
        }
    }

    class Factory(
        private val studentId: String,
        private val repository: StudentRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return StudentPublicProfileViewModel(studentId, repository) as T
        }
    }
}
