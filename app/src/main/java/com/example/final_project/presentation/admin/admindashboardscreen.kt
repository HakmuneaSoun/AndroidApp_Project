package com.example.final_project.presentation.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.final_project.R
import com.example.final_project.domain.model.Destination
import com.example.final_project.domain.model.mockDestinations

data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val badge: Int? = null
)

data class AdminProfile(
    var name: String = "Admin",
    var email: String = "admin@admin.com",
    var phone: String = "+855 12 345 678",
    var role: String = "Administrator",
    var profileImage: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit
) {
    // currentScreen drives which full-screen content is shown next to the side menu:
    // "dashboard", "profile", "addDestination", "editDestination"
    var currentScreen by remember { mutableStateOf("dashboard") }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    val destinations = remember { mockDestinations.toMutableStateList() }
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var adminProfile by remember { mutableStateOf(AdminProfile()) }

    val menuItems = listOf(
        MenuItem(Icons.Default.Dashboard, "Dashboard", 3),
        MenuItem(Icons.Default.Explore, "Tours", 12),
        MenuItem(Icons.Default.Bookmark, "Booking", 5),
        MenuItem(Icons.Default.Star, "Reviews", 24),
        MenuItem(Icons.Default.People, "Users", 156),
        MenuItem(Icons.Default.Category, "Categories", 6),
        MenuItem(Icons.Default.Person, "Profile", 1),
        MenuItem(Icons.Default.Settings, "Setting"),
        MenuItem(Icons.Default.Logout, "Logout")
    )

    Scaffold(
        modifier = Modifier.background(Color.White),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                if (showMenu) Icons.Default.Close else Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color(0xFF2196F3)
                            )
                        }
                        Text(
                            text = when (currentScreen) {
                                "profile" -> "Edit Profile"
                                "addDestination" -> "Add Destination"
                                "editDestination" -> "Edit Destination"
                                else -> "Admin Dashboard"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                },
                actions = {
                    if (currentScreen == "dashboard") {
                        IconButton(onClick = { /* Notification */ }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2196F3)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF2196F3)
                )
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Side Menu
            if (showMenu) {
                Surface(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .shadow(elevation = 8.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        // Admin Profile Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF2196F3),
                                            Color(0xFF4FC3F7)
                                        )
                                    )
                                )
                                .padding(20.dp)
                                .clickable {
                                    currentScreen = "profile"
                                    showMenu = false
                                }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    modifier = Modifier.size(70.dp),
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 4.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.AdminPanelSettings,
                                            contentDescription = "Admin",
                                            modifier = Modifier.size(36.dp),
                                            tint = Color(0xFF2196F3)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = adminProfile.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = adminProfile.email,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Menu Items
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp)
                        ) {
                            items(menuItems) { item ->
                                val isSelected = item.title == "Dashboard" && currentScreen == "dashboard"
                                MenuItemRow(
                                    item = item,
                                    isSelected = isSelected,
                                    onClick = {
                                        when (item.title) {
                                            "Logout" -> onNavigateBack()
                                            "Profile" -> {
                                                currentScreen = "profile"
                                                showMenu = false
                                            }
                                            else -> {
                                                currentScreen = "dashboard"
                                                showMenu = false
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Main Content
            when (currentScreen) {
                "profile" -> {
                    EditProfileScreen(
                        adminProfile = adminProfile,
                        onProfileUpdate = { updatedProfile ->
                            adminProfile = updatedProfile
                        },
                        onBack = { currentScreen = "dashboard" }
                    )
                }
                "addDestination" -> {
                    DestinationFormScreen(
                        initialDestination = null,
                        onBack = { currentScreen = "dashboard" },
                        onSave = { newDestination ->
                            destinations.add(newDestination)
                            currentScreen = "dashboard"
                        }
                    )
                }
                "editDestination" -> {
                    selectedDestination?.let { destination ->
                        DestinationFormScreen(
                            initialDestination = destination,
                            onBack = { currentScreen = "dashboard" },
                            onSave = { updatedDestination ->
                                val index = destinations.indexOfFirst { it.id == updatedDestination.id }
                                if (index != -1) {
                                    destinations[index] = updatedDestination
                                }
                                currentScreen = "dashboard"
                            }
                        )
                    }
                }
                else -> {
                    DashboardContent(
                        destinations = destinations,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onAddDestination = { currentScreen = "addDestination" },
                        onEditDestination = { destination ->
                            selectedDestination = destination
                            currentScreen = "editDestination"
                        },
                        onDeleteDestination = { destination ->
                            destinations.remove(destination)
                        },
                        onNavigateToProfile = { currentScreen = "profile" }
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.DashboardContent(
    destinations: List<Destination>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddDestination: () -> Unit,
    onEditDestination: (Destination) -> Unit,
    onDeleteDestination: (Destination) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF5F9FF)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "Search in admin panel..." else searchQuery,
                    color = if (searchQuery.isEmpty()) Color.Gray else Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Tours",
                value = destinations.size.toString(),
                icon = Icons.Default.Explore,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Bookings",
                value = "567",
                icon = Icons.Default.Bookmark,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Users",
                value = "1,234",
                icon = Icons.Default.People,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Activity & Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Recent Activity
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Recent Activity",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ActivityItem(
                        icon = Icons.Default.Add,
                        text = "New tour added",
                        time = "2 min ago",
                        color = Color(0xFF4CAF50)
                    )
                    ActivityItem(
                        icon = Icons.Default.Star,
                        text = "New review received",
                        time = "15 min ago",
                        color = Color(0xFFFFD700)
                    )
                }
            }

            // Quick Actions
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Quick Actions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        title = "Add Tour",
                        subtitle = "Create new destination",
                        color = Color(0xFF2196F3),
                        onClick = onAddDestination
                    )
                    QuickActionButton(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Update admin profile",
                        color = Color(0xFFFF9800),
                        onClick = onNavigateToProfile
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Destinations List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Tours (${destinations.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Button(
                onClick = onAddDestination,
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Destinations List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(destinations.take(5)) { destination ->
                AdminDestinationItem(
                    destination = destination,
                    onEdit = { onEditDestination(destination) },
                    onDelete = { onDeleteDestination(destination) }
                )
            }
        }
    }
}

@Composable
fun RowScope.EditProfileScreen(
    adminProfile: AdminProfile,
    onProfileUpdate: (AdminProfile) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(adminProfile.name) }
    var email by remember { mutableStateOf(adminProfile.email) }
    var phone by remember { mutableStateOf(adminProfile.phone) }
    var role by remember { mutableStateOf(adminProfile.role) }
    var profileImage by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // Menu items for edit profile
    val editMenuItems = listOf(
        EditMenuItem(Icons.Default.Person, "Personal Information", "Update your personal details"),
        EditMenuItem(Icons.Default.Phone, "Contact Details", "Manage your contact information"),
        EditMenuItem(Icons.Default.Lock, "Security", "Change password & security settings"),
        EditMenuItem(Icons.Default.Notifications, "Notifications", "Manage notification preferences"),
        EditMenuItem(Icons.Default.Info, "About", "App information & version")
    )

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Photo Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profile Photo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Profile Image with upload button
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                clip = false
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF2196F3),
                                            Color(0xFF4FC3F7)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Edit icon overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3))
                                .border(2.dp, Color.White, CircleShape)
                                .clickable {
                                    // Photo picker logic would go here
                                    isEditing = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap the camera icon to change photo",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Personal Information Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Personal Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Role") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val updatedProfile = adminProfile.copy(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    role = role
                                )
                                onProfileUpdate(updatedProfile)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Changes")
                        }
                    }
                }
            }
        }

        // Quick Settings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Quick Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    editMenuItems.forEach { item ->
                        EditMenuItemRow(item = item)
                        if (item != editMenuItems.last()) {
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class EditMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@Composable
fun EditMenuItemRow(item: EditMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to respective setting */ }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = Color(0xFF2196F3).copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun MenuItemRow(
    item: MenuItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) Color(0xFF2196F3).copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            item.icon,
            contentDescription = item.title,
            tint = if (isSelected) Color(0xFF2196F3) else Color.Gray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2196F3) else Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        if (item.badge != null) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) Color(0xFF2196F3) else Color(0xFFE3F2FD),
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = item.badge.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF2196F3)
                    )
                }
            }
        }
        if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier.size(6.dp),
                shape = CircleShape,
                color = Color(0xFF2196F3)
            ) {}
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = color.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun ActivityItem(
    icon: ImageVector,
    text: String,
    time: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text,
                fontSize = 12.sp,
                color = Color.Black,
                maxLines = 1
            )
            Text(
                time,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                subtitle,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AdminDestinationItem(
    destination: Destination,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = painterResource(id = destination.imageRes),
                    contentDescription = destination.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${destination.category} • ${destination.province}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${destination.price} • ⭐ ${destination.rating}",
                    fontSize = 11.sp,
                    color = Color(0xFF2196F3)
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF4759),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-screen form used for BOTH adding and editing a destination.
 * Renders inside the same Row as the side menu (just like EditProfileScreen),
 * so the hamburger menu stays available while creating/editing a tour.
 *
 * Photo input supports two paths:
 *  1. Gallery picker (Android Photo Picker via PickVisualMedia) — tap the camera badge.
 *  2. Manual image URL text field as a fallback.
 * Whichever was set most recently wins when saving.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.DestinationFormScreen(
    initialDestination: Destination?,
    onBack: () -> Unit,
    onSave: (Destination) -> Unit
) {
    val isEditMode = initialDestination != null

    var name by remember { mutableStateOf(initialDestination?.name ?: "") }
    var province by remember { mutableStateOf(initialDestination?.province ?: "") }
    var category by remember { mutableStateOf(initialDestination?.category ?: "") }
    var price by remember { mutableStateOf(initialDestination?.price?.toString() ?: "") }
    var rating by remember { mutableStateOf(initialDestination?.rating?.toString() ?: "") }
    var description by remember { mutableStateOf(initialDestination?.description ?: "") }

    // var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    // var imageUrlInput by remember { mutableStateOf(initialDestination?.imageUrl ?: "") }
    var imageResInput by remember { mutableStateOf(initialDestination?.imageRes ?: R.drawable.angkorwat) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // selectedImageUri = uri
            // imageUrlInput = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Photo Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Destination Photo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // val previewModel: Any? = selectedImageUri ?: imageUrlInput.takeIf { it.isNotBlank() }

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            )
                    ) {
                        Image(
                            painter = painterResource(id = imageResInput),
                            contentDescription = "Destination photo preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Camera badge — opens the gallery photo picker
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3))
                                .border(2.dp, Color.White, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Pick photo from gallery",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the camera icon to choose a photo from your gallery",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    /*
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                        Text(text = "  OR  ", fontSize = 11.sp, color = Color.Gray)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = imageUrlInput,
                        onValueChange = {
                            imageUrlInput = it
                            if (it.isNotBlank()) selectedImageUri = null
                        },
                        label = { Text("Image URL") },
                        placeholder = { Text("https://example.com/photo.jpg") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )
                    */
                }
            }
        }

        // Details Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Destination Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Destination Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = province,
                        onValueChange = { province = it },
                        label = { Text("Province") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF2196F3))
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price ($)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                focusedLabelColor = Color(0xFF2196F3)
                            )
                        )

                        OutlinedTextField(
                            value = rating,
                            onValueChange = { rating = it },
                            label = { Text("Rating") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                focusedLabelColor = Color(0xFF2196F3)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            focusedLabelColor = Color(0xFF2196F3)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val result = if (initialDestination != null) {
                                    initialDestination.copy(
                                        name = name,
                                        province = province,
                                        category = category,
                                        price = price.toDoubleOrNull() ?: initialDestination.price,
                                        rating = rating.toDoubleOrNull() ?: initialDestination.rating,
                                        description = description,
                                        imageRes = imageResInput
                                    )
                                } else {
                                    Destination(
                                        id = System.currentTimeMillis().toString(),
                                        name = name,
                                        province = province,
                                        category = category,
                                        price = price.toDoubleOrNull() ?: 0.0,
                                        rating = rating.toDoubleOrNull() ?: 0.0,
                                        imageRes = imageResInput,
                                        description = description
                                    )
                                }
                                onSave(result)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                if (isEditMode) Icons.Default.Save else Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isEditMode) "Update" else "Save")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}