package com.example.final_project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.final_project.presentation.auth.login.LoginScreen
import com.example.final_project.presentation.detail.DetailScreen
import com.example.final_project.presentation.favorite.FavoriteScreen
import com.example.final_project.presentation.home.HomeScreen
import com.example.final_project.presentation.profile.ProfileScreen
import com.example.final_project.presentation.search.SearchScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Detail : Screen("detail")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
}

class NavigationState {
    var currentScreen by mutableStateOf<Screen>(Screen.Login)
        private set
    var selectedDestinationId by mutableStateOf("")
        private set

    fun navigateTo(screen: Screen, destinationId: String = "") {
        selectedDestinationId = destinationId
        currentScreen = screen
    }

    fun navigateBack() {
        when (currentScreen) {
            Screen.Detail, Screen.Search, Screen.Favorites, Screen.Profile -> {
                currentScreen = Screen.Home
            }
            else -> {
                currentScreen = Screen.Login
            }
        }
    }
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}

@Composable
fun AppNavigation(navState: NavigationState = rememberNavigationState()) {
    when (navState.currentScreen) {
        Screen.Login -> {
            LoginScreen(
                onNavigateToRegister = { navState.navigateTo(Screen.Register) },
                onNavigateToForgotPassword = { navState.navigateTo(Screen.ForgotPassword) },
                onLoginSuccess = { navState.navigateTo(Screen.Home) }
            )
        }

        Screen.Register -> {
            // You can implement RegisterScreen similarly
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToForgotPassword = {},
                onLoginSuccess = { navState.navigateTo(Screen.Home) }
            )
        }

        Screen.ForgotPassword -> {
            // You can implement ForgotPasswordScreen similarly
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToForgotPassword = {},
                onLoginSuccess = {}
            )
        }

        Screen.Home -> {
            HomeScreen(
                onDestinationClick = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onSearchClick = { navState.navigateTo(Screen.Search) },
                onFavoritesClick = { navState.navigateTo(Screen.Favorites) }
            )
        }

        Screen.Detail -> {
            DetailScreen(
                destinationId = navState.selectedDestinationId.ifEmpty { "1" },
                onNavigateBack = { navState.navigateBack() }
            )
        }

        Screen.Search -> {
            SearchScreen(
                onDestinationClick = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onNavigateBack = { navState.navigateBack() }
            )
        }

        Screen.Favorites -> {
            FavoriteScreen(
                onDestinationClick = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onNavigateBack = { navState.navigateBack() }
            )
        }

        Screen.Profile -> {
            ProfileScreen(
                onLogout = { navState.navigateTo(Screen.Login) },
                onNavigateBack = { navState.navigateBack() }
            )
        }
    }
}

