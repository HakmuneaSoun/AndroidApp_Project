package com.example.final_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardData(
    val stats: DashboardStats,
    val period: String,
    @SerializedName("bookings_chart") val bookingsChart: List<BookingChartPoint>,
    @SerializedName("recent_bookings") val recentBookings: List<DashboardRecentBookingData>
)

data class DashboardStats(
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("total_tours") val totalTours: Int,
    @SerializedName("total_bookings") val totalBookings: Int,
    @SerializedName("total_revenue") val totalRevenue: Double
)

data class BookingChartPoint(
    val date: String,
    @SerializedName("booking_count") val bookingCount: Int
)

data class DashboardRecentBookingData(
    val id: Long,
    @SerializedName("tour_title") val tourTitle: String,
    @SerializedName("tour_image") val tourImage: String?,
    @SerializedName("tour_date") val tourDate: String,
    @SerializedName("people_count") val peopleCount: Int,
    @SerializedName("total_price") val totalPrice: Double,
    val status: String
)
