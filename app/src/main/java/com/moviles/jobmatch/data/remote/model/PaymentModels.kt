package com.moviles.jobmatch.data.remote.model

data class PaymentResponse(
    val idPayment: Int,
    val idContract: Int,
    val amount: Double,
    val paymentMethod: String,
    val date: String,
    val type: String,
    val receiptUrl: String? = null,
    val concept: String? = null
)

// Request body for POST /payments
data class CreatePaymentRequest(
    val idContract: Int,
    val amount: Double,
    val paymentMethod: String,
    val receipt: String
)

// Response from POST /payments
data class CreatePaymentResponse(
    val idPayment: Int,
    val idContract: Int,
    val amount: Double,
    val paymentMethod: String,
    val date: String,
    val type: String,
    val receiptUrl: String? = null,
    val concept: String? = null
)
