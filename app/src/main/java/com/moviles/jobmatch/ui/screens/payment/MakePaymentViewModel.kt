package com.moviles.jobmatch.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.CreatePaymentRequest
import com.moviles.jobmatch.data.remote.model.CreatePaymentResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Supported payment methods
enum class PaymentMethod(val label: String, val subtitle: String, val backendValue: String) {
    SINPE("SINPE Móvil", "Transferencia instantánea 24/7", "sinpe"),
    CASH("Efectivo", "Pago presencial", "cash"),
    BANK_TRANSFER("Transferencia Bancaria", "IBAN local (BCR, BN, BAC)", "transfer")
}

data class MakePaymentUiState(
    // --- form fields ---
    val amount: Double = 0.0,
    val amountString: String = "",
    val paymentDate: String = "",           // "YYYY-MM-DD"
    val selectedMethod: PaymentMethod = PaymentMethod.SINPE,

    // Reference/Receipt
    val receiptUrl: String = "",

    // --- metadata ---
    val jobId: Int = 0,
    val studentId: String = "",
    val jobTitle: String = "",
    val subtotal: Double = 0.0,
    val commission: Double = 0.0,

    // --- async state ---
    val isLoading: Boolean = false,
    val paymentSuccess: Boolean = false,
    val successResponse: CreatePaymentResponse? = null,
    val errorMessage: String? = null,

    // --- validation ---
    val fieldErrors: Map<String, String> = emptyMap()
)

class MakePaymentViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MakePaymentUiState())
    val uiState: StateFlow<MakePaymentUiState> = _uiState.asStateFlow()

    private var currentContractId: Int = 0

    // Called from the screen to seed job/contract data
    fun initWithJob(jobId: Int, studentId: String, jobTitle: String, amount: Double, contractNumber: String) {
        val parsedContractId = contractNumber.toIntOrNull()
        if (parsedContractId == null) {
            _uiState.update { it.copy(errorMessage = "Error: ID de contrato no válido ($contractNumber)") }
            return
        }
        currentContractId = parsedContractId
        val commission = amount * 0.06
        _uiState.update {
            it.copy(
                jobId = jobId,
                studentId = studentId,
                jobTitle = jobTitle,
                amount = amount,
                amountString = amount.toLong().toString(),
                subtotal = amount - commission,
                commission = commission,
                paymentDate = todayIso()
            )
        }
    }

    // --- field updaters ---

    fun onAmountChanged(v: String) {
        // Allow only digits to match toLong behavior, or confirm if decimal is needed. 
        // For now, keeping digits only as requested for simple entry.
        val filtered = v.filter { it.isDigit() }
        val amountValue = filtered.toDoubleOrNull() ?: 0.0
        val commissionValue = amountValue * 0.06
        _uiState.update {
            it.copy(
                amountString = filtered,
                amount = amountValue,
                subtotal = amountValue - commissionValue,
                commission = commissionValue
            )
        }
    }

    fun onMethodSelected(method: PaymentMethod) =
        _uiState.update { it.copy(selectedMethod = method, fieldErrors = emptyMap()) }

    fun onReceiptUrlChanged(v: String) =
        _uiState.update { it.copy(receiptUrl = v) }

    fun clearError() =
        _uiState.update { it.copy(errorMessage = null) }

    // --- submit ---

    fun submitPayment() {
        val state = _uiState.value
        val errors = validate(state)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val request = CreatePaymentRequest(
                idContract = currentContractId,
                amount = state.amount,
                paymentMethod = state.selectedMethod.backendValue,
                receipt = state.receiptUrl
            )
            
            when (val result = paymentRepository.createPayment(request)) {
                is ApiResult.Success ->
                    _uiState.update {
                        it.copy(isLoading = false, paymentSuccess = true, successResponse = result.data)
                    }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    // --- helpers ---

    private fun validate(s: MakePaymentUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (s.amount <= 0) {
            errors["amount"] = "Ingrese un monto válido"
        }
        if (s.receiptUrl.isBlank()) {
            errors["receiptUrl"] = "La referencia o comprobante es requerido"
        }
        return errors
    }

    private fun buildMethodDetail(s: MakePaymentUiState): String = s.receiptUrl

    private fun todayIso(): String {
        val c = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            c.get(java.util.Calendar.YEAR),
            c.get(java.util.Calendar.MONTH) + 1,
            c.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }
}

class MakePaymentViewModelFactory(
    private val paymentRepository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MakePaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MakePaymentViewModel(paymentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}