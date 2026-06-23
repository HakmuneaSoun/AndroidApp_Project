package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.CategoryData
import com.example.final_project.data.remote.dto.CreateCategoryRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApiService {

    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<CategoryData>>

    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): ApiResponse<CategoryData>

    @POST("api/categories")
    suspend fun createCategory(@Body request: CreateCategoryRequest): ApiResponse<CategoryData>
}
