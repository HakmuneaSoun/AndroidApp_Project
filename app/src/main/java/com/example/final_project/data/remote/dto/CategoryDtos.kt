package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryData(
    @SerializedName("categoryId") val categoryId: Long,
    val name: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class CreateCategoryRequest(
    val name: String
)
