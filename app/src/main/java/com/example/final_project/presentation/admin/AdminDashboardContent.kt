package com.example.final_project.presentation.admin

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.final_project.R
import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.dto.BookingChartPoint
import com.example.final_project.data.remote.dto.DashboardRecentBookingData
import kotlin.math.ceil

private val StatBlue = Color(0xFF2196F3)
private val StatPurple = Color(0xFF5E35B1)
private val StatGreen = Color(0xFF16A34A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    onViewAllBookings: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel(
        factory = AdminDashboardViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val state = viewModel.uiState

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminTheme.Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (state.isLoading && state.stats == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StatPurple)
                }
            }
        } else {
            state.errorMessage?.let { message ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = message, color = Color(0xFFC62828), modifier = Modifier.weight(1f))
                            TextButton(onClick = { viewModel.loadDashboard() }) {
                                Text("Retry", color = StatPurple)
                            }
                        }
                    }
                }
            }

            state.stats?.let { stats ->
                item {
                    DashboardStatsGrid(
                        totalUsers = stats.totalUsers,
                        totalTours = stats.totalTours,
                        totalBookings = stats.totalBookings,
                        totalRevenue = stats.totalRevenue
                    )
                }
            }

            item {
                BookingsOverviewCard(
                    selectedPeriod = state.selectedPeriod,
                    onPeriodChange = viewModel::changePeriod,
                    chartData = state.chartData,
                    isLoading = state.isLoading
                )
            }

            item {
                RecentBookingsSection(
                    bookings = state.recentBookings,
                    onViewAll = onViewAllBookings
                )
            }
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
    selectedPeriod: DashboardPeriod,
    onPeriodChange: (DashboardPeriod) -> Unit,
    chartData: List<BookingChartPoint>,
    isLoading: Boolean
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
                                text = selectedPeriod.label,
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
                        DashboardPeriod.entries.forEach { period ->
                            DropdownMenuItem(
                                text = { Text(period.label) },
                                onClick = {
                                    onPeriodChange(period)
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && chartData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = StatPurple,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                BookingsLineChart(
                    chartData = chartData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }
    }
}

@Composable
private fun BookingsLineChart(
    chartData: List<BookingChartPoint>,
    modifier: Modifier = Modifier
) {
    val lineColor = StatPurple
    val gridColor = Color(0xFFE5E7EB)
    val labelColor = AdminTheme.TextSecondary

    val data = chartData.map { it.bookingCount.toFloat() }
    val maxCount = data.maxOrNull() ?: 0f
    val yMax = when {
        maxCount <= 0f -> 4f
        maxCount <= 4f -> 4f
        else -> ceil(maxCount / 4f) * 4f
    }
    val ySteps = listOf(0f, yMax * 0.25f, yMax * 0.5f, yMax * 0.75f, yMax)
    val yLabels = ySteps.map { label ->
        if (label == label.toLong().toFloat()) label.toLong().toString()
        else String.format("%.1f", label)
    }.reversed()

    val xLabels = buildChartXLabels(chartData)

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
                } else if (data.size == 1) {
                    val x = chartLeft + chartWidth / 2f
                    val y = chartBottom - (data.first().coerceIn(0f, yMax) / yMax) * chartHeight
                    drawCircle(color = AdminTheme.Surface, radius = 6f, center = Offset(x, y))
                    drawCircle(color = lineColor, radius = 4f, center = Offset(x, y))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                yLabels.forEach { label ->
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

private fun buildChartXLabels(chartData: List<BookingChartPoint>): List<String> {
    if (chartData.isEmpty()) return emptyList()
    if (chartData.size <= 6) {
        return chartData.map { formatChartDateLabel(it.date) }
    }
    val indices = listOf(0, chartData.size / 5, 2 * chartData.size / 5, 3 * chartData.size / 5, 4 * chartData.size / 5, chartData.lastIndex)
    return indices.distinct().map { formatChartDateLabel(chartData[it].date) }
}

private fun formatChartDateLabel(date: String): String {
    val day = date.substringAfterLast('-', date)
    return day.trimStart('0').ifEmpty { day }
}

@Composable
private fun RecentBookingsSection(
    bookings: List<DashboardRecentBookingData>,
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

        if (bookings.isEmpty()) {
            Text(
                text = "No recent bookings",
                fontSize = 14.sp,
                color = AdminTheme.TextSecondary
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                bookings.forEach { booking ->
                    RecentBookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
private fun RecentBookingCard(booking: DashboardRecentBookingData) {
    val imageUrl = ApiConstants.resolveMediaUrl(booking.tourImage)
    val statusColor = when (booking.status.uppercase()) {
        "CONFIRMED" -> Color(0xFF2196F3)
        "PENDING" -> Color(0xFFFF9800)
        "COMPLETED" -> StatGreen
        "CANCELLED" -> Color(0xFFE53935)
        else -> AdminTheme.TextSecondary
    }

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
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = booking.tourTitle,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.angkorwat),
                    error = painterResource(R.drawable.angkorwat)
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.angkorwat),
                    contentDescription = booking.tourTitle,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.tourTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminTheme.TextPrimary
                )
                Text(
                    text = "${booking.tourDate} • ${booking.peopleCount} People",
                    fontSize = 13.sp,
                    color = AdminTheme.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = booking.status.replace('_', ' '),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(
                text = "$${String.format("%,.2f", booking.totalPrice)}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AdminTheme.TextPrimary
            )
        }
    }
}
