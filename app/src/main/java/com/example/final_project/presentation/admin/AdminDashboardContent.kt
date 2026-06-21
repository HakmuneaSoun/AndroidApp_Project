package com.example.final_project.presentation.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.R
import com.example.final_project.domain.model.AdminBooking
import com.example.final_project.domain.model.Destination
import com.example.final_project.domain.model.mockAdminBookings

private val StatBlue = Color(0xFF2196F3)
private val StatPurple = Color(0xFF5E35B1)
private val StatGreen = Color(0xFF16A34A)

private val chartFilters = listOf("This Month", "This Week", "This Year")

private val monthlyChartData = listOf(42f, 58f, 48f, 72f, 65f, 88f)
private val weeklyChartData = listOf(12f, 18f, 15f, 22f, 20f, 25f)
private val yearlyChartData = listOf(320f, 410f, 380f, 460f, 520f, 542f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    destinations: List<Destination>,
    bookings: List<AdminBooking> = mockAdminBookings,
    onViewAllBookings: () -> Unit
) {
    var selectedChartFilter by remember { mutableStateOf(chartFilters.first()) }

    val chartData = when (selectedChartFilter) {
        "This Week" -> weeklyChartData
        "This Year" -> yearlyChartData
        else -> monthlyChartData
    }

    val totalRevenue = 24_680.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminTheme.Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            DashboardStatsGrid(
                totalUsers = 1_248,
                totalTours = destinations.size,
                totalBookings = 542,
                totalRevenue = totalRevenue
            )
        }

        item {
            BookingsOverviewCard(
                selectedFilter = selectedChartFilter,
                onFilterChange = { selectedChartFilter = it },
                chartData = chartData
            )
        }

        item {
            RecentBookingsSection(
                bookings = bookings.take(4),
                destinations = destinations,
                onViewAll = onViewAllBookings
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun DashboardStatsGrid(
    totalUsers: Int,
    totalTours: Int,
    totalBookings: Int,
    totalRevenue: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                label = "Total Users",
                value = "%,d".format(totalUsers),
                valueColor = StatBlue,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                label = "Total Tours",
                value = totalTours.toString(),
                valueColor = StatPurple,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                label = "Total Bookings",
                value = totalBookings.toString(),
                valueColor = StatPurple,
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                label = "Total Revenue",
                value = "$${String.format("%,.0f", totalRevenue)}",
                valueColor = StatGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DashboardStatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = AdminTheme.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingsOverviewCard(
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    chartData: List<Float>
) {
    var filterExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bookings Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminTheme.TextPrimary
                )

                ExposedDropdownMenuBox(
                    expanded = filterExpanded,
                    onExpandedChange = { filterExpanded = it }
                ) {
                    Surface(
                        modifier = Modifier
                            .menuAnchor()
                            .clickable { filterExpanded = true },
                        shape = RoundedCornerShape(10.dp),
                        color = AdminTheme.Background
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedFilter,
                                fontSize = 13.sp,
                                color = AdminTheme.TextSecondary
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = AdminTheme.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        chartFilters.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter) },
                                onClick = {
                                    onFilterChange(filter)
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BookingsLineChart(
                data = chartData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
    }
}

@Composable
private fun BookingsLineChart(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    val lineColor = StatPurple
    val gridColor = Color(0xFFE5E7EB)
    val labelColor = AdminTheme.TextSecondary
    val xLabels = listOf("1st", "2nd", "3rd", "4th", "5th", "6th")
    val yMax = 100f

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartLeft = 36f
                val chartRight = size.width - 8f
                val chartTop = 8f
                val chartBottom = size.height - 8f
                val chartWidth = chartRight - chartLeft
                val chartHeight = chartBottom - chartTop

                val ySteps = listOf(0f, 25f, 50f, 75f, 100f)
                ySteps.forEach { step ->
                    val y = chartBottom - (step / yMax) * chartHeight
                    drawLine(
                        color = gridColor,
                        start = Offset(chartLeft, y),
                        end = Offset(chartRight, y),
                        strokeWidth = 1f
                    )
                }

                if (data.size >= 2) {
                    val points = data.mapIndexed { index, value ->
                        val x = chartLeft + (index.toFloat() / (data.size - 1)) * chartWidth
                        val y = chartBottom - (value.coerceIn(0f, yMax) / yMax) * chartHeight
                        Offset(x, y)
                    }

                    val fillPath = Path().apply {
                        moveTo(points.first().x, chartBottom)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, chartBottom)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.28f),
                                lineColor.copy(alpha = 0.04f)
                            ),
                            startY = chartTop,
                            endY = chartBottom
                        )
                    )

                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )

                    points.forEach { point ->
                        drawCircle(color = AdminTheme.Surface, radius = 6f, center = point)
                        drawCircle(color = lineColor, radius = 4f, center = point)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("100", "75", "50", "25", "0").forEach { label ->
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = labelColor,
                        modifier = Modifier.width(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            xLabels.forEach { label ->
                Text(text = label, fontSize = 10.sp, color = labelColor)
            }
        }
    }
}

@Composable
private fun RecentBookingsSection(
    bookings: List<AdminBooking>,
    destinations: List<Destination>,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Bookings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AdminTheme.TextPrimary
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = StatPurple,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            bookings.forEach { booking ->
                RecentBookingCard(
                    booking = booking,
                    imageRes = imageResForTour(booking.tourName, destinations)
                )
            }
        }
    }
}

@Composable
private fun RecentBookingCard(
    booking: AdminBooking,
    imageRes: Int
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
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = booking.tourName,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.tourName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminTheme.TextPrimary
                )
                Text(
                    text = "${booking.date} • ${booking.people} People",
                    fontSize = 13.sp,
                    color = AdminTheme.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = "$${booking.price.toInt()}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AdminTheme.TextPrimary
            )
        }
    }
}

private fun imageResForTour(tourName: String, destinations: List<Destination>): Int {
    val match = destinations.firstOrNull { destination ->
        tourName.contains(destination.name, ignoreCase = true)
    }
    return match?.imageRes ?: R.drawable.angkorwat
}
