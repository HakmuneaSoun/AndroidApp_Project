package com.example.final_project.presentation.booking

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.domain.model.BookingDraft
import com.example.final_project.domain.model.Destination

private val ScreenBg = Color(0xFFF8F9FC)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val ActionGreen = Color(0xFF16A34A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    destination: Destination,
    booking: BookingDraft,
    onBookingChange: (BookingDraft) -> Unit,
    onNavigateBack: () -> Unit,
    onContinueToPayment: () -> Unit
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showTravelersDialog by remember { mutableStateOf(false) }
    var showPickupDialog by remember { mutableStateOf(false) }

    val totalPrice = booking.totalPrice(destination.price)

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
                        Image(
                            painter = painterResource(id = destination.imageRes),
                            contentDescription = destination.name,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "${destination.name} Full Day Tour",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$${destination.price.toInt()} / Person",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BookingOptionRow(
                    label = "Date",
                    value = booking.date,
                    onClick = { showDateDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                BookingOptionRow(
                    label = "Travelers",
                    value = "${booking.travelers} Adults",
                    onClick = { showTravelersDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                BookingOptionRow(
                    label = "Pick-up Location",
                    value = booking.pickupLocation,
                    onClick = { showPickupDialog = true }
                )
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
                    Text("Total Price", fontSize = 15.sp, color = TextSecondary)
                    Text(
                        text = "$${totalPrice.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onContinueToPayment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Continue to Payment", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showDateDialog) {
        val dates = listOf("25 May 2024", "26 May 2024", "27 May 2024", "28 May 2024")
        SelectionDialog(
            title = "Select Date",
            options = dates,
            selected = booking.date,
            onSelect = {
                onBookingChange(booking.copy(date = it))
                showDateDialog = false
            },
            onDismiss = { showDateDialog = false }
        )
    }

    if (showTravelersDialog) {
        val options = (1..6).map { "$it Adults" }
        SelectionDialog(
            title = "Number of Travelers",
            options = options,
            selected = "${booking.travelers} Adults",
            onSelect = {
                val count = it.substringBefore(" ").toIntOrNull() ?: booking.travelers
                onBookingChange(booking.copy(travelers = count))
                showTravelersDialog = false
            },
            onDismiss = { showTravelersDialog = false }
        )
    }

    if (showPickupDialog) {
        val locations = listOf(
            "Your Hotel in Phnom Penh",
            "Siem Reap Airport",
            "Phnom Penh Airport",
            "Central Market, Phnom Penh"
        )
        SelectionDialog(
            title = "Pick-up Location",
            options = locations,
            selected = booking.pickupLocation,
            onSelect = {
                onBookingChange(booking.copy(pickupLocation = it))
                showPickupDialog = false
            },
            onDismiss = { showPickupDialog = false }
        )
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
