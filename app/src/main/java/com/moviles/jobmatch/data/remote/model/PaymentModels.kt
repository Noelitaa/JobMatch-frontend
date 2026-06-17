package com.moviles.jobmatch.data.remote.model

// Request body for POST /payments
data class CreatePaymentRequest(
    val jobId: Int,
    val studentId: String,
    val amount: Double,
    val paymentDate: String,          // ISO-8601 format: "2024-08-15"
    val paymentMethod: String,        // "SINPE", "CARD", "BANK_TRANSFER"
    val paymentMethodDetail: String,  // phone / last 4 digits / IBAN
    val notes: String? = null
)

// Response from POST /payments
data class CreatePaymentResponse(
    val idPayment: Int,
    val jobId: Int,
    val studentId: String,
    val companyId: String,
    val amount: Double,
    val paymentDate: String,
    val paymentMethod: String,
    val paymentMethodDetail: String,
    val status: String,               // "COMPLETED", "PENDING"
    val createdAt: String
)