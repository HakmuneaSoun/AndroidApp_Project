package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.LoginData
import com.example.final_project.data.remote.dto.LoginRequest
import com.example.final_project.data.remote.dto.RegisterRequest
import com.example.final_project.data.remote.dto.RegisterUserData
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterUserData>

    @POST("api/auth/logout")
    suspend fun logout(): ApiResponse<Unit?>
}
