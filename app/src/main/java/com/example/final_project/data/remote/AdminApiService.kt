package com.example.final_project.data.remote

import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.AdminBookingData
import com.example.final_project.data.remote.dto.DashboardData
import com.example.final_project.data.remote.dto.UpdateBookingStatusRequest
import com.example.final_project.data.remote.dto.UpdateUserStatusRequest
import com.example.final_project.data.remote.dto.UserProfileData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApiService {

    @GET("api/admin/dashboard")
    suspend fun getDashboard(
        @Query("period") period: String,
        @Query("limit") limit: Int = 10
    ): ApiResponse<DashboardData>

    @GET("api/admin/users")
    suspend fun getUsers(): ApiResponse<List<UserProfileData>>

    @GET("api/admin/bookings")
    suspend fun getBookings(): ApiResponse<List<AdminBookingData>>

    @PATCH("api/admin/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") bookingId: Long,
        @Body request: UpdateBookingStatusRequest
    ): ApiResponse<AdminBookingData>

    @PATCH("api/admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: Long,
        @Body request: UpdateUserStatusRequest
    ): ApiResponse<UserProfileData>
}
