package com.example.final_project.presentation.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.final_project.R
import com.example.final_project.domain.model.Destination
import com.example.final_project.domain.model.mockAdminNotifications
import com.example.final_project.presentation.categories.CategoriesViewModel
import com.example.final_project.presentation.categories.CategoriesViewModelFactory
import com.example.final_project.presentation.notifications.NotificationBellIcon
import com.example.final_project.presentation.notifications.NotificationStyle
import com.example.final_project.presentation.notifications.NotificationsScreen
import com.example.final_project.presentation.profile.EditProfileScreen
import com.example.final_project.presentation.profile.ProfileViewModel
import com.example.final_project.presentation.profile.ProfileViewModelFactory
import com.example.final_project.presentation.tours.ToursViewModel
import com.example.final_project.presentation.tours.ToursViewModelFactory
import kotlinx.coroutines.launch

data class MenuItem(
    val icon: ImageVector,
    val title: String
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
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context.applicationContext as android.app.Application)
    )
    val profileState = profileViewModel.uiState
    val toursViewModel: ToursViewModel = viewModel(
        factory = ToursViewModelFactory(context.applicationContext as android.app.Application)
    )
    val toursState = toursViewModel.uiState
    val categoriesViewModel: CategoriesViewModel = viewModel(
        factory = CategoriesViewModelFactory(context.applicationContext as android.app.Application)
    )
    val categoriesState = categoriesViewModel.uiState

    var currentScreen by remember { mutableStateOf<AdminScreen>(AdminScreen.Dashboard) }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    var toursSearchQuery by remember { mutableStateOf("") }
    var adminProfile by remember { mutableStateOf(AdminProfile()) }
    val adminNotifications = remember { mockAdminNotifications.toMutableStateList() }
    var previousAdminScreen by remember { mutableStateOf(AdminScreen.Dashboard) }

    LaunchedEffect(toursState.saveCompleted) {
        if (toursState.saveCompleted) {
            toursViewModel.clearSaveCompleted()
            currentScreen = AdminScreen.Tours
        }
    }

    LaunchedEffect(currentScreen, selectedDestination) {
        when (currentScreen) {
            AdminScreen.AddTour -> toursViewModel.prepareTourForm(null)
            AdminScreen.EditTour -> toursViewModel.prepareTourForm(selectedDestination)
            else -> Unit
        }
    }

    LaunchedEffect(profileState.profile) {
        profileState.profile?.let { profile ->
            adminProfile = AdminProfile(
                name = profile.name,
                email = profile.email,
                phone = profile.phone.orEmpty(),
                role = profile.role,
                profileImage = profile.avatarUrl.orEmpty()
            )
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showDrawerMenu = currentScreen !in listOf(
        AdminScreen.AddTour,
        AdminScreen.EditTour,
        AdminScreen.Notifications
    )


    fun navigateTo(screen: AdminScreen) {
        currentScreen = screen
    }

    fun openNotifications() {
        if (currentScreen != AdminScreen.Notifications) {
//            previousAdminScreen = currentScreen
            navigateTo(AdminScreen.Notifications)
        }
    }

    fun navigateBackFromNotifications() {
        navigateTo(previousAdminScreen)
    }
    fun openDrawer() {
        scope.launch { drawerState.open() }
    }

    fun closeDrawer() {
        scope.launch { drawerState.close() }
    }

    fun handleBack() {
        when (currentScreen) {
            AdminScreen.AddTour, AdminScreen.EditTour -> navigateTo(AdminScreen.Tours)
            AdminScreen.Notifications -> navigateBackFromNotifications()
            AdminScreen.Profile -> navigateTo(AdminScreen.Dashboard)
            else -> Unit
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showDrawerMenu,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = AdminTheme.Surface
            ) {
                AdminDrawerContent(
                    selectedScreen = currentScreen,
                    adminName = adminProfile.name,
                    adminEmail = adminProfile.email,
                    onItemClick = { screen ->
                        navigateTo(screen)
                        closeDrawer()
                    },
                    onLogout = { profileViewModel.logout(onNavigateBack) },
                    onProfileClick = {
                        navigateTo(AdminScreen.Profile)
                        closeDrawer()
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.background(AdminTheme.Background),
            topBar = {
                if (currentScreen != AdminScreen.Notifications) {
                    TopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminTheme.Primary
                        )
                    },
                    navigationIcon = {
                        if (showDrawerMenu) {
                            IconButton(onClick = { openDrawer() }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = AdminTheme.Primary
                                )
                            }
                        } else {
                            IconButton(onClick = { handleBack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = AdminTheme.Primary
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentScreen != AdminScreen.Notifications) {
                            NotificationBellIcon(
                                unreadCount = adminNotifications.count { !it.isRead },
                                tint = AdminTheme.Primary,
                                onClick = { openNotifications() }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AdminTheme.Surface,
                        titleContentColor = AdminTheme.Primary
                    )
                )
                }
            }
        ) { paddingValues ->
            AnimatedContent(
                targetState = currentScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                transitionSpec = {
                    (fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 5 })
                        .togetherWith(fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 5 })
                },
                label = "admin_screen_transition"
            ) { screen ->
                when (screen) {
                    AdminScreen.Dashboard -> {
                        AdminDashboardContent(
                            onViewAllBookings = { navigateTo(AdminScreen.Bookings) }
                        )
                    }

                    AdminScreen.Tours -> {
                        AdminToursScreen(
                            destinations = toursState.tours,
                            isLoading = toursState.isLoading,
                            isDeletingTourId = toursState.isDeletingTourId,
                            errorMessage = toursState.errorMessage,
                            successMessage = toursState.successMessage,
                            searchQuery = toursSearchQuery,
                            onSearchQueryChange = { toursSearchQuery = it },
                            onAddTour = { navigateTo(AdminScreen.AddTour) },
                            onEditTour = { destination ->
                                selectedDestination = destination
                                navigateTo(AdminScreen.EditTour)
                            },
                            onDeleteTour = { destination ->
                                destination.id.toLongOrNull()?.let { toursViewModel.deleteTour(it) }
                            },
                            onDismissMessage = { toursViewModel.clearMessages() }
                        )
                    }

                    AdminScreen.AddTour -> {
                        AdminAddTourScreen(
                            initialDestination = null,
                            categories = categoriesState.categoryOptions,
                            imageCoverUrl = toursState.pendingImageUrl,
                            isSaving = toursState.isSaving,
                            isUploadingImage = toursState.isUploadingImage,
                            errorMessage = toursState.errorMessage,
                            onBack = { navigateTo(AdminScreen.Tours) },
                            onUploadImage = toursViewModel::uploadTourImage,
                            onSave = { form, _ -> toursViewModel.createTour(form) }
                        )
                    }

                    AdminScreen.EditTour -> {
                        AdminAddTourScreen(
                            initialDestination = selectedDestination,
                            categories = categoriesState.categoryOptions,
                            imageCoverUrl = toursState.pendingImageUrl,
                            isSaving = toursState.isSaving,
                            isUploadingImage = toursState.isUploadingImage,
                            errorMessage = toursState.errorMessage,
                            onBack = { navigateTo(AdminScreen.Tours) },
                            onUploadImage = toursViewModel::uploadTourImage,
                            onSave = { form, tourId ->
                                val id = tourId ?: selectedDestination?.id?.toLongOrNull()
                                if (id != null) toursViewModel.updateTour(id, form)
                            }
                        )
                    }

                    AdminScreen.Bookings -> {
                        AdminBookingsScreen()
                    }

                    AdminScreen.Users -> {
                        AdminUsersScreen()
                    }

                    AdminScreen.Categories -> {
                        AdminCategoriesScreen(
                            destinations = toursState.tours,
                            viewModel = categoriesViewModel
                        )
                    }

                    AdminScreen.Notifications -> {
                        NotificationsScreen(
                            style = NotificationStyle.Admin,
                            notifications = adminNotifications,
                            onNavigateBack = { navigateBackFromNotifications() },
                            onNotificationsChange = { updated ->
                                adminNotifications.clear()
                                adminNotifications.addAll(updated)
                            }
                        )
                    }

                    AdminScreen.Profile -> {
                        EditProfileScreen(
                            onBack = { navigateTo(AdminScreen.Dashboard) },
                            onSaved = { updated ->
                                adminProfile = AdminProfile(
                                    name = updated.name,
                                    email = updated.email,
                                    phone = updated.phone.orEmpty(),
                                    role = updated.role,
                                    profileImage = updated.avatarUrl.orEmpty()
                                )
                            },
                            useAdminTheme = true,
                            viewModel = profileViewModel
                        )
                    }
                }
            }
        }
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
fun DestinationFormScreen(
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
            .fillMaxSize()
            .background(AdminTheme.Background)
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