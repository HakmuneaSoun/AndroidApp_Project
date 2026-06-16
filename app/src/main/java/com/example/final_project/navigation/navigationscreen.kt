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
import com.example.final_project.presentation.admin.AdminDashboardScreen
import com.example.final_project.domain.auth.UserRole

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Detail : Screen("detail")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object Admin : Screen("admin")
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
    fun navigateToTab(tab: BottomNavItem) {
        navState.navigateTo(
            when (tab) {
                BottomNavItem.Home -> Screen.Home
                BottomNavItem.Explore -> Screen.Search
                BottomNavItem.Favorites -> Screen.Favorites
                BottomNavItem.Profile -> Screen.Profile
            }
        )
    }

    fun selectedTabForScreen(): BottomNavItem = when (navState.currentScreen) {
        Screen.Search -> BottomNavItem.Explore
        Screen.Favorites -> BottomNavItem.Favorites
        Screen.Profile -> BottomNavItem.Profile
        else -> BottomNavItem.Home
    }

    fun handleLoginSuccess(role: UserRole) {
        navState.navigateTo(
            when (role) {
                UserRole.ADMIN -> Screen.Admin
                UserRole.USER -> Screen.Home
            }
        )
    }

    when (navState.currentScreen) {
        Screen.Login -> {
            LoginScreen(
                onNavigateToRegister = { navState.navigateTo(Screen.Register) },
                onNavigateToForgotPassword = { navState.navigateTo(Screen.ForgotPassword) },
                onLoginSuccess = ::handleLoginSuccess
            )
        }

        Screen.Register -> {
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToForgotPassword = {},
                onLoginSuccess = ::handleLoginSuccess
            )
        }

        Screen.ForgotPassword -> {
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToForgotPassword = {},
                onLoginSuccess = ::handleLoginSuccess
            )
        }

        Screen.Home -> {
            HomeScreen(
                onDestinationClick = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onSearchClick = { navigateToTab(BottomNavItem.Explore) },
                onFavoritesClick = { navigateToTab(BottomNavItem.Favorites) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
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
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        Screen.Favorites -> {
            FavoriteScreen(
                onDestinationClick = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        Screen.Profile -> {
            ProfileScreen(
                onLogout = { navState.navigateTo(Screen.Login) },
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        Screen.Admin -> {
            AdminDashboardScreen(
                onNavigateBack = { navState.navigateTo(Screen.Login) }
            )
        }
    }
}

