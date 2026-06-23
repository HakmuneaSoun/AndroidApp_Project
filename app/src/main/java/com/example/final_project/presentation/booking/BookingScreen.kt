package com.example.final_project.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.domain.model.BookingDraft
import com.example.final_project.domain.model.Destination
import com.example.final_project.presentation.common.DestinationImage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val ScreenBg = Color(0xFFF8F9FC)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val ActionGreen = Color(0xFF16A34A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    destination: Destination,
    booking: BookingDraft,
    isSubmitting: Boolean,
    errorMessage: String?,
    onBookingChange: (BookingDraft) -> Unit,
    onNavigateBack: () -> Unit,
    onContinueToPayment: () -> Unit,
    onClearError: () -> Unit
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showTravelersDialog by remember { mutableStateOf(false) }

    val estimatedTotal = booking.totalPrice(destination.price)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Booking", fontWeight = FontWeight.Bold, color = TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg)
            )
        },
        containerColor = ScreenBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DestinationImage(
                            destination = destination,
                            contentDescription = destination.name,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = destination.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$${String.format("%.2f", destination.price)} / person",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BookingOptionRow(
                    label = "Tour Date",
                    value = formatDisplayDate(booking.tourDate),
                    onClick = { showDateDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                BookingOptionRow(
                    label = "Travelers",
                    value = "${booking.travelers} ${if (booking.travelers == 1) "Person" else "People"}",
                    onClick = { showTravelersDialog = true }
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFE53935),
                        fontSize = 13.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Estimated Total", fontSize = 15.sp, color = TextSecondary)
                    Text(
                        text = "$${String.format("%.2f", estimatedTotal)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        onClearError()
                        onContinueToPayment()
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionGreen,
                        contentColor = Color.White
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Continue to Payment", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showDateDialog) {
        val dates = remember { generateTourDates(30) }
        SelectionDialog(
            title = "Select Tour Date",
            options = dates.map { formatDisplayDate(it) },
            selected = formatDisplayDate(booking.tourDate),
            onSelect = { display ->
                val index = dates.indexOfFirst { formatDisplayDate(it) == display }
                if (index >= 0) {
                    onBookingChange(booking.copy(tourDate = dates[index]))
                }
                showDateDialog = false
            },
            onDismiss = { showDateDialog = false }
        )
    }

    if (showTravelersDialog) {
        val maxPeople = destination.maxPeople.coerceAtLeast(1)
        val options = (1..maxPeople).map {
            "$it ${if (it == 1) "Person" else "People"}"
        }
        SelectionDialog(
            title = "Number of Travelers",
            options = options,
            selected = "${booking.travelers} ${if (booking.travelers == 1) "Person" else "People"}",
            onSelect = {
                val count = it.substringBefore(" ").toIntOrNull() ?: booking.travelers
                onBookingChange(booking.copy(travelers = count))
                showTravelersDialog = false
            },
            onDismiss = { showTravelersDialog = false }
        )
    }
}

private fun generateTourDates(days: Int): List<String> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val calendar = Calendar.getInstance()
    return (0 until days).map { offset ->
        val day = calendar.clone() as Calendar
        day.add(Calendar.DAY_OF_MONTH, offset)
        formatter.format(day.time)
    }
}

private fun formatDisplayDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val display = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val date = parser.parse(isoDate)
        if (date != null) display.format(date) else isoDate
    } catch (_: Exception) {
        isoDate
    }
}

@Composable
private fun BookingOptionRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = TextSecondary)
                Text(
                    value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selected,
                            onClick = { onSelect(option) },
                            colors = RadioButtonDefaults.colors(selectedColor = ActionGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option, color = TextPrimary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ActionGreen)
            }
        }
    )
}
