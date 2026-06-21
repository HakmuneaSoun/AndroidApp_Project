package com.example.final_project.presentation.admin

import androidx.compose.ui.graphics.Color

object AdminTheme {
    val Primary = Color(0xFF5E35B1)
    val PrimaryDark = Color(0xFF4527A0)
    val PrimaryLight = Color(0xFFEDE7F6)
    val Accent = Color(0xFF667eea)
    val Background = Color(0xFFF8F9FC)
    val Surface = Color.White
    val TextPrimary = Color(0xFF1A1A2E)
    val TextSecondary = Color(0xFF6B7280)
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFE53935)
}

sealed class AdminScreen(val title: String) {
    data object Dashboard : AdminScreen("Admin Dashboard")
    data object Tours : AdminScreen("Tours")
    data object AddTour : AdminScreen("Add New Tour")
    data object EditTour : AdminScreen("Edit Tour")
    data object Bookings : AdminScreen("Manage Bookings")
    data object Users : AdminScreen("Users")
    data object Categories : AdminScreen("Tour Categories")
    data object Notifications : AdminScreen("Notifications")
    data object Profile : AdminScreen("Edit Profile")
}
