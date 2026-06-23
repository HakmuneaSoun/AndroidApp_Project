package com.example.final_project.domain.model

data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "",
    val isActive: Boolean = true
)

data class AdminBooking(
    val id: Long,
    val bookingCode: String = "",
    val userId: Long = 0,
    val customerName: String,
    val tourId: Long = 0,
    val tourName: String,
    val date: String,
    val people: Int,
    val status: String,
    val price: Double
)

val mockAdminUsers = listOf(
    AdminUser("1", "Sokha Pich", "sokha@gmail.com"),
    AdminUser("2", "Dara Chan", "dara.chan@gmail.com"),
    AdminUser("3", "Bopha Lim", "bopha.l@gmail.com"),
    AdminUser("4", "Vanna Heng", "vanna.h@gmail.com"),
    AdminUser("5", "Kosal Nhem", "kosal.n@gmail.com"),
    AdminUser("6", "Ratha Keo", "ratha.k@gmail.com")
)
