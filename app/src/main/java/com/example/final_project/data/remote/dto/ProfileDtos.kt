package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserProfileData(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val role: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class UpdateProfileRequest(
    val fullName: String,
    val phone: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class UploadImageData(
    @SerializedName("image_url") val imageUrl: String
)

enum class UploadType {
    AVATAR,
    TOUR,
    IMAGE
}
