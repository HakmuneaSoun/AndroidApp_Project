package com.example.final_project.domain.model

enum class BookingStatus {
    Upcoming,
    Completed,
    Cancelled
}

data class AdminUser(
    val id: String,
    val name: String,
    val email: String
)

data class AdminBooking(
    val id: String,
    val customerName: String,
    val tourName: String,
    val date: String,
    val people: Int,
    val status: BookingStatus,
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

val mockAdminBookings = listOf(
    AdminBooking("1", "Sokha Pich", "Angkor Wat Full Day Tour", "25 May 2024", 2, BookingStatus.Upcoming, 90.0),
    AdminBooking("2", "Dara Chan", "Koh Rong Island Escape", "18 May 2024", 4, BookingStatus.Completed, 180.0),
    AdminBooking("3", "Bopha Lim", "Phnom Penh City Tour", "12 May 2024", 1, BookingStatus.Cancelled, 45.0),
    AdminBooking("4", "Vanna Heng", "Bayon Temple Sunrise", "28 May 2024", 3, BookingStatus.Upcoming, 120.0),
    AdminBooking("5", "Kosal Nhem", "Royal Palace Visit", "10 May 2024", 2, BookingStatus.Completed, 60.0)
)
