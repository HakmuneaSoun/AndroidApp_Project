package com.example.final_project.presentation.auth.register

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.presentation.auth.AuthViewModel
import com.example.final_project.presentation.auth.AuthViewModelFactory

// ── Tokens ─────────────────────────────────────────────────────────────────────
private val Purple900  = Color(0xFF2D1B69)
private val Purple700  = Color(0xFF5E35B1)
private val Accent     = Color(0xFF667eea)
private val AccentDark = Color(0xFF764ba2)
private val Gold       = Color(0xFFFFD700)
private val ScreenBg   = Color(0xFFF5F7FF)
private val CardBg     = Color.White
private val TextPri    = Color(0xFF1A1A2E)
private val TextSec    = Color(0xFF6B7280)
private val ErrorRed   = Color(0xFFFF4759)
private val GreenOk    = Color(0xFF4CAF50)

// Password strength levels
private enum class PasswordStrength(val label: String, val color: Color, val fraction: Float) {
    None("", Color.Transparent, 0f),
    Weak("Weak", ErrorRed, 0.25f),
    Fair("Fair", Color(0xFFFF9800), 0.55f),
    Good("Good", Color(0xFF8BC34A), 0.80f),
    Strong("Strong", GreenOk, 1f)
}

private fun passwordStrength(pwd: String): PasswordStrength = when {
    pwd.isEmpty()                                               -> PasswordStrength.None
    pwd.length < 6                                             -> PasswordStrength.Weak
    pwd.length < 8                                             -> PasswordStrength.Fair
    pwd.length >= 8 && pwd.any { it.isDigit() }
            && pwd.any { it.isLetter() }                       -> PasswordStrength.Good
    pwd.length >= 10 && pwd.any { it.isDigit() }
            && pwd.any { it.isLetter() }
            && pwd.any { !it.isLetterOrDigit() }               -> PasswordStrength.Strong
    else                                                        -> PasswordStrength.Fair
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext as android.app.Application)
    )
    val authState = viewModel.uiState

    // Form state
    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }
    var agreeTerms      by remember { mutableStateOf(false) }
    val isLoading = authState.isLoading
    val apiError = authState.errorMessage

    LaunchedEffect(authState.registerSuccess) {
        if (authState.registerSuccess) {
            onRegisterSuccess()
            viewModel.clearRegisterSuccess()
        }
    }

    // Validation
    val nameError    = fullName.isNotEmpty() && fullName.length < 2
    val emailError   = email.isNotEmpty() && !email.contains("@")
    val phoneError   = phone.isNotEmpty() && phone.length < 8
    val pwdStrength  = passwordStrength(password)
    val pwdMatch     = confirmPassword.isEmpty() || password == confirmPassword
    val formValid    = fullName.length >= 2 && email.contains("@") &&
            phone.length >= 8 && password.length >= 6 &&
            password == confirmPassword && agreeTerms

    // Ambient float animation
    val infiniteTransition = rememberInfiniteTransition()
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -14f,
        animationSpec = infiniteRepeatable(tween(2800, easing = EaseInOutSine), RepeatMode.Reverse)
    )
    val floatY2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse)
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    // Entrance
    var started by remember { mutableStateOf(false) }
    val heroAlpha   by animateFloatAsState(if (started) 1f else 0f, tween(600))
    val heroOffset  by animateFloatAsState(if (started) 0f else 40f, tween(700, easing = EaseOutCubic))
    LaunchedEffect(Unit) { started = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Hero Panel ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(colors = listOf(Purple900, Purple700, Accent))
                )
        ) {
            // Blurred ambient spheres
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-20).dp)
                    .graphicsLayer { translationY = floatY }
                    .size(180.dp).blur(40.dp).clip(CircleShape)
                    .background(Color(0xFF9BB8FF).copy(alpha = 0.35f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-40).dp, y = 40.dp)
                    .graphicsLayer { translationY = floatY2 }
                    .size(140.dp).blur(30.dp).clip(CircleShape)
                    .background(Gold.copy(alpha = 0.20f))
            )

            // Floating destination chips
            listOf(
                Triple("🛕 Temples", Alignment.TopStart,
                    Modifier.padding(start = 20.dp, top = 54.dp)),
                Triple("🏖️ Beaches",  Alignment.TopEnd,
                    Modifier.padding(end = 20.dp, top = 90.dp)),
                Triple("🌿 Nature",   Alignment.BottomStart,
                    Modifier.padding(start = 32.dp, bottom = 64.dp))
            ).forEach { (label, align, mod) ->
                Box(
                    modifier = Modifier
                        .align(align)
                        .then(mod)
                        .graphicsLayer { translationY = if (align == Alignment.TopEnd) floatY2 else floatY }
                        .alpha(heroAlpha)
                ) {
                    Surface(shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.18f)) {
                        Text(label, fontSize = 11.sp, color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                    }
                }
            }

            // Back button
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                Surface(modifier = Modifier.size(36.dp), shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = Color.White,
                            modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Hero text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .graphicsLayer { translationY = heroOffset }
                    .alpha(heroAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing logo badge
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .alpha(glowAlpha)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF9BB8FF).copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                    )
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Join thousands of Cambodia explorers", fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.80f))
            }

            // Wave arch
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(36.dp)
                    .align(Alignment.BottomCenter)
                    .background(ScreenBg, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            )
        }

        // ── Form ─────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            // Trust strip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Accent.copy(alpha = 0.07f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Icons.Default.Lock to "Secure",
                        Icons.Default.Verified to "Verified",
                        Icons.Default.Speed to "Instant"
                    ).forEach { (icon, label) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, contentDescription = null,
                                tint = Accent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(label, fontSize = 10.sp, color = Accent,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section label
            SectionDividerLabel("Personal Details")

            Spacer(modifier = Modifier.height(12.dp))

            // Full Name
            FormField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                placeholder = "Sakha Pech",
                icon = Icons.Default.Person,
                isError = nameError,
                errorText = "Name must be at least 2 characters",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            FormField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "sakha@example.com",
                icon = Icons.Default.Email,
                isError = emailError,
                errorText = "Please enter a valid email",
                keyboardType = KeyboardType.Email,
                trailingContent = if (email.contains("@") && !emailError) {
                    { Icon(Icons.Default.CheckCircle, null, tint = GreenOk, modifier = Modifier.size(18.dp)) }
                } else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            FormField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                placeholder = "+855 12 345 678",
                icon = Icons.Default.Phone,
                isError = phoneError,
                errorText = "Enter a valid phone number",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(20.dp))
            SectionDividerLabel("Security")
            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Min. 6 characters") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, tint = Accent, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null, tint = TextSec
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = fieldColors()
            )

            // Password strength indicator
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            PasswordStrength.Weak,
                            PasswordStrength.Fair,
                            PasswordStrength.Good,
                            PasswordStrength.Strong
                        ).forEachIndexed { index, level ->
                            val filled = pwdStrength.ordinal > index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (filled) pwdStrength.color
                                        else Color.LightGray.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Password strength", fontSize = 10.sp, color = TextSec)
                        Text(pwdStrength.label, fontSize = 10.sp,
                            color = pwdStrength.color, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                placeholder = { Text("Re-enter your password") },
                leadingIcon = {
                    Icon(Icons.Default.LockReset, null, tint = Accent, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (confirmPassword.isNotEmpty()) {
                            Icon(
                                if (pwdMatch) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (pwdMatch) GreenOk else ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(
                                if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null, tint = TextSec
                            )
                        }
                    }
                },
                visualTransformation = if (confirmVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = confirmPassword.isNotEmpty() && !pwdMatch,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = fieldColors()
            )
            if (confirmPassword.isNotEmpty() && !pwdMatch) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, null,
                        tint = ErrorRed, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Passwords do not match", fontSize = 11.sp, color = ErrorRed)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password requirements checklist
            PasswordChecklist(password)

            Spacer(modifier = Modifier.height(20.dp))

            // Terms checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (agreeTerms) Accent.copy(alpha = 0.06f) else Color.Transparent)
                    .clickable { agreeTerms = !agreeTerms }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Accent,
                        uncheckedColor = TextSec
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row {
                        Text("I agree to the ", fontSize = 13.sp, color = TextSec)
                        Text("Terms of Service", fontSize = 13.sp, color = Accent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { })
                    }
                    Row {
                        Text("and ", fontSize = 13.sp, color = TextSec)
                        Text("Privacy Policy", fontSize = 13.sp, color = Accent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { })
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (apiError != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ErrorRed.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, null,
                            tint = ErrorRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(apiError, fontSize = 12.sp, color = ErrorRed)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Register button
            Button(
                onClick = {
                    if (formValid) {
                        viewModel.clearError()
                        viewModel.register(fullName, email, password, confirmPassword)
                    }
                },
                enabled = formValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        if (formValid) 10.dp else 0.dp,
                        RoundedCornerShape(16.dp),
                        ambientColor = Accent.copy(alpha = 0.4f),
                        spotColor = Accent.copy(alpha = 0.4f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = Color.White,
                    disabledContainerColor = Accent.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White, strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create My Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                Text("  or sign up with  ", fontSize = 12.sp, color = TextSec)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialButton(
                    label = "Google",
                    badgeColor = Color(0xFF4285F4),
                    letter = "G",
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
                SocialButton(
                    label = "Facebook",
                    badgeColor = Color(0xFF1877F2),
                    letter = "f",
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Already have account
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = TextSec, fontSize = 14.sp)
                Text("Sign In", color = Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToLogin() })
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ── Reusable field ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    isError: Boolean = false,
    errorText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(icon, contentDescription = null,
                    tint = if (isError) ErrorRed else Accent,
                    modifier = Modifier.size(20.dp))
            },
            trailingIcon = trailingContent?.let { { it() } },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = fieldColors()
        )
        if (isError && errorText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ErrorOutline, null,
                    tint = ErrorRed, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(errorText, fontSize = 11.sp, color = ErrorRed)
            }
        }
    }
}

// ── Password checklist ────────────────────────────────────────────────────────
@Composable
fun PasswordChecklist(password: String) {
    val checks = listOf(
        "At least 6 characters" to (password.length >= 6),
        "Contains a letter"     to password.any { it.isLetter() },
        "Contains a number"     to password.any { it.isDigit() },
        "Contains a symbol"     to password.any { !it.isLetterOrDigit() }
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Password must include:", fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, color = TextSec)
            Spacer(modifier = Modifier.height(8.dp))
            checks.forEach { (label, passed) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(18.dp),
                        shape = CircleShape,
                        color = if (passed) GreenOk.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (passed) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (passed) GreenOk else TextSec,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(label, fontSize = 12.sp,
                        color = if (passed) TextPri else TextSec)
                }
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
fun SectionDividerLabel(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp).height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Accent)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPri)
    }
}

// ── Social button ─────────────────────────────────────────────────────────────
@Composable
fun SocialButton(
    label: String,
    badgeColor: Color,
    letter: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPri),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(20.dp), shape = CircleShape, color = badgeColor) {
                Box(contentAlignment = Alignment.Center) {
                    Text(letter, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Field colours helper ───────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor     = Accent,
    unfocusedBorderColor   = Color(0xFFE5E7EB),
    focusedLabelColor      = Accent,
    unfocusedLabelColor    = TextSec,
    focusedTextColor       = TextPri,
    unfocusedTextColor     = TextPri,
    cursorColor            = Accent,
    errorBorderColor       = ErrorRed,
    errorLabelColor        = ErrorRed,
    errorLeadingIconColor  = ErrorRed
)