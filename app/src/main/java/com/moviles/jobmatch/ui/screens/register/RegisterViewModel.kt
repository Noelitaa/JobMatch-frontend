package com.moviles.jobmatch.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        university: String,
        career: String,
        studentId: String?
    ) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() ||
            confirmPassword.isBlank() || university.isBlank() || career.isBlank() || studentId.isNullOrBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.registerStudent(
                fullName, email, password, university, career, studentId
            )) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            successMessage = "¡Cuenta creada exitosamente! Inicia sesión."
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun registerCompany(
        companyName: String,
        taxId: String,
        email: String,
        phone: String,
        description: String,
        password: String,
        confirmPassword: String
    ) {
        if (companyName.isBlank() || taxId.isBlank() || email.isBlank() ||
            phone.isBlank() || description.isBlank() || password.isBlank() || confirmPassword.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.registerCompany(
                companyName, taxId, email, phone, description, password
            )) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            successMessage = "¡Cuenta creada exitosamente! Inicia sesión."
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

class RegisterViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
