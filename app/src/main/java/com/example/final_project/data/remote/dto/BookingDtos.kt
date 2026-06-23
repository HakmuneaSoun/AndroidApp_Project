package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateBookingRequest(
    @SerializedName("tour_id") val tourId: Long,
    @SerializedName("tour_date") val tourDate: String,
    @SerializedName("people_count") val peopleCount: Int
)

data class BookingData(
    val id: Long,
    val status: String,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("user_name") val userName: String,
    @SerializedName("tour_id") val tourId: Long,
    @SerializedName("tour_title") val tourTitle: String,
    @SerializedName("tour_date") val tourDate: String,
    @SerializedName("people_count") val peopleCount: Int,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
