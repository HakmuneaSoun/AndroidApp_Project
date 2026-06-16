package com.example.final_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.final_project.presentation.admin.AdminDashboardScreen
import com.example.final_project.presentation.auth.login.LoginScreen
import com.example.final_project.presentation.detail.DetailScreen
import com.example.final_project.presentation.favorite.FavoriteScreen
import com.example.final_project.presentation.home.HomeScreen
import com.example.final_project.presentation.profile.ProfileScreen
import com.example.final_project.presentation.search.SearchScreen
import com.example.final_project.ui.theme.Final_ProjectTheme
import com.example.final_project.navigation.BottomNavItem
import com.example.final_project.domain.auth.UserRole

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Final_ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("login") }
    var selectedDestinationId by remember { mutableStateOf("") }

    fun navigateToTab(tab: BottomNavItem) {
        currentScreen = when (tab) {
            BottomNavItem.Home -> "home"
            BottomNavItem.Explore -> "search"
            BottomNavItem.Favorites -> "favorites"
            BottomNavItem.Profile -> "profile"
        }
    }

    fun selectedTabForScreen(): BottomNavItem = when (currentScreen) {
        "search" -> BottomNavItem.Explore
        "favorites" -> BottomNavItem.Favorites
        "profile" -> BottomNavItem.Profile
        else -> BottomNavItem.Home
    }

    fun handleLoginSuccess(role: UserRole) {
        currentScreen = when (role) {
            UserRole.ADMIN -> "admin"
            UserRole.USER -> "home"
        }
    }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onNavigateToRegister = {
                    currentScreen = "register"
                },
                onNavigateToForgotPassword = {
                    currentScreen = "forgot_password"
                },
                onLoginSuccess = ::handleLoginSuccess
            )
        }

        "home" -> {
            HomeScreen(
                onDestinationClick = { destinationId ->
                    selectedDestinationId = destinationId
                    currentScreen = "detail"
                },
                onSearchClick = { navigateToTab(BottomNavItem.Explore) },
                onFavoritesClick = { navigateToTab(BottomNavItem.Favorites) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        "detail" -> {
            DetailScreen(
                destinationId = selectedDestinationId.ifEmpty { "1" },
                onNavigateBack = {
                    currentScreen = "home"
                }
            )
        }

        "search" -> {
            SearchScreen(
                onDestinationClick = { destinationId ->
                    selectedDestinationId = destinationId
                    currentScreen = "detail"
                },
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        "favorites" -> {
            FavoriteScreen(
                onDestinationClick = { destinationId ->
                    selectedDestinationId = destinationId
                    currentScreen = "detail"
                },
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        "profile" -> {
            ProfileScreen(
                onLogout = {
                    currentScreen = "login"
                },
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        "admin" -> {
            AdminDashboardScreen(
                onNavigateBack = {
                    currentScreen = "login"
                }
            )
        }

        else -> {
            LoginScreen(
                onNavigateToRegister = { currentScreen = "register" },
                onNavigateToForgotPassword = { currentScreen = "forgot_password" },
                onLoginSuccess = ::handleLoginSuccess
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Final_ProjectTheme {
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onLoginSuccess = { _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Final_ProjectTheme {
        HomeScreen(
            onDestinationClick = {},
            onSearchClick = {},
            onFavoritesClick = {}
        )
    }
}