package com.moviles.jobmatch.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.Company
import com.moviles.jobmatch.data.remote.model.ContractDetailResponse
import com.moviles.jobmatch.data.remote.model.ContractListResponse
import com.moviles.jobmatch.data.remote.model.CreateRatingRequest
import com.moviles.jobmatch.data.remote.model.ReceivedRatingResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompanyProfileUiState(
    val isLoading: Boolean = false,
    val company: Company? = null,
    val contracts: List<ContractListResponse> = emptyList(),
    val isLoadingContracts: Boolean = false,
    val contractDetails: Map<Int, ContractDetailResponse> = emptyMap(),
    val contractDetailErrors: Map<Int, String> = emptyMap(),
    val loadingContractIds: Set<Int> = emptySet(),
    val ratingLoadingIds: Set<Int> = emptySet(),
    val ratingSuccessIds: Set<Int> = emptySet(),
    val ratingErrors: Map<Int, String> = emptyMap(),
    val receivedRatings: List<ReceivedRatingResponse> = emptyList(),
    val isLoadingRatings: Boolean = false,
    val ratingsError: String? = null,
    val contractsErrorMessage: String? = null,
    val errorMessage: String? = null
)

class CompanyProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CompanyProfileUiState())
    val uiState: StateFlow<CompanyProfileUiState> = _uiState.asStateFlow()

    fun loadCompanyProfile(companyId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isLoadingContracts = true, isLoadingRatings = true, errorMessage = null) }

            val profileDeferred = async { AppContainer.companyRepository.getCompanyById(companyId) }
            val contractsDeferred = async { AppContainer.contractRepository.getCompanyContracts() }
            val ratingsDeferred = async { AppContainer.ratingRepository.getMyRatings() }

            when (val result = profileDeferred.await()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, company = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }

            when (val result = contractsDeferred.await()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoadingContracts = false, contracts = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingContracts = false, contractsErrorMessage = result.message) }
            }

            when (val result = ratingsDeferred.await()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoadingRatings = false, receivedRatings = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingRatings = false, ratingsError = result.message) }
            }
        }
    }

    fun submitRating(contractId: Int, stars: Int, comment: String?) {
        if (_uiState.value.ratingLoadingIds.contains(contractId)) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    ratingLoadingIds = it.ratingLoadingIds + contractId,
                    ratingErrors = it.ratingErrors - contractId
                )
            }
            val request = CreateRatingRequest(idContract = contractId, stars = stars, comment = comment)
            when (val result = AppContainer.ratingRepository.submitRating(request)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        ratingLoadingIds = it.ratingLoadingIds - contractId,
                        ratingSuccessIds = it.ratingSuccessIds + contractId
                    )
                }
                is ApiResult.Error -> {
                    if (result.statusCode == 409) {
                        _uiState.update {
                            it.copy(
                                ratingLoadingIds = it.ratingLoadingIds - contractId,
                                ratingSuccessIds = it.ratingSuccessIds + contractId
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                ratingLoadingIds = it.ratingLoadingIds - contractId,
                                ratingErrors = it.ratingErrors + (contractId to result.message)
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearRatingError(contractId: Int) {
        _uiState.update { it.copy(ratingErrors = it.ratingErrors - contractId) }
    }

    fun loadContractDetail(contractId: Int) {
        if (_uiState.value.contractDetails.containsKey(contractId) ||
            _uiState.value.loadingContractIds.contains(contractId)) return
        viewModelScope.launch {
            _uiState.update { it.copy(loadingContractIds = it.loadingContractIds + contractId) }
            when (val result = AppContainer.contractRepository.getContractById(contractId)) {
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
