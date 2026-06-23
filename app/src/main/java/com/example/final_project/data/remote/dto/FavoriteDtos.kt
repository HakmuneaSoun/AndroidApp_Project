package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FavoriteTourData(
    val id: Long,
    @SerializedName("tour_id") val tourId: Long,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("category_name") val categoryName: String,
    val title: String,
    val description: String,
    val location: String,
    @SerializedName("duration_hours") val durationHours: Int,
    val price: Double,
    @SerializedName("max_people") val maxPeople: Int,
    @SerializedName("image_cover") val imageCover: String?,
    @SerializedName("avg_rating") val avgRating: Double,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_favorite") val isFavorite: Boolean = true,
    @SerializedName("favorited_at") val favoritedAt: String? = null
)

data class AddFavoriteRequest(
    @SerializedName("tour_id") val tourId: Long
)

data class FavoriteCheckData(
    @SerializedName("tour_id") val tourId: Long,
    @SerializedName("is_favorite") val isFavorite: Boolean
)
