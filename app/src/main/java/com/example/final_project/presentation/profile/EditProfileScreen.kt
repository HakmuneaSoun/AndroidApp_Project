package com.example.final_project.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.dto.UserProfileData

private val AccentColor = Color(0xFF667eea)
private val AccentDark = Color(0xFF764ba2)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val ErrorRed = Color(0xFFFF4759)
private val ScreenBg = Color(0xFFF5F7FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: (UserProfileData) -> Unit = {},
    useAdminTheme: Boolean = false,
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState = viewModel.uiState
    val profile = uiState.profile

    var fullName by remember(profile) { mutableStateOf(profile?.name.orEmpty()) }
    var phone by remember(profile) { mutableStateOf(profile?.phone.orEmpty()) }

    LaunchedEffect(profile) {
        profile?.let {
            fullName = it.name
            phone = it.phone.orEmpty()
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.isSaving) {
        if (uiState.successMessage != null && uiState.profile != null && !uiState.isSaving) {
            onSaved(uiState.profile!!)
            viewModel.clearMessages()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    val primaryColor = if (useAdminTheme) Color(0xFF2196F3) else AccentColor
    val backgroundColor = if (useAdminTheme) Color(0xFFF5F5F5) else ScreenBg

    Scaffold(
        containerColor = backgroundColor,
    ) { padding ->
        if (uiState.isLoading && profile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            "Profile Photo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clickable(enabled = !uiState.isUploading) {
                                    imagePicker.launch("image/*")
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shadow(8.dp, CircleShape)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val avatarUrl = ApiConstants.resolveMediaUrl(uiState.pendingAvatarUrl)
                                if (!avatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "Profile photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.linearGradient(listOf(primaryColor, AccentDark))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = profileInitials(fullName.ifBlank { profile?.name }),
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                if (uiState.isUploading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(28.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(36.dp),
                                shape = CircleShape,
                                color = primaryColor,
                                border = BorderStroke(2.dp, Color.White),
                                shadowElevation = 4.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = "Change photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            "Personal Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor)
                            },
                            colors = outlinedFieldColors(primaryColor)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = profile?.email.orEmpty(),
                            onValueChange = {},
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary)
                            },
                            colors = outlinedFieldColors(primaryColor)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor)
                            },
                            colors = outlinedFieldColors(primaryColor)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = profile?.role.orEmpty(),
                            onValueChange = {},
                            label = { Text("Role") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = TextSecondary)
                            },
                            colors = outlinedFieldColors(primaryColor)
                        )

                        if (uiState.errorMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(uiState.errorMessage!!, color = ErrorRed, fontSize = 12.sp)
                        }

                        if (uiState.successMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(uiState.successMessage!!, color = Color(0xFF4CAF50), fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    viewModel.clearMessages()
                                    viewModel.saveProfile(fullName, phone)
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving && fullName.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

private fun profileInitials(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    return name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { name.first().uppercaseChar().toString() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun outlinedFieldColors(primaryColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = primaryColor,
    focusedLabelColor = primaryColor,
    cursorColor = primaryColor
)
