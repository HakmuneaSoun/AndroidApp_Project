package com.example.final_project.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.domain.model.AdminBooking
import com.example.final_project.domain.model.BookingStatus
import com.example.final_project.domain.model.mockAdminBookings

private val bookingTabs = listOf("All", "Upcoming", "Completed", "Cancelled")

@Composable
fun AdminBookingsScreen(
    modifier: Modifier = Modifier,
    bookings: List<AdminBooking> = mockAdminBookings
) {
    var selectedTab by remember { mutableStateOf("All") }

    val filteredBookings = remember(selectedTab, bookings) {
        when (selectedTab) {
            "Upcoming" -> bookings.filter { it.status == BookingStatus.Upcoming }
            "Completed" -> bookings.filter { it.status == BookingStatus.Completed }
            "Cancelled" -> bookings.filter { it.status == BookingStatus.Cancelled }
            else -> bookings
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
                    onClick = { selectedTab = tab },
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredBookings, key = { it.id }) { booking ->
                BookingCard(booking = booking)
            }
        }
    }
}

@Composable
private fun BookingCard(booking: AdminBooking) {
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
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = AdminTheme.PrimaryLight
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Tour,
                        contentDescription = null,
                        tint = AdminTheme.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

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
                Text(
                    text = "${booking.date} • ${booking.people} People",
                    fontSize = 12.sp,
                    color = AdminTheme.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BookingStatusBadge(status = booking.status)
            }

            Text(
                text = "$${booking.price.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AdminTheme.TextPrimary
            )
        }
    }
}

@Composable
private fun BookingStatusBadge(status: BookingStatus) {
    val (label, bg, fg) = when (status) {
        BookingStatus.Upcoming -> Triple("Upcoming", AdminTheme.PrimaryLight, AdminTheme.Primary)
        BookingStatus.Completed -> Triple("Completed", Color(0xFFF3F4F6), AdminTheme.TextSecondary)
        BookingStatus.Cancelled -> Triple("Cancelled", Color(0xFFFFEBEE), AdminTheme.Error)
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
