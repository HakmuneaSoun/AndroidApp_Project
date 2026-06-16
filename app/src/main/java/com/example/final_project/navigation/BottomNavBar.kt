package com.example.final_project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class BottomNavItem(val label: String) {
    Home("Home"),
    Explore("Explore"),
    Favorites("Favorites"),
    Profile("Profile")
}

@Composable
fun AppBottomBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = item == selectedItem
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = when (item) {
                            BottomNavItem.Home -> Icons.Default.Home
                            BottomNavItem.Explore -> if (selected) Icons.Default.Explore else Icons.Default.ExploreOff
                            BottomNavItem.Favorites -> if (selected) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                            BottomNavItem.Profile -> if (selected) Icons.Default.Person else Icons.Outlined.Person
                        },
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
