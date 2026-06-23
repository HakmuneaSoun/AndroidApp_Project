package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val status: Int,
    val timestamp: String?,
    val data: T?,
    val errors: Map<String, String>?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val role: String = "ROLE_USER"
)

data class LoginData(
    val token: String,
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("created_at") val createdAt: String?
)

data class RegisterUserData(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
