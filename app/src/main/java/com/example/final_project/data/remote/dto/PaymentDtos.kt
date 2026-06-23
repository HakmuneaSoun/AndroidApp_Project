package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequest(
    val method: String,
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("transaction_id") val transactionId: String
)

data class PaymentData(
    val id: Long,
    val amount: Double,
    val method: String,
    val status: String,
    @SerializedName("booking_id") val bookingId: Long,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("transaction_id") val transactionId: String,
    @SerializedName("paid_at") val paidAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
