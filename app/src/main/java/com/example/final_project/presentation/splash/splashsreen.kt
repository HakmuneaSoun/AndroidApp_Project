package com.example.final_project.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.data.remote.RetrofitClient
import kotlinx.coroutines.delay

// ── Design tokens ─────────────────────────────────────────────────────────────
private val Purple900 = Color(0xFF2D1B69)
private val Purple700 = Color(0xFF5E35B1)
private val Indigo500 = Color(0xFF667eea)
private val Periwinkle = Color(0xFF9BB8FF)
private val Gold       = Color(0xFFFFD700)
private val White80    = Color.White.copy(alpha = 0.80f)
private val White50    = Color.White.copy(alpha = 0.50f)
private val White15    = Color.White.copy(alpha = 0.15f)
private val White08    = Color.White.copy(alpha = 0.08f)

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    // ── Entrance animations (one-shot) ────────────────────────────────────────
    var started by remember { mutableStateOf(false) }

    val logoScale   by animateFloatAsState(if (started) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
    val logoAlpha   by animateFloatAsState(if (started) 1f else 0f,
        animationSpec = tween(600))
    val titleOffset by animateFloatAsState(if (started) 0f else 60f,
        animationSpec = tween(700, easing = EaseOutCubic))
    val titleAlpha  by animateFloatAsState(if (started) 1f else 0f,
        animationSpec = tween(700, delayMillis = 200))
    val tagAlpha    by animateFloatAsState(if (started) 1f else 0f,
        animationSpec = tween(700, delayMillis = 450))
    val pillsAlpha  by animateFloatAsState(if (started) 1f else 0f,
        animationSpec = tween(700, delayMillis = 650))
    val barAlpha    by animateFloatAsState(if (started) 1f else 0f,
        animationSpec = tween(500, delayMillis = 900))

    // ── Infinite ambient animations ────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition()

    // Soft pulsing glow behind the logo
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.88f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine),
            RepeatMode.Reverse))
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine),
            RepeatMode.Reverse))

    // Floating decorative circles drift
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -18f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine),
            RepeatMode.Reverse))
    val floatY2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(2400, easing = EaseInOutSine),
            RepeatMode.Reverse))

    // Loading bar progress
    val barProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutCubic),
            RepeatMode.Restart))

    // Small shimmer on tagline
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Restart))

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        started = true
        RetrofitClient.warmupServer()
        delay(2500)
        onTimeout()
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Purple900, Purple700, Indigo500)
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // ── Background decoration: large blurred spheres ───────────────────
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .graphicsLayer { translationY = floatY }
                .size(220.dp)
                .blur(40.dp)
                .clip(CircleShape)
                .background(Periwinkle.copy(alpha = 0.30f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 60.dp)
                .graphicsLayer { translationY = floatY2 }
                .size(180.dp)
                .blur(36.dp)
                .clip(CircleShape)
                .background(Gold.copy(alpha = 0.18f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-70).dp, y = 40.dp)
                .graphicsLayer { translationY = floatY }
                .size(140.dp)
                .blur(30.dp)
                .clip(CircleShape)
                .background(Indigo500.copy(alpha = 0.40f))
        )

        // ── Decorative small floating pills ───────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 100.dp)
                .alpha(pillsAlpha)
                .graphicsLayer { translationY = floatY2 }
        ) {
            FloatingPill("🛕 Angkor Wat")
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 160.dp)
                .alpha(pillsAlpha)
                .graphicsLayer { translationY = floatY }
        ) {
            FloatingPill("🏖️ Koh Rong")
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 180.dp)
                .alpha(pillsAlpha)
                .graphicsLayer { translationY = floatY2 * 0.6f }
        ) {
            FloatingPill("🌿 Kampot")
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 28.dp, bottom = 230.dp)
                .alpha(pillsAlpha)
                .graphicsLayer { translationY = floatY * 0.7f }
        ) {
            FloatingPill("🏛️ Phnom Penh")
        }

        // ── Main content column ────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Pulsing glow ring behind logo
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(glowScale)
                        .alpha(glowAlpha)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Periwinkle.copy(alpha = 0.6f), Color.Transparent)
                            )
                        )
                )

                // Logo card
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha),
                    shape = RoundedCornerShape(30.dp),
                    color = White15,
                    shadowElevation = 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Inner gradient surface
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(White15, White08)
                                    ),
                                    shape = RoundedCornerShape(30.dp)
                                )
                        )
                        // Gold accent ring
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Gold.copy(alpha = 0.12f))
                        )
                        Icon(
                            Icons.Default.Map,
                            contentDescription = "Logo",
                            modifier = Modifier.size(52.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text = "Cambodia Tour",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .graphicsLayer { translationY = titleOffset }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Gold accent line
            Box(
                modifier = Modifier
                    .alpha(titleAlpha)
                    .width(60.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(colors = listOf(Gold.copy(alpha = 0f), Gold, Gold.copy(alpha = 0f)))
                    )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline
            Text(
                text = "Discover the Kingdom of Wonder",
                fontSize = 15.sp,
                color = White80,
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp,
                modifier = Modifier.alpha(tagAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-tagline with shimmering effect
            Text(
                text = "Temples • Beaches • History • Nature",
                fontSize = 12.sp,
                color = White50,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp,
                modifier = Modifier.alpha(tagAlpha * shimmer.coerceIn(0.5f, 1f))
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Loading bar
            Box(
                modifier = Modifier
                    .alpha(barAlpha)
                    .width(160.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(White15)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(barProgress)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(colors = listOf(Gold, Periwinkle))
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Loading your adventure…",
                fontSize = 11.sp,
                color = White50,
                modifier = Modifier.alpha(barAlpha)
            )
        }

        // Version pill at the very bottom
        Text(
            text = "v1.0.0",
            fontSize = 11.sp,
            color = White50,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
                .alpha(barAlpha)
        )
    }
}

@Composable
fun FloatingPill(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White15
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}