package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.CreateReviewRequest
import com.example.final_project.data.remote.dto.ReviewData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @GET("api/reviews/tour/{tourId}")
    suspend fun getReviewsByTour(@Path("tourId") tourId: Long): ApiResponse<List<ReviewData>>

    @POST("api/reviews")
    suspend fun createReview(@Body request: CreateReviewRequest): ApiResponse<ReviewData>
}
