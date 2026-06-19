package com.moviles.jobmatch.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.remote.model.ContractDetailResponse
import com.moviles.jobmatch.data.remote.model.ContractListResponse
import com.moviles.jobmatch.data.remote.model.StudentProfileResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.ContractRepository
import com.moviles.jobmatch.data.repository.StudentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentProfileUiState(
    val isLoading: Boolean = false,
    val student: StudentProfileResponse? = null,
    val contracts: List<ContractListResponse> = emptyList(),
    val isLoadingContracts: Boolean = false,
    val contractDetails: Map<Int, ContractDetailResponse> = emptyMap(),
    val contractDetailErrors: Map<Int, String> = emptyMap(),
    val loadingContractIds: Set<Int> = emptySet(),
    val contractsErrorMessage: String? = null,
    val errorMessage: String? = null
)

class StudentProfileViewModel(
    private val studentRepository: StudentRepository,
    private val contractRepository: ContractRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentProfileUiState())
    val uiState: StateFlow<StudentProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val studentId = AuthSession.currentUser?.userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isLoadingContracts = true, errorMessage = null) }

            val profileDeferred = async { studentRepository.getStudentProfile(studentId) }
            val contractsDeferred = async { contractRepository.getStudentContracts() }

            when (val result = profileDeferred.await()) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isLoading = false, student = result.data) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }

            when (val result = contractsDeferred.await()) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isLoadingContracts = false, contracts = result.data) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isLoadingContracts = false, contractsErrorMessage = result.message) }
            }
        }
    }

    fun loadContractDetail(contractId: Int) {
        if (_uiState.value.contractDetails.containsKey(contractId) ||
            _uiState.value.loadingContractIds.contains(contractId)) return
        viewModelScope.launch {
            _uiState.update { it.copy(loadingContractIds = it.loadingContractIds + contractId) }
            when (val result = contractRepository.getContractById(contractId)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        contractDetails = it.contractDetails + (contractId to result.data),
                        loadingContractIds = it.loadingContractIds - contractId
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(
                        contractDetailErrors = it.contractDetailErrors + (contractId to result.message),
                        loadingContractIds = it.loadingContractIds - contractId
                    )
                }
            }
        }
    }
}

class StudentProfileViewModelFactory(
    private val studentRepository: StudentRepository,
    private val contractRepository: ContractRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudentProfileViewModel(studentRepository, contractRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
