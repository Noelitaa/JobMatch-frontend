package com.moviles.jobmatch.data.remote.model

// Request body for POST /payments
data class CreatePaymentRequest(
    val idContract: Int,
    val amount: Double,
    val paymentMethod: String,        // "sinpe", "cash", "transfer"
    val receipt: String               // URL or detail
)

// Response from POST /payments
data class CreatePaymentResponse(
    val idPayment: Int,
    val idContract: Int,
    val amount: Double,
    val paymentMethod: String,
    val status: String,               // "COMPLETED", "PENDING"
    val createdAt: String
)
