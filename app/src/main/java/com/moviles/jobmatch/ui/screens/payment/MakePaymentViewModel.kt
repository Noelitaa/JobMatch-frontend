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
enum class PaymentMethod(val label: String, val subtitle: String) {
    SINPE("SINPE Móvil", "Transferencia instantánea 24/7"),
    CARD("Tarjeta Débito/Crédito", "Visa, Mastercard o AMEX"),
    BANK_TRANSFER("Transferencia Bancaria", "IBAN local (BCR, BN, BAC)")
}

data class MakePaymentUiState(
    // --- form fields ---
    val amount: Double = 0.0,
    val paymentDate: String = "",           // "YYYY-MM-DD"
    val selectedMethod: PaymentMethod = PaymentMethod.SINPE,

    // SINPE
    val sinpePhone: String = "",

    // Card (invented — not backed by real processor)
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = "",
    val cardHolder: String = "",

    // Bank transfer
    val ibanNumber: String = "",

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

    // Called from the screen to seed job/contract data
    fun initWithJob(jobId: Int, studentId: String, jobTitle: String, amount: Double) {
        val commission = amount * 0.06
        _uiState.update {
            it.copy(
                jobId = jobId,
                studentId = studentId,
                jobTitle = jobTitle,
                amount = amount,
                subtotal = amount - commission,
                commission = commission,
                paymentDate = todayIso()
            )
        }
    }

    // --- field updaters ---

    fun onMethodSelected(method: PaymentMethod) =
        _uiState.update { it.copy(selectedMethod = method, fieldErrors = emptyMap()) }

    fun onSinpePhoneChanged(v: String) =
        _uiState.update { it.copy(sinpePhone = v) }

    fun onCardNumberChanged(v: String) =
        _uiState.update { it.copy(cardNumber = v) }

    fun onCardExpiryChanged(v: String) =
        _uiState.update { it.copy(cardExpiry = v) }

    fun onCardCvvChanged(v: String) =
        _uiState.update { it.copy(cardCvv = v) }

    fun onCardHolderChanged(v: String) =
        _uiState.update { it.copy(cardHolder = v) }

    fun onIbanChanged(v: String) =
        _uiState.update { it.copy(ibanNumber = v) }

    fun onDateChanged(v: String) =
        _uiState.update { it.copy(paymentDate = v) }

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

        val detail = buildMethodDetail(state)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val request = CreatePaymentRequest(
                jobId = state.jobId,
                studentId = state.studentId,
                amount = state.amount,
                paymentDate = state.paymentDate,
                paymentMethod = state.selectedMethod.name,
                paymentMethodDetail = detail
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
        when (s.selectedMethod) {
            PaymentMethod.SINPE -> {
                if (s.sinpePhone.length < 8)
                    errors["sinpePhone"] = "Enter a valid 8-digit phone number"
            }
            PaymentMethod.CARD -> {
                if (s.cardNumber.replace(" ", "").length < 16)
                    errors["cardNumber"] = "Enter a valid 16-digit card number"
                if (s.cardExpiry.length < 5)
                    errors["cardExpiry"] = "Enter expiry in MM/YY format"
                if (s.cardCvv.length < 3)
                    errors["cardCvv"] = "Enter a valid CVV"
                if (s.cardHolder.isBlank())
                    errors["cardHolder"] = "Enter the cardholder name"
            }
            PaymentMethod.BANK_TRANSFER -> {
                if (s.ibanNumber.replace(" ", "").length < 22)
                    errors["ibanNumber"] = "Enter a valid CR IBAN (22 characters)"
            }
        }
        return errors
    }

    private fun buildMethodDetail(s: MakePaymentUiState): String = when (s.selectedMethod) {
        PaymentMethod.SINPE -> s.sinpePhone
        PaymentMethod.CARD  -> "**** **** **** ${s.cardNumber.takeLast(4)}"
        PaymentMethod.BANK_TRANSFER -> s.ibanNumber
    }

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