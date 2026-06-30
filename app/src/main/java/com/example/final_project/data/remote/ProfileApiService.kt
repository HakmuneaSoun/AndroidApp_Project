package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.UpdateProfileRequest
import com.example.final_project.data.remote.dto.UserProfileData
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileApiService {

    @GET("api/user/profile")
    suspend fun getProfile(): ApiResponse<UserProfileData>

    @PUT("api/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<UserProfileData>

    @Multipart
    @POST("api/user/profile/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): ApiResponse<UserProfileData>
}
