package com.example.final_project.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgBar = Color.White
private val BarDivider = Color(0xFFEAECF2)
private val AccentCoral = Color(0xFFFF5C47)
private val TxtSecondary = Color(0xFF7B82A0)

enum class BottomNavItem(
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    Home("Home", Icons.Filled.Home, Icons.Outlined.Home),
    Explore("Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
    Favorites("Saved", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    Profile("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppBottomBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = BarDivider)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(BgBar),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.entries.forEach { item ->
                val selected = item == selectedItem

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onItemSelected(item) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 42.dp, height = 28.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selected)
                                    AccentCoral.copy(alpha = 0.12f)
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) item.filledIcon else item.outlinedIcon,
                            contentDescription = item.label,
                            tint = if (selected) AccentCoral else TxtSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        color = if (selected) AccentCoral else TxtSecondary,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) AccentCoral else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}