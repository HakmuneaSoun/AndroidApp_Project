package com.example.final_project.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.domain.model.AdminUser
import com.example.final_project.domain.model.mockAdminUsers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    modifier: Modifier = Modifier,
    users: List<AdminUser> = mockAdminUsers
) {
    var searchQuery by remember { mutableStateOf("") }
    var showMenuForUserId by remember { mutableStateOf<String?>(null) }

    val filteredUsers = remember(searchQuery, users) {
        if (searchQuery.isBlank()) users
        else users.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
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
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search users...", color = AdminTheme.TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = AdminTheme.TextSecondary)
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AdminTheme.Primary,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = AdminTheme.Surface,
                unfocusedContainerColor = AdminTheme.Surface,
                focusedTextColor = AdminTheme.TextPrimary,
                unfocusedTextColor = AdminTheme.TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${filteredUsers.size} users",
            fontSize = 13.sp,
            color = AdminTheme.TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                UserListItem(
                    user = user,
                    showMenu = showMenuForUserId == user.id,
                    onMenuClick = {
                        showMenuForUserId = if (showMenuForUserId == user.id) null else user.id
                    },
                    onDismissMenu = { showMenuForUserId = null }
                )
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: AdminUser,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onDismissMenu: () -> Unit
) {
    val initials = user.name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AdminTheme.Surface)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(AdminTheme.Accent, AdminTheme.Primary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminTheme.TextPrimary
            )
            Text(
                text = user.email,
                fontSize = 13.sp,
                color = AdminTheme.TextSecondary
            )
        }

        Box {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = AdminTheme.TextSecondary
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = onDismissMenu
            ) {
                DropdownMenuItem(
                    text = { Text("View profile") },
                    onClick = onDismissMenu
                )
                DropdownMenuItem(
                    text = { Text("Send message") },
                    onClick = onDismissMenu
                )
                DropdownMenuItem(
                    text = { Text("Deactivate", color = AdminTheme.Error) },
                    onClick = onDismissMenu
                )
            }
        }
    }
}
