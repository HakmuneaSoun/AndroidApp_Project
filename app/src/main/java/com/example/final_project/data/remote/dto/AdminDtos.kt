package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserStatusRequest(
    @SerializedName("is_active") val isActive: Boolean
)

data class AdminBookingData(
    val id: Long,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("user_name") val userName: String,
    @SerializedName("tour_id") val tourId: Long,
    @SerializedName("tour_title") val tourTitle: String,
    @SerializedName("tour_date") val tourDate: String,
    @SerializedName("people_count") val peopleCount: Int,
    @SerializedName("total_price") val totalPrice: Double,
    val status: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class UpdateBookingStatusRequest(
    val status: String
)
