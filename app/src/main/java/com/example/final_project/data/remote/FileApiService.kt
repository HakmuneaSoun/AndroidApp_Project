package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.UploadImageData
import com.example.final_project.data.remote.dto.UploadType
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface FileApiService {

    @Multipart
    @POST("api/files/upload")
    suspend fun uploadFile(
        @Query("type") type: UploadType,
        @Part file: MultipartBody.Part
    ): ApiResponse<UploadImageData>
}
