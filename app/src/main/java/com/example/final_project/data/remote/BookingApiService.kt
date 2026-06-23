package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.BookingData
import com.example.final_project.data.remote.dto.CreateBookingRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApiService {

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): ApiResponse<BookingData>
}
