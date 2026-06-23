package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.TourData
import com.example.final_project.data.remote.dto.TourRequest
import com.example.final_project.data.remote.dto.UpdateTourRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TourApiService {

    @GET("api/tours")
    suspend fun getTours(): ApiResponse<List<TourData>>

    @GET("api/tours/{id}")
    suspend fun getTourById(@Path("id") id: Long): ApiResponse<TourData>

    @POST("api/admin/tours")
    suspend fun createTour(@Body request: TourRequest): ApiResponse<TourData>

    @PUT("api/admin/tours/{id}")
    suspend fun updateTour(
        @Path("id") id: Long,
        @Body request: UpdateTourRequest
    ): ApiResponse<TourData>

    @DELETE("api/admin/tours/{id}")
    suspend fun deleteTour(@Path("id") id: Long): ApiResponse<TourData?>
}
