package com.example.final_project.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

enum class NotificationType {
    Booking,
    Payment,
    Offer,
    System,
    User,
    Review,
    Alert
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val isForAdmin: Boolean = false
)

val mockUserNotifications = listOf(
    AppNotification(
        id = "u1",
        title = "Booking Confirmed",
        message = "Your Angkor Wat Full Day Tour on 25 May 2024 is confirmed. Get ready for an amazing trip!",
        time = "2 min ago",
        type = NotificationType.Booking
    ),
    AppNotification(
        id = "u2",
        title = "Payment Successful",
        message = "We received your payment of $90 for Angkor Wat Full Day Tour.",
        time = "5 min ago",
        type = NotificationType.Payment
    ),
    AppNotification(
        id = "u3",
        title = "Special Offer",
        message = "Get 20% off on Koh Rong Island tours this weekend. Book now!",
        time = "1 hour ago",
        type = NotificationType.Offer
    ),
    AppNotification(
        id = "u4",
        title = "Tour Reminder",
        message = "Your Bayon Temple tour starts tomorrow at 8:00 AM. Don't forget your camera!",
        time = "3 hours ago",
        type = NotificationType.Booking,
        isRead = true
    ),
    AppNotification(
        id = "u5",
        title = "New Destination Added",
        message = "Explore the newly added Royal Palace experience in Phnom Penh.",
        time = "Yesterday",
        type = NotificationType.Offer,
        isRead = true
    )
)

val mockAdminNotifications = listOf(
    AppNotification(
        id = "a1",
        title = "New Booking",
        message = "Sokha Pich booked Angkor Wat Full Day Tour for 2 people on 25 May 2024.",
        time = "Just now",
        type = NotificationType.Booking,
        isForAdmin = true
    ),
    AppNotification(
        id = "a2",
        title = "Payment Received",
        message = "Payment of $90 received from Sokha Pich via Visa / MasterCard.",
        time = "3 min ago",
        type = NotificationType.Payment,
        isForAdmin = true
    ),
    AppNotification(
        id = "a3",
        title = "New User Registered",
        message = "Dara Chan just created a new account with email dara.chan@gmail.com.",
        time = "15 min ago",
        type = NotificationType.User,
        isForAdmin = true
    ),
    AppNotification(
        id = "a4",
        title = "Booking Cancelled",
        message = "Bopha Lim cancelled the Phnom Penh City Tour scheduled for 12 May 2024.",
        time = "1 hour ago",
        type = NotificationType.Alert,
        isForAdmin = true,
        isRead = true
    ),
    AppNotification(
        id = "a5",
        title = "Low Tour Inventory",
        message = "Koh Rong Island Escape has only 2 slots left for this weekend.",
        time = "2 hours ago",
        type = NotificationType.System,
        isForAdmin = true,
        isRead = true
    ),
    AppNotification(
        id = "a6",
        title = "New Review",
        message = "Vanna Heng left a 5-star review on Angkor Wat Full Day Tour.",
        time = "Yesterday",
        type = NotificationType.Review,
        isForAdmin = true,
        isRead = true
    )
)

fun iconForNotificationType(type: NotificationType): ImageVector = when (type) {
    NotificationType.Booking -> Icons.Default.ConfirmationNumber
    NotificationType.Payment -> Icons.Default.Payments
    NotificationType.Offer -> Icons.Default.LocalOffer
    NotificationType.System -> Icons.Default.Info
    NotificationType.User -> Icons.Default.PersonAdd
    NotificationType.Review -> Icons.Default.Star
    NotificationType.Alert -> Icons.Default.Warning
}

fun colorForNotificationType(type: NotificationType, isAdmin: Boolean): Long = when (type) {
    NotificationType.Booking -> if (isAdmin) 0xFF5E35B1 else 0xFF667eea
    NotificationType.Payment -> 0xFF16A34A
    NotificationType.Offer -> 0xFFFF9800
    NotificationType.System -> 0xFF6B7280
    NotificationType.User -> 0xFF2196F3
    NotificationType.Review -> 0xFFFFD700
    NotificationType.Alert -> 0xFFE53935
}
