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
import com.example.final_project.domain.model.Destination

private enum class CategoryInputMode { Select, AddNew }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminToursScreen(
    modifier: Modifier = Modifier,
    destinations: List<Destination>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddTour: () -> Unit,
    onEditTour: (Destination) -> Unit,
    onDeleteTour: (Destination) -> Unit
) {
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
            onValueChange = onSearchQueryChange,
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filtered, key = { it.id }) { destination ->
                TourListCard(
                    destination = destination,
                    onEdit = { onEditTour(destination) },
                    onDelete = { onDeleteTour(destination) }
                )
            }
        }
    }
}

@Composable
private fun TourListCard(
    destination: Destination,
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
            Image(
                painter = painterResource(id = destination.imageRes),
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
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AdminTheme.Primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AdminTheme.Error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddTourScreen(
    modifier: Modifier = Modifier,
    initialDestination: Destination?,
    categoryNames: List<String>,
    onCategoryAdded: (String) -> Unit,
    onBack: () -> Unit,
    onSave: (Destination) -> Unit
) {
    val isEditMode = initialDestination != null

    var name by remember { mutableStateOf(initialDestination?.name ?: "") }
    var category by remember {
        mutableStateOf(
            initialDestination?.category?.takeIf { it in categoryNames }
                ?: categoryNames.firstOrNull()
                ?: ""
        )
    }
    var newCategoryName by remember(initialDestination, categoryNames) {
        mutableStateOf(
            if (initialDestination != null && initialDestination.category !in categoryNames) {
                initialDestination.category
            } else {
                ""
            }
        )
    }
    var categoryMode by remember(initialDestination, categoryNames) {
        mutableStateOf(
            if (initialDestination != null && initialDestination.category !in categoryNames) {
                CategoryInputMode.AddNew
            } else {
                CategoryInputMode.Select
            }
        )
    }
    var location by remember { mutableStateOf(initialDestination?.province ?: "") }
    var duration by remember { mutableStateOf("8") }
    var price by remember { mutableStateOf(initialDestination?.price?.toInt()?.toString() ?: "45") }
    var description by remember { mutableStateOf(initialDestination?.description ?: "") }
    var imageRes by remember { mutableStateOf(initialDestination?.imageRes ?: R.drawable.angkorwat1) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { /* gallery pick — preview uses default drawable for now */ }

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
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Tour image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        shape = CircleShape,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = AdminTheme.Primary)
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        shape = CircleShape,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Image, contentDescription = "Gallery", tint = AdminTheme.Primary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Tour Name", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTheme.TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Enter tour name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Category", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTheme.TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = categoryMode == CategoryInputMode.Select,
                onClick = { categoryMode = CategoryInputMode.Select },
                label = { Text("Select") },
                leadingIcon = if (categoryMode == CategoryInputMode.Select) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AdminTheme.PrimaryLight,
                    selectedLabelColor = AdminTheme.Primary
                )
            )
            FilterChip(
                selected = categoryMode == CategoryInputMode.AddNew,
                onClick = { categoryMode = CategoryInputMode.AddNew },
                label = { Text("Add New") },
                leadingIcon = if (categoryMode == CategoryInputMode.AddNew) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AdminTheme.PrimaryLight,
                    selectedLabelColor = AdminTheme.Primary
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (categoryMode) {
            CategoryInputMode.Select -> {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select category") },
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
                        categoryNames.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            CategoryInputMode.AddNew -> {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("Enter new category name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = adminFieldColors(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AdminTheme.Primary)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Location", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTheme.TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            placeholder = { Text("e.g. Siem Reap, Phnom Penh") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AdminTheme.Primary)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Duration (Hours)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTheme.TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = adminFieldColors(),
                    singleLine = true
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Price ($)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTheme.TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = adminFieldColors(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminTheme.TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Enter description...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = adminFieldColors(),
            minLines = 4
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                val finalCategory = when (categoryMode) {
                    CategoryInputMode.Select -> category
                    CategoryInputMode.AddNew -> newCategoryName.trim()
                }

                if (finalCategory.isBlank()) return@Button

                if (categoryMode == CategoryInputMode.AddNew && finalCategory !in categoryNames) {
                    onCategoryAdded(finalCategory)
                }

                val result = if (initialDestination != null) {
                    initialDestination.copy(
                        name = name,
                        province = location.ifBlank { initialDestination.province },
                        category = finalCategory,
                        price = price.toDoubleOrNull() ?: initialDestination.price,
                        description = description,
                        imageRes = imageRes
                    )
                } else {
                    Destination(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        province = location.ifBlank { "Cambodia" },
                        category = finalCategory,
                        price = price.toDoubleOrNull() ?: 0.0,
                        rating = 4.5,
                        imageRes = imageRes,
                        description = description
                    )
                }
                onSave(result)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AdminTheme.Primary,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Text(
                text = if (isEditMode) "Update Tour" else "Save Tour",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
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
