package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.CreatePaymentRequest
import com.example.final_project.data.remote.dto.PaymentData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiService {

    @POST("api/payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): ApiResponse<PaymentData>

    @GET("api/payments/booking/{bookingId}")
    suspend fun getPaymentByBooking(@Path("bookingId") bookingId: Long): ApiResponse<PaymentData>
}
