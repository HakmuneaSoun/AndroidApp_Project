package com.example.final_project.presentation.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.navigation.AppBottomBar
import com.example.final_project.navigation.BottomNavItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Design tokens ─────────────────────────────────────────────────────────────
private val AccentColor   = Color(0xFF667eea)
private val AccentDark    = Color(0xFF764ba2)
private val ScreenBg      = Color(0xFFF5F7FF)
private val CardBg        = Color.White
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val ErrorRed      = Color(0xFFFF4759)

// ── Section groups ─────────────────────────────────────────────────────────────
data class MenuSection(val title: String, val items: List<ProfileMenuItem>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    selectedTab: BottomNavItem = BottomNavItem.Profile,
    onTabSelected: (BottomNavItem) -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    val menuSections = listOf(
        MenuSection(
            "Travel",
            listOf(
                ProfileMenuItem(Icons.Default.ConfirmationNumber, "My Bookings", "3 upcoming · 9 completed", Color(0xFF2196F3)),
                ProfileMenuItem(Icons.Default.Favorite, "Saved Places", "8 destinations saved", ErrorRed),
                ProfileMenuItem(Icons.Default.Map, "Travel History", "12 places visited", Color(0xFF4CAF50)),
                ProfileMenuItem(Icons.Default.Star, "My Reviews", "5 reviews written", Color(0xFFFFD700))
            )
        ),
        MenuSection(
            "Account",
            listOf(
                ProfileMenuItem(Icons.Default.Edit, "Edit Profile", "Update personal info", AccentColor),
                ProfileMenuItem(Icons.Default.Payments, "Payment Methods", "Manage cards & wallets", Color(0xFF4CAF50)),
                ProfileMenuItem(Icons.Default.Notifications, "Notifications", "Manage alerts & offers", Color(0xFFFF9800)),
                ProfileMenuItem(Icons.Default.Security, "Security", "Password & login", Color(0xFF9C27B0))
            )
        ),
        MenuSection(
            "Support",
            listOf(
                ProfileMenuItem(Icons.AutoMirrored.Filled.Help, "Help & Support", "FAQs and contact us", Color(0xFF00BCD4)),
                ProfileMenuItem(Icons.Default.Info, "About App", "Version 1.0.0", TextSecondary),
                ProfileMenuItem(Icons.AutoMirrored.Filled.Logout, "Logout", "Sign out of your account", ErrorRed, isLogout = true)
            )
        )
    )

    Scaffold(
        modifier = Modifier.background(ScreenBg),
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = AccentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg)
            )
        },
        bottomBar = {
            AppBottomBar(selectedItem = selectedTab, onItemSelected = onTabSelected)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Hero Profile Card ──────────────────────────────────────────────
            item { ProfileHeroCard() }

            // ── Travel Stats ───────────────────────────────────────────────────
            item { TravelStatsRow() }

            // ── Travel Passport Card ───────────────────────────────────────────
            item { TravelPassportCard() }

            // ── Achievements ──────────────────────────────────────────────────
            item { AchievementsSection() }

            // ── Recent Bookings Preview ────────────────────────────────────────
            item { RecentBookingsSection() }

            // ── Menu Sections ─────────────────────────────────────────────────
            items(menuSections) { section ->
                MenuSectionBlock(
                    section = section,
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = { showLogoutDialog = false; onLogout() },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

// ── Profile Hero Card ──────────────────────────────────────────────────────────
@Composable
fun ProfileHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp), clip = false),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(AccentDark, AccentColor, Color(0xFF89A7F8))))
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-30).dp)
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 30.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(modifier = Modifier.size(96.dp)) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors = listOf(AccentColor, AccentDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("SP", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    // Edit badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clickable { },
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Edit photo",
                                tint = AccentColor, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Sakha Pech", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("sakha.pech@example.com", fontSize = 13.sp, color = Color.White.copy(alpha = 0.82f))

                Spacer(modifier = Modifier.height(8.dp))

                // Location + member since row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Phnom Penh, KH", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Member since 2023", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Premium badge
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.22f)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null,
                            tint = Color(0xFFFFD700), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Premium Explorer", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ── Travel Stats Row ───────────────────────────────────────────────────────────
@Composable
fun TravelStatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(
            Triple("12", "Trips", Icons.Default.ConfirmationNumber to Color(0xFF2196F3)),
            Triple("8", "Saved", Icons.Default.Favorite to ErrorRed),
            Triple("4.8", "Rating", Icons.Default.Star to Color(0xFFFFD700)),
            Triple("5", "Reviews", Icons.Default.RateReview to Color(0xFF9C27B0))
        ).forEach { (value, label, iconPair) ->
            val (icon, color) = iconPair
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(18.dp), clip = false),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(shape = CircleShape, color = color.copy(alpha = 0.1f),
                        modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = color,
                                modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(label, fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}

// ── Travel Passport Card ───────────────────────────────────────────────────────
@Composable
fun TravelPassportCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), clip = false),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp).height(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AccentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Travel Passport", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Surface(shape = RoundedCornerShape(8.dp), color = AccentColor.copy(alpha = 0.1f)) {
                    Text("Level 3 Explorer", fontSize = 11.sp, color = AccentColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // XP Progress bar
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Travel XP", fontSize = 12.sp, color = TextSecondary)
                    Text("720 / 1000 XP", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AccentColor)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.72f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = AccentColor,
                    trackColor = AccentColor.copy(alpha = 0.12f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("280 XP until Level 4 Adventurer", fontSize = 11.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Province stamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Provinces Visited", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("6 / 25", fontSize = 12.sp, color = AccentColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Siem Reap", "Phnom Penh", "Sihanoukville", "Battambang", "Kampot", "Kep")) { province ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AccentColor.copy(alpha = 0.08f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = AccentColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(province, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ── Achievements ───────────────────────────────────────────────────────────────
@Composable
fun AchievementsSection() {
    val achievements = listOf(
        Triple("🛕", "Temple Seeker", true),
        Triple("🏖️", "Beach Lover", true),
        Triple("🌿", "Nature Walker", true),
        Triple("🗺️", "Explorer", false),
        Triple("⭐", "Top Reviewer", false),
        Triple("🎯", "Adventurer", false)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(4.dp).height(18.dp)
                    .clip(RoundedCornerShape(2.dp)).background(AccentColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Achievements", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text("3/6 unlocked", fontSize = 12.sp, color = AccentColor, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(achievements) { (emoji, label, unlocked) ->
                Card(
                    modifier = Modifier
                        .width(90.dp)
                        .shadow(if (unlocked) 5.dp else 2.dp, RoundedCornerShape(16.dp), clip = false),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (unlocked) CardBg else Color(0xFFF3F4F6)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(emoji, fontSize = 26.sp,
                            modifier = Modifier.graphicsLayer(alpha = if (unlocked) 1f else 0.35f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium,
                            color = if (unlocked) TextPrimary else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp),
                            maxLines = 2)
                        if (unlocked) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(shape = CircleShape, color = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(9.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Recent Bookings ────────────────────────────────────────────────────────────
@Composable
fun RecentBookingsSection() {
    val bookings = listOf(
        Triple("Angkor Wat Temple", "Siem Reap", "Nov 15, 2024"),
        Triple("Koh Rong Island", "Sihanoukville", "Oct 3, 2024")
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(4.dp).height(18.dp)
                    .clip(RoundedCornerShape(2.dp)).background(AccentColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recent Trips", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text("See All", fontSize = 12.sp, color = AccentColor,
                fontWeight = FontWeight.Medium, modifier = Modifier.clickable { })
        }
        Spacer(modifier = Modifier.height(12.dp))
        bookings.forEach { (name, province, date) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp), clip = false),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(colors = listOf(AccentColor, AccentDark))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null,
                                tint = TextSecondary, modifier = Modifier.size(11.dp))
                            Text(" $province", fontSize = 11.sp, color = TextSecondary)
                        }
                        Text(date, fontSize = 11.sp, color = TextSecondary)
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF4CAF50).copy(alpha = 0.1f)) {
                        Text("Completed", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

// ── Menu Section Block ─────────────────────────────────────────────────────────
@Composable
fun MenuSectionBlock(section: MenuSection, onLogoutClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)) {
            Box(modifier = Modifier.width(4.dp).height(16.dp)
                .clip(RoundedCornerShape(2.dp)).background(AccentColor))
            Spacer(modifier = Modifier.width(8.dp))
            Text(section.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(20.dp), clip = false),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    ProfileOption(
                        item = item,
                        showDivider = index != section.items.lastIndex,
                        onClick = { if (item.isLogout) onLogoutClick() else item.onClick() }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ── Profile Option Row ─────────────────────────────────────────────────────────
@Composable
fun ProfileOption(
    item: ProfileMenuItem,
    showDivider: Boolean = false,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(120)
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clickable {
                    pressed = true
                    onClick()
                    MainScope().launch { delay(150); pressed = false }
                }
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = item.iconColor.copy(alpha = 0.10f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(item.icon, contentDescription = null,
                        tint = item.iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isLogout) ErrorRed else TextPrimary
                )
                Text(item.subtitle, fontSize = 11.sp, color = TextSecondary)
            }
            if (!item.isLogout) {
                Icon(Icons.Default.ChevronRight, contentDescription = null,
                    tint = Color.LightGray, modifier = Modifier.size(18.dp))
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp, end = 16.dp),
                color = Color(0xFFF0F0F0)
            )
        }
    }
}

// ── Logout Dialog ─────────────────────────────────────────────────────────────
@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(shape = CircleShape, color = ErrorRed.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null,
                        tint = ErrorRed, modifier = Modifier.size(28.dp))
                }
            }
        },
        title = { Text("Sign out?", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
        text = { Text("You'll need to sign in again to access your bookings and saved places.",
            textAlign = TextAlign.Center, color = TextSecondary) },
        confirmButton = {
            Button(onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(12.dp)) {
                Text("Sign out")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Stay signed in")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

// ── Data class ────────────────────────────────────────────────────────────────
data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val iconColor: Color,
    val isLogout: Boolean = false,
    val onClick: () -> Unit = {}
)