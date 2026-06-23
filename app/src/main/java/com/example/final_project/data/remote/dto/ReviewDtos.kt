package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    @SerializedName("tour_id") val tourId: Long,
    val rating: Int,
    val comment: String
)

data class ReviewData(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("user_name") val userName: String,
    @SerializedName("tour_id") val tourId: Long,
    @SerializedName("tour_title") val tourTitle: String,
    val rating: Int,
    val comment: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
