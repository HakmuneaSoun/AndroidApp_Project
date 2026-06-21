package com.example.final_project.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.domain.model.AppNotification
import com.example.final_project.domain.model.colorForNotificationType
import com.example.final_project.domain.model.iconForNotificationType
import com.example.final_project.presentation.admin.AdminTheme

enum class NotificationStyle {
    User,
    Admin
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    style: NotificationStyle,
    notifications: List<AppNotification>,
    onNavigateBack: () -> Unit,
    onNotificationsChange: (List<AppNotification>) -> Unit
) {
    var selectedTab by remember { mutableStateOf("All") }

    val primaryColor = when (style) {
        NotificationStyle.User -> Color(0xFF667eea)
        NotificationStyle.Admin -> AdminTheme.Primary
    }
    val backgroundColor = when (style) {
        NotificationStyle.User -> Color(0xFFF5F7FF)
        NotificationStyle.Admin -> AdminTheme.Background
    }

    val filtered = remember(notifications, selectedTab) {
        when (selectedTab) {
            "Unread" -> notifications.filter { !it.isRead }
            else -> notifications
        }
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (style == NotificationStyle.Admin) AdminTheme.TextPrimary else Color(0xFF1A1A2E)
                        )
                        if (unreadCount > 0) {
                            Text(
                                text = "$unreadCount unread",
                                fontSize = 12.sp,
                                color = primaryColor
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (style == NotificationStyle.Admin) AdminTheme.Primary else Color(0xFF1A1A2E)
                        )
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                onNotificationsChange(notifications.map { it.copy(isRead = true) })
                            }
                        ) {
                            Text("Mark all read", color = primaryColor, fontSize = 13.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (style == NotificationStyle.Admin) AdminTheme.Surface else Color.White
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = if (selectedTab == "All") 0 else 1,
                containerColor = if (style == NotificationStyle.Admin) AdminTheme.Surface else Color.White,
                contentColor = primaryColor,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(color = Color(0xFFE5E7EB)) },
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        val index = if (selectedTab == "All") 0 else 1
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[index])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
                            color = primaryColor
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == "All",
                    onClick = { selectedTab = "All" },
                    text = { Text("All (${notifications.size})") }
                )
                Tab(
                    selected = selectedTab == "Unread",
                    onClick = { selectedTab = "Unread" },
                    text = { Text("Unread ($unreadCount)") }
                )
            }

            if (filtered.isEmpty()) {
                EmptyNotificationsState(style = style, isUnreadTab = selectedTab == "Unread")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { notification ->
                        NotificationCard(
                            notification = notification,
                            style = style,
                            onClick = {
                                onNotificationsChange(
                                    notifications.map {
                                        if (it.id == notification.id) it.copy(isRead = true) else it
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

private val UnreadBackground = Color(0xFFE3F2FD)

@Composable
private fun NotificationCard(
    notification: AppNotification,
    style: NotificationStyle,
    onClick: () -> Unit
) {
    val isAdmin = style == NotificationStyle.Admin
    val iconColor = Color(colorForNotificationType(notification.type, isAdmin))
    val readBackground = if (isAdmin) AdminTheme.Surface else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) readBackground else UnreadBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        iconForNotificationType(notification.type),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAdmin) AdminTheme.TextPrimary else Color(0xFF1A1A2E)
                )

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = if (isAdmin) AdminTheme.TextSecondary else Color(0xFF6B7280),
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = notification.time,
                    fontSize = 11.sp,
                    color = if (isAdmin) AdminTheme.TextSecondary.copy(alpha = 0.8f) else Color(0xFF9CA3AF),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState(
    style: NotificationStyle,
    isUnreadTab: Boolean
) {
    val primaryColor = when (style) {
        NotificationStyle.User -> Color(0xFF667eea)
        NotificationStyle.Admin -> AdminTheme.Primary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = primaryColor.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isUnreadTab) "No unread notifications" else "No notifications yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (style == NotificationStyle.Admin) AdminTheme.TextPrimary else Color(0xFF1A1A2E)
        )
        Text(
            text = if (isUnreadTab) "You're all caught up!" else "We'll notify you when something arrives.",
            fontSize = 13.sp,
            color = if (style == NotificationStyle.Admin) AdminTheme.TextSecondary else Color(0xFF6B7280),
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun NotificationBellIcon(
    unreadCount: Int,
    tint: Color,
    onClick: () -> Unit
) {
    Box {
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = tint
            )
        }
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4759)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
