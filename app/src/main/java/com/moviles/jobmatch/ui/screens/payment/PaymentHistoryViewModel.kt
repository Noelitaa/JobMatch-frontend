package com.moviles.jobmatch.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.PaymentResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PaymentFilter { ALL, RECEIVED, MADE }

data class PaymentHistoryUiState(
    val isLoading: Boolean = false,
    val payments: List<PaymentResponse> = emptyList(),
    val filter: PaymentFilter = PaymentFilter.ALL,
    val startDate: String? = null,
    val endDate: String? = null,
    val errorMessage: String? = null
) {
    val filtered: List<PaymentResponse> get() = when (filter) {
        PaymentFilter.ALL -> payments
        PaymentFilter.RECEIVED -> payments.filter { it.type == "received" }
        PaymentFilter.MADE -> payments.filter { it.type == "made" }
    }

    val totalReceived: Double get() = payments.filter { it.type == "received" }.sumOf { it.amount }
    val totalMade: Double get() = payments.filter { it.type == "made" }.sumOf { it.amount }
}

class PaymentHistoryViewModel(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentHistoryUiState())
    val uiState: StateFlow<PaymentHistoryUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.getPaymentHistory(state.startDate, state.endDate)) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isLoading = false, payments = result.data) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun setFilter(filter: PaymentFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun applyDateRange(startDate: String?, endDate: String?) {
        _uiState.update { it.copy(startDate = startDate, endDate = endDate) }
        load()
    }

    fun clearDateRange() {
        _uiState.update { it.copy(startDate = null, endDate = null) }
        load()
    }

    class Factory(private val repository: PaymentRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentHistoryViewModel(repository) as T
        }
    }
}
