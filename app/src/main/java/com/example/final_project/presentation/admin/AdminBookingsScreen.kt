package com.example.final_project.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.domain.model.AdminBooking

private val bookingTabs = listOf("All", "Pending", "Confirmed", "Completed", "Cancelled")

@Composable
fun AdminBookingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminBookingsViewModel = viewModel(
        factory = AdminBookingsViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState = viewModel.uiState
    var selectedTab by remember { mutableStateOf("All") }
    var showMenuForBookingId by remember { mutableStateOf<Long?>(null) }

    val filteredBookings = remember(selectedTab, uiState.bookings) {
        when (selectedTab) {
            "Pending" -> uiState.bookings.filter { it.status.equals("PENDING", ignoreCase = true) }
            "Confirmed" -> uiState.bookings.filter { it.status.equals("CONFIRMED", ignoreCase = true) }
            "Completed" -> uiState.bookings.filter { it.status.equals("COMPLETED", ignoreCase = true) }
            "Cancelled" -> uiState.bookings.filter { it.status.equals("CANCELLED", ignoreCase = true) }
            else -> uiState.bookings
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTheme.Background)
    ) {
        ScrollableTabRow(
            selectedTabIndex = bookingTabs.indexOf(selectedTab),
            containerColor = AdminTheme.Surface,
            contentColor = AdminTheme.Primary,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = Color(0xFFE5E7EB)) },
            indicator = { tabPositions ->
                val index = bookingTabs.indexOf(selectedTab)
                if (index in tabPositions.indices) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[index])
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
                        color = AdminTheme.Primary
                    )
                }
            }
        ) {
            bookingTabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = {
                        selectedTab = tab
                        viewModel.clearMessages()
                    },
                    text = {
                        Text(
                            text = tab,
                            fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    selectedContentColor = AdminTheme.Primary,
                    unselectedContentColor = AdminTheme.TextSecondary
                )
            }
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                fontSize = 12.sp,
                color = AdminTheme.Error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (uiState.successMessage != null) {
            Text(
                text = uiState.successMessage,
                fontSize = 12.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AdminTheme.Primary)
            }
        } else if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No bookings found", color = AdminTheme.TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings, key = { it.id }) { booking ->
                    BookingCard(
                        booking = booking,
                        isUpdating = uiState.isUpdatingBookingId == booking.id,
                        showMenu = showMenuForBookingId == booking.id,
                        onMenuClick = {
                            showMenuForBookingId = if (showMenuForBookingId == booking.id) null else booking.id
                        },
                        onDismissMenu = { showMenuForBookingId = null },
                        onStatusChange = { status ->
                            showMenuForBookingId = null
                            viewModel.updateBookingStatus(booking.id, status)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: AdminBooking,
    isUpdating: Boolean,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Surface(
//                modifier = Modifier.size(48.dp),
//                shape = RoundedCornerShape(12.dp),
//                color = AdminTheme.PrimaryLight
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    Icon(
//                        Icons.Default.Tour,
//                        contentDescription = null,
//                        tint = AdminTheme.Primary,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.customerName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminTheme.TextPrimary
                )
                Text(
                    text = booking.tourName,
                    fontSize = 13.sp,
                    color = AdminTheme.TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (booking.bookingCode.isNotBlank()) {
                    Text(
                        text = booking.bookingCode,
                        fontSize = 11.sp,
                        color = AdminTheme.TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text(
                    text = "${booking.date} • ${booking.people} People",
                    fontSize = 12.sp,
                    color = AdminTheme.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BookingStatusBadge(status = booking.status)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%,.2f", booking.price)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminTheme.TextPrimary
                )

                if (isUpdating) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AdminTheme.Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Box {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Change status",
                                tint = AdminTheme.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = onDismissMenu
                        ) {
                            bookingStatusOptions.forEach { status ->
                                if (!status.equals(booking.status, ignoreCase = true)) {
                                    DropdownMenuItem(
                                        text = { Text(formatStatusLabel(status)) },
                                        onClick = { onStatusChange(status) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingStatusBadge(status: String) {
    val (label, bg, fg) = when (status.uppercase()) {
        "PENDING" -> Triple("Pending", Color(0xFFFFF3E0), Color(0xFFE65100))
        "CONFIRMED" -> Triple("Confirmed", AdminTheme.PrimaryLight, AdminTheme.Primary)
        "COMPLETED" -> Triple("Completed", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "CANCELLED" -> Triple("Cancelled", Color(0xFFFFEBEE), AdminTheme.Error)
        else -> Triple(formatStatusLabel(status), Color(0xFFF3F4F6), AdminTheme.TextSecondary)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private fun formatStatusLabel(status: String): String {
    return status.lowercase().replaceFirstChar { it.uppercase() }
}
