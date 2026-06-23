package com.example.final_project.presentation.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.final_project.R
import com.example.final_project.domain.model.CategoryOption
import com.example.final_project.domain.model.Destination
import com.example.final_project.presentation.common.DestinationImage
import com.example.final_project.presentation.tours.TourFormData
import android.net.Uri
import coil.compose.AsyncImage
import com.example.final_project.data.remote.ApiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminToursScreen(
    modifier: Modifier = Modifier,
    destinations: List<Destination>,
    isLoading: Boolean = false,
    isDeletingTourId: Long? = null,
    errorMessage: String? = null,
    successMessage: String? = null,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddTour: () -> Unit,
    onEditTour: (Destination) -> Unit,
    onDeleteTour: (Destination) -> Unit,
    onDismissMessage: () -> Unit = {}
) {
    var tourToDelete by remember { mutableStateOf<Destination?>(null) }

    tourToDelete?.let { tour ->
        AlertDialog(
            onDismissRequest = { tourToDelete = null },
            title = { Text("Delete tour?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete \"${tour.name}\"? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTour(tour)
                        tourToDelete = null
                    }
                ) {
                    Text("Delete", color = AdminTheme.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tourToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    val filtered = remember(destinations, searchQuery) {
        if (searchQuery.isBlank()) destinations
        else destinations.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.province.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTheme.Background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChange(it)
                onDismissMessage()
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search tours...", color = AdminTheme.TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = AdminTheme.TextSecondary)
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AdminTheme.Primary,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = AdminTheme.Surface,
                unfocusedContainerColor = AdminTheme.Surface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Tours (${filtered.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AdminTheme.TextPrimary
            )
            FilledTonalButton(
                onClick = onAddTour,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = AdminTheme.Primary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Tour", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (errorMessage != null) {
            Text(errorMessage, fontSize = 12.sp, color = AdminTheme.Error, modifier = Modifier.padding(bottom = 8.dp))
        }
        if (successMessage != null) {
            Text(successMessage, fontSize = 12.sp, color = Color(0xFF4CAF50), modifier = Modifier.padding(bottom = 8.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AdminTheme.Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered, key = { it.id }) { destination ->
                    TourListCard(
                        destination = destination,
                        isDeleting = isDeletingTourId?.toString() == destination.id,
                        onEdit = { onEditTour(destination) },
                        onDelete = { tourToDelete = destination }
                    )
                }
            }
        }
    }
}

@Composable
private fun TourListCard(
    destination: Destination,
    isDeleting: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AdminTheme.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DestinationImage(
                destination = destination,
                contentDescription = destination.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(destination.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    "${destination.category} • ${destination.province}",
                    fontSize = 12.sp,
                    color = AdminTheme.TextSecondary
                )
                Text(
                    "$${destination.price.toInt()} • ⭐ ${destination.rating}",
                    fontSize = 12.sp,
                    color = AdminTheme.Primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = AdminTheme.Primary,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AdminTheme.Primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AdminTheme.Error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddTourScreen(
    modifier: Modifier = Modifier,
    initialDestination: Destination?,
    categories: List<CategoryOption>,
    imageCoverUrl: String?,
    isSaving: Boolean = false,
    isUploadingImage: Boolean = false,
    errorMessage: String? = null,
    onBack: () -> Unit,
    onUploadImage: (Uri) -> Unit,
    onSave: (TourFormData, Long?) -> Unit
) {
    val isEditMode = initialDestination != null

    var name by remember(initialDestination) { mutableStateOf(initialDestination?.name ?: "") }
    var selectedCategoryId by remember(initialDestination, categories) {
        mutableStateOf(
            initialDestination?.categoryId ?: categories.firstOrNull()?.id ?: 1L
        )
    }
    var location by remember(initialDestination) { mutableStateOf(initialDestination?.province ?: "") }
    var duration by remember(initialDestination) { mutableStateOf((initialDestination?.durationHours ?: 8).toString()) }
    var maxPeople by remember(initialDestination) { mutableStateOf((initialDestination?.maxPeople ?: 20).toString()) }
    var price by remember(initialDestination) { mutableStateOf(initialDestination?.price?.toString() ?: "45") }
    var latitude by remember(initialDestination) { mutableStateOf(initialDestination?.latitude?.toString() ?: "0") }
    var longitude by remember(initialDestination) { mutableStateOf(initialDestination?.longitude?.toString() ?: "0") }
    var description by remember(initialDestination) { mutableStateOf(initialDestination?.description ?: "") }
    var isActive by remember(initialDestination) { mutableStateOf(initialDestination?.isActive ?: true) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name.orEmpty()
    val resolvedImageUrl = ApiConstants.resolveMediaUrl(imageCoverUrl ?: initialDestination?.imageUrl)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(onUploadImage) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTheme.Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (!resolvedImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = resolvedImageUrl,
                    contentDescription = "Tour image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = initialDestination?.imageRes ?: R.drawable.angkorwat1),
                    contentDescription = "Tour image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            if (isUploadingImage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(enabled = !isUploadingImage) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Upload image", tint = AdminTheme.Primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tour Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = adminFieldColors()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name) },
                        onClick = {
                            selectedCategoryId = option.id
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AdminTheme.Primary) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (h)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = adminFieldColors(),
                singleLine = true
            )
            OutlinedTextField(
                value = maxPeople,
                onValueChange = { maxPeople = it },
                label = { Text("Max People") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = adminFieldColors(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price ($)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = adminFieldColors(),
                singleLine = true
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = adminFieldColors(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            minLines = 4
        )

        if (isEditMode) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminTheme.Surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tour active", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        if (isActive) "Visible to users" else "Hidden from users",
                        fontSize = 12.sp,
                        color = AdminTheme.TextSecondary
                    )
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = { isActive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AdminTheme.Primary
                    )
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(errorMessage, color = AdminTheme.Error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val imageCover = imageCoverUrl ?: initialDestination?.imageUrl.orEmpty()
                    if (name.isBlank() || imageCover.isBlank()) return@Button
                    onSave(
                        TourFormData(
                            title = name,
                            description = description,
                            location = location.ifBlank { "Cambodia" },
                            latitude = latitude.toDoubleOrNull() ?: 0.0,
                            longitude = longitude.toDoubleOrNull() ?: 0.0,
                            price = price.toDoubleOrNull() ?: 0.0,
                            categoryId = selectedCategoryId,
                            durationHours = duration.toIntOrNull() ?: 1,
                            maxPeople = maxPeople.toIntOrNull() ?: 1,
                            imageCover = imageCover,
                            isActive = isActive
                        ),
                        initialDestination?.id?.toLongOrNull()
                    )
                },
                enabled = !isSaving && !isUploadingImage,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdminTheme.Primary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isEditMode) "Update Tour" else "Save Tour", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun adminFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AdminTheme.Primary,
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedContainerColor = AdminTheme.Surface,
    unfocusedContainerColor = AdminTheme.Surface,
    focusedTextColor = AdminTheme.TextPrimary,
    unfocusedTextColor = AdminTheme.TextPrimary
)
