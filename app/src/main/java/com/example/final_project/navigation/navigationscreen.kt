package com.example.final_project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_project.domain.model.BookingDraft
import com.example.final_project.domain.model.PaymentMethod
import com.example.final_project.domain.model.mockUserNotifications
import com.example.final_project.presentation.booking.BookingViewModel
import com.example.final_project.presentation.booking.BookingViewModelFactory
import com.example.final_project.presentation.reviews.ReviewsViewModel
import com.example.final_project.presentation.reviews.ReviewsViewModelFactory
import com.example.final_project.presentation.admin.AdminDashboardScreen
import com.example.final_project.presentation.auth.login.LoginScreen
import com.example.final_project.presentation.auth.register.RegisterScreen
import com.example.final_project.presentation.booking.BookingScreen
import com.example.final_project.presentation.booking.PaymentScreen
import com.example.final_project.presentation.detail.DetailScreen
import com.example.final_project.presentation.favorite.FavoriteScreen
import com.example.final_project.presentation.favorite.FavoritesViewModel
import com.example.final_project.presentation.favorite.FavoritesViewModelFactory
import com.example.final_project.presentation.home.HomeScreen
import com.example.final_project.presentation.notifications.NotificationStyle
import com.example.final_project.presentation.notifications.NotificationsScreen
import com.example.final_project.presentation.profile.ProfileScreen
import com.example.final_project.presentation.search.SearchScreen
import com.example.final_project.presentation.splash.SplashScreen
import com.example.final_project.presentation.tours.ToursViewModel
import com.example.final_project.presentation.tours.ToursViewModelFactory

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Detail : Screen("detail")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object Booking : Screen("booking")
    object Payment : Screen("payment")
    object Notifications : Screen("notifications")
    object AdminDashboard : Screen("admin_dashboard")
}

class NavigationState {
    var currentScreen by mutableStateOf<Screen>(Screen.Splash)
        private set
    var selectedDestinationId by mutableStateOf("")
        private set
    var detailReturnScreen by mutableStateOf<Screen>(Screen.Home)
        private set
    var notificationsReturnScreen by mutableStateOf<Screen>(Screen.Home)
        private set
    var bookingDraft by mutableStateOf<BookingDraft?>(null)
        private set
    var selectedPaymentMethod by mutableStateOf(PaymentMethod.VisaMasterCard)
        private set

    fun navigateTo(screen: Screen, destinationId: String = "") {
        if (screen == Screen.Detail) {
            detailReturnScreen = currentScreen
        }
        if (destinationId.isNotEmpty()) {
            selectedDestinationId = destinationId
        }
        currentScreen = screen
    }

    fun startBooking(destinationId: String) {
        selectedDestinationId = destinationId
        bookingDraft = BookingDraft(destinationId = destinationId)
        currentScreen = Screen.Booking
    }

    fun updateBooking(draft: BookingDraft) {
        bookingDraft = draft
    }

    fun updatePaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod = method
    }

    fun openNotifications() {
        notificationsReturnScreen = currentScreen
        currentScreen = Screen.Notifications
    }

    fun navigateBack() {
        currentScreen = when (currentScreen) {
            Screen.Notifications -> notificationsReturnScreen
            Screen.Payment -> Screen.Booking
            Screen.Booking -> Screen.Detail
            Screen.Detail -> detailReturnScreen
            Screen.Search, Screen.Favorites, Screen.Profile -> Screen.Home
            else -> Screen.Login
        }
    }

    fun resetForLogout() {
        currentScreen = Screen.Login
        selectedDestinationId = ""
        detailReturnScreen = Screen.Home
        notificationsReturnScreen = Screen.Home
        bookingDraft = null
        selectedPaymentMethod = PaymentMethod.VisaMasterCard
    }
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}

@Composable
fun AppNavigation(navState: NavigationState = rememberNavigationState()) {
    val context = LocalContext.current
    val toursViewModel: ToursViewModel = viewModel(
        factory = ToursViewModelFactory(context.applicationContext as android.app.Application)
    )
    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(context.applicationContext as android.app.Application)
    )
    val bookingViewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(context.applicationContext as android.app.Application)
    )
    val reviewsViewModel: ReviewsViewModel = viewModel(
        factory = ReviewsViewModelFactory(context.applicationContext as android.app.Application)
    )
    val toursState = toursViewModel.uiState
    val bookingFlowState = bookingViewModel.uiState
    val userNotifications = remember { mockUserNotifications.toMutableStateList() }

    fun handleLogout() {
        bookingViewModel.resetFlow()
        favoritesViewModel.reset()
        reviewsViewModel.reset()
        navState.resetForLogout()
    }

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
        Screen.Search, Screen.Booking, Screen.Payment -> BottomNavItem.Explore
        Screen.Favorites -> BottomNavItem.Favorites
        Screen.Profile -> BottomNavItem.Profile
        else -> BottomNavItem.Home
    }

    when (navState.currentScreen) {
        Screen.Splash -> {
            SplashScreen(
                onTimeout = { navState.navigateTo(Screen.Login) }
            )
        }

        Screen.Login -> {
            LoginScreen(
                onNavigateToRegister = { navState.navigateTo(Screen.Register) },
                onNavigateToForgotPassword = { navState.navigateTo(Screen.ForgotPassword) },
                onLoginSuccess = { navState.navigateTo(Screen.Home) },
                onAdminLoginSuccess = { navState.navigateTo(Screen.AdminDashboard) }
            )
        }

        Screen.AdminDashboard -> {
            AdminDashboardScreen(
                onNavigateBack = { handleLogout() }
            )
        }

        Screen.Register -> {
            RegisterScreen(
                onNavigateToLogin = { navState.navigateTo(Screen.Login) },
                onRegisterSuccess = { navState.navigateTo(Screen.Login) }
            )
        }

        Screen.ForgotPassword -> {
            // You can implement ForgotPasswordScreen similarly
            LoginScreen(
                onNavigateToRegister = {},
                onNavigateToForgotPassword = {},
                onLoginSuccess = {},
                onAdminLoginSuccess = {}
            )
        }

        Screen.Home -> {
            HomeScreen(
                destinations = toursState.tours.filter { it.isActive },
                isLoading = toursState.isLoading,
                onDestinationClick = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onSearchClick = { navigateToTab(BottomNavItem.Explore) },
                onFavoritesClick = { navigateToTab(BottomNavItem.Favorites) },
                onNotificationsClick = { navState.openNotifications() },
                notificationUnreadCount = userNotifications.count { !it.isRead },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        Screen.Detail -> {
            DetailScreen(
                destinationId = navState.selectedDestinationId.ifEmpty { toursState.tours.firstOrNull()?.id ?: "1" },
                toursViewModel = toursViewModel,
                favoritesViewModel = favoritesViewModel,
                reviewsViewModel = reviewsViewModel,
                onNavigateBack = { navState.navigateBack() },
                onNavigateToDestination = { destinationId ->
                    navState.navigateTo(Screen.Detail, destinationId)
                },
                onBookNow = {
                    bookingViewModel.resetFlow()
                    navState.startBooking(navState.selectedDestinationId.ifEmpty { "1" })
                },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        Screen.Booking -> {
            val draft = navState.bookingDraft
            val destination = toursState.tours.find {
                it.id == (draft?.destinationId ?: navState.selectedDestinationId)
            } ?: toursState.tours.firstOrNull()

            LaunchedEffect(bookingFlowState.createdBooking?.id) {
                if (bookingFlowState.createdBooking != null && navState.currentScreen == Screen.Booking) {
                    navState.navigateTo(Screen.Payment)
                }
            }

            if (draft != null && destination != null) {
                BookingScreen(
                    destination = destination,
                    booking = draft,
                    isSubmitting = bookingFlowState.isSubmittingBooking,
                    errorMessage = bookingFlowState.errorMessage,
                    onBookingChange = { navState.updateBooking(it) },
                    onNavigateBack = {
                        bookingViewModel.resetFlow()
                        navState.navigateBack()
                    },
                    onContinueToPayment = {
                        val tourId = destination.id.toLongOrNull() ?: return@BookingScreen
                        bookingViewModel.createBooking(tourId, draft.tourDate, draft.travelers)
                    },
                    onClearError = { bookingViewModel.clearError() }
                )
            }
        }

        Screen.Payment -> {
            val createdBooking = bookingFlowState.createdBooking

            if (createdBooking != null) {
                PaymentScreen(
                    booking = createdBooking,
                    selectedMethod = navState.selectedPaymentMethod,
                    isProcessing = bookingFlowState.isProcessingPayment,
                    errorMessage = bookingFlowState.errorMessage,
                    onMethodChange = { navState.updatePaymentMethod(it) },
                    onNavigateBack = { navState.navigateBack() },
                    onPayNow = {
                        bookingViewModel.processPayment(navState.selectedPaymentMethod)
                    },
                    paymentSuccess = bookingFlowState.paymentSuccess,
                    onPaymentSuccessDismiss = {
                        bookingViewModel.clearPaymentSuccess()
                        bookingViewModel.resetFlow()
                        navState.navigateTo(Screen.Home)
                    }
                )
            }
        }

        Screen.Search -> {
            SearchScreen(
                destinations = toursState.tours.filter { it.isActive },
                isLoading = toursState.isLoading,
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
                onTabSelected = ::navigateToTab,
                viewModel = favoritesViewModel
            )
        }

        Screen.Profile -> {
            ProfileScreen(
                onLogout = { handleLogout() },
                onNavigateBack = { navigateToTab(BottomNavItem.Home) },
                onNotificationsClick = { navState.openNotifications() },
                selectedTab = selectedTabForScreen(),
                onTabSelected = ::navigateToTab
            )
        }

        Screen.Notifications -> {
            NotificationsScreen(
                style = NotificationStyle.User,
                notifications = userNotifications,
                onNavigateBack = { navState.navigateBack() },
                onNotificationsChange = { updated ->
                    userNotifications.clear()
                    userNotifications.addAll(updated)
                }
            )
        }
    }
}
