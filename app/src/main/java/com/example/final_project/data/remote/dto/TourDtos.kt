package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TourData(
    @SerializedName("tourId") val tourId: Long,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("category_name") val categoryName: String,
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("duration_hours") val durationHours: Int,
    val price: Double,
    @SerializedName("max_people") val maxPeople: Int,
    @SerializedName("image_cover") val imageCover: String?,
    @SerializedName("avg_rating") val avgRating: Double,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class TourRequest(
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("duration_hours") val durationHours: Int,
    @SerializedName("max_people") val maxPeople: Int,
    @SerializedName("image_cover") val imageCover: String
)

data class UpdateTourRequest(
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("duration_hours") val durationHours: Int,
    @SerializedName("max_people") val maxPeople: Int,
    @SerializedName("image_cover") val imageCover: String,
    @SerializedName("is_active") val isActive: Boolean
)
