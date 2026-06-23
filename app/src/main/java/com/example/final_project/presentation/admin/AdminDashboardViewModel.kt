package com.example.final_project.presentation.admin

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.RetrofitClient
import com.example.final_project.data.remote.dto.BookingChartPoint
import com.example.final_project.data.remote.dto.DashboardRecentBookingData
import com.example.final_project.data.remote.dto.DashboardStats
import com.example.final_project.data.repository.AdminDashboardRepository
import com.example.final_project.data.repository.AuthResult
import kotlinx.coroutines.launch

enum class DashboardPeriod(val apiValue: String, val label: String) {
    THIS_MONTH("THIS_MONTH", "This Month"),
    THIS_WEEK("THIS_WEEK", "This Week"),
    THIS_YEAR("THIS_YEAR", "This Year")
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val chartData: List<BookingChartPoint> = emptyList(),
    val recentBookings: List<DashboardRecentBookingData> = emptyList(),
    val selectedPeriod: DashboardPeriod = DashboardPeriod.THIS_MONTH,
    val errorMessage: String? = null
)

class AdminDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminDashboardRepository(
        adminApi = RetrofitClient.adminApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboard()
    }

    fun loadDashboard(period: DashboardPeriod = uiState.selectedPeriod) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null,
                selectedPeriod = period
            )
            when (val result = repository.getDashboard(period = period.apiValue, limit = 10)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        stats = result.data.stats,
                        chartData = result.data.bookingsChart,
                        recentBookings = result.data.recentBookings
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun changePeriod(period: DashboardPeriod) {
        if (period != uiState.selectedPeriod) {
            loadDashboard(period)
        }
    }
}

class AdminDashboardViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminDashboardViewModel::class.java)) {
            return AdminDashboardViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
