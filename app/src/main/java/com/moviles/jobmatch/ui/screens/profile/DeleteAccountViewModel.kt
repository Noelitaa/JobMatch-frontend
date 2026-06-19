package com.moviles.jobmatch.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DeleteAccountUiState(
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null
)

class DeleteAccountViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState: StateFlow<DeleteAccountUiState> = _uiState.asStateFlow()

    fun deleteAccount(password: String) {
        val userId = AuthSession.currentUser?.userId ?: return
        viewModelScope.launch {
            _uiState.value = DeleteAccountUiState(isLoading = true)
            when (val result = userRepository.deleteAccount(userId, password)) {
                is ApiResult.Success -> {
                    AuthSession.clear()
                    _uiState.value = DeleteAccountUiState(isDeleted = true)
                }
                is ApiResult.Error -> {
                    _uiState.value = DeleteAccountUiState(errorMessage = result.message)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DeleteAccountViewModel(userRepository) as T
        }
    }
}
