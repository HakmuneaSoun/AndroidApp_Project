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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.domain.model.AdminUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminUsersViewModel = viewModel(
        factory = AdminUsersViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState = viewModel.uiState
    var searchQuery by remember { mutableStateOf("") }
    var showMenuForUserId by remember { mutableStateOf<String?>(null) }

    val filteredUsers = remember(searchQuery, uiState.users) {
        if (searchQuery.isBlank()) uiState.users
        else uiState.users.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.role.contains(searchQuery, ignoreCase = true)
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
                searchQuery = it
                viewModel.clearMessages()
            },
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

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                fontSize = 12.sp,
                color = AdminTheme.Error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (uiState.successMessage != null) {
            Text(
                text = uiState.successMessage,
                fontSize = 12.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(
            text = "${filteredUsers.size} users",
            fontSize = 13.sp,
            color = AdminTheme.TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AdminTheme.Primary)
            }
        } else if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No users found", color = AdminTheme.TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredUsers, key = { it.id }) { user ->
                    UserListItem(
                        user = user,
                        isUpdating = uiState.isUpdatingUserId?.toString() == user.id,
                        showMenu = showMenuForUserId == user.id,
                        onMenuClick = {
                            showMenuForUserId = if (showMenuForUserId == user.id) null else user.id
                        },
                        onDismissMenu = { showMenuForUserId = null },
                        onToggleStatus = { isActive ->
                            showMenuForUserId = null
                            viewModel.updateUserStatus(user.id.toLong(), isActive)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: AdminUser,
    isUpdating: Boolean,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onToggleStatus: (Boolean) -> Unit
) {
    val initials = user.name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifBlank { user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?" }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = user.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminTheme.TextPrimary
                )
                UserStatusChip(isActive = user.isActive)
            }
            Text(
                text = user.email,
                fontSize = 13.sp,
                color = AdminTheme.TextSecondary
            )
            if (user.role.isNotBlank()) {
                Text(
                    text = user.role.replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    color = AdminTheme.TextSecondary
                )
            }
        }

        if (isUpdating) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AdminTheme.Primary,
                strokeWidth = 2.dp
            )
        } else {
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
                    if (user.isActive) {
                        DropdownMenuItem(
                            text = { Text("Set Inactive", color = AdminTheme.Error) },
                            onClick = { onToggleStatus(false) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Set Active", color = Color(0xFF4CAF50)) },
                            onClick = { onToggleStatus(true) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserStatusChip(isActive: Boolean) {
    val backgroundColor = if (isActive) Color(0xFF4CAF50).copy(alpha = 0.12f)
    else AdminTheme.Error.copy(alpha = 0.12f)
    val textColor = if (isActive) Color(0xFF2E7D32) else AdminTheme.Error
    val label = if (isActive) "Active" else "Inactive"

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
