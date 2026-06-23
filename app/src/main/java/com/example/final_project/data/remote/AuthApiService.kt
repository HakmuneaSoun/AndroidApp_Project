package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.LoginData
import com.example.final_project.data.remote.dto.LoginRequest
import com.example.final_project.data.remote.dto.RegisterRequest
import com.example.final_project.data.remote.dto.RegisterUserData
import com.example.final_project.data.remote.dto.UpdateProfileRequest
import com.example.final_project.data.remote.dto.UserProfileData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterUserData>

    @GET("api/auth/me")
    suspend fun getMe(): ApiResponse<UserProfileData>

    @PUT("api/auth/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<UserProfileData>
}
