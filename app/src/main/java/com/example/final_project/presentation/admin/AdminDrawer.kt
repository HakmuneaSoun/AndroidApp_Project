package com.example.final_project.presentation.admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AdminMenuItem(
    val screen: AdminScreen?,
    val icon: ImageVector,
    val title: String,
    val badge: Int? = null,
    val isLogout: Boolean = false
)

@Composable
fun AdminDrawerContent(
    selectedScreen: AdminScreen,
    adminName: String,
    adminEmail: String,
    onItemClick: (AdminScreen) -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit
) {
    val menuItems = listOf(
        AdminMenuItem(AdminScreen.Dashboard, Icons.Default.Dashboard, "Dashboard"),
        AdminMenuItem(AdminScreen.Tours, Icons.Default.Explore, "Tours", 12),
        AdminMenuItem(AdminScreen.Bookings, Icons.Default.Bookmark, "Booking", 5),
        AdminMenuItem(AdminScreen.Users, Icons.Default.People, "Users", 156),
        AdminMenuItem(AdminScreen.Categories, Icons.Default.Category, "Categories", 6),
        AdminMenuItem(AdminScreen.Profile, Icons.Default.Person, "Profile"),
        AdminMenuItem(null, Icons.Default.Settings, "Setting"),
        AdminMenuItem(null, Icons.Default.Logout, "Logout", isLogout = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminTheme.Surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AdminTheme.PrimaryDark, AdminTheme.Primary, AdminTheme.Accent)
                    )
                )
                .clickable(onClick = onProfileClick)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(adminName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(adminEmail, fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            items(menuItems) { item ->
                val isSelected = item.screen != null && item.screen == selectedScreen.rootScreen()
                AdminDrawerItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        when {
                            item.isLogout -> onLogout()
                            item.screen != null -> onItemClick(item.screen)
                        }
                    }
                )
            }
        }
    }
}

private fun AdminScreen.rootScreen(): AdminScreen = when (this) {
    AdminScreen.AddTour, AdminScreen.EditTour -> AdminScreen.Tours
    AdminScreen.Notifications -> AdminScreen.Dashboard
    else -> this
}

@Composable
private fun AdminDrawerItem(
    item: AdminMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected) AdminTheme.PrimaryLight else Color.Transparent,
        label = "drawer_bg"
    )
    val scale by animateFloatAsState(
        if (isSelected) 1f else 0.98f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "drawer_scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) AdminTheme.Primary.copy(alpha = 0.15f)
            else AdminTheme.Background
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    item.icon,
                    contentDescription = item.title,
                    tint = if (item.isLogout) AdminTheme.Error
                    else if (isSelected) AdminTheme.Primary
                    else AdminTheme.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (item.isLogout) AdminTheme.Error
            else if (isSelected) AdminTheme.Primary
            else AdminTheme.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (item.badge != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) AdminTheme.Primary else AdminTheme.PrimaryLight
            ) {
                Text(
                    text = item.badge.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else AdminTheme.Primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        if (isSelected) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AdminTheme.Primary)
            )
        }
    }
}
