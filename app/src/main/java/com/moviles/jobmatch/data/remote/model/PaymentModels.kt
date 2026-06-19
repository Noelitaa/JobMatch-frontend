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
