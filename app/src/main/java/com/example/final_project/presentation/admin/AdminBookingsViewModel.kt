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
import com.example.final_project.data.remote.dto.AdminBookingData
import com.example.final_project.data.repository.AdminBookingRepository
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.domain.model.AdminBooking
import kotlinx.coroutines.launch

val bookingStatusOptions = listOf("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED")

data class AdminBookingsUiState(
    val isLoading: Boolean = false,
    val isUpdatingBookingId: Long? = null,
    val bookings: List<AdminBooking> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminBookingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminBookingRepository(
        adminApi = RetrofitClient.adminApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(AdminBookingsUiState())
        private set

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getBookings()) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        bookings = result.data.map { it.toAdminBooking() }
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateBookingStatus(bookingId: Long, status: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isUpdatingBookingId = bookingId,
                errorMessage = null,
                successMessage = null
            )
            when (val result = repository.updateBookingStatus(bookingId, status)) {
                is AuthResult.Success -> {
                    val updated = result.data.toAdminBooking()
                    uiState = uiState.copy(
                        isUpdatingBookingId = null,
                        bookings = uiState.bookings.map { booking ->
                            if (booking.id == updated.id) updated else booking
                        },
                        successMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isUpdatingBookingId = null,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }
}

private fun AdminBookingData.toAdminBooking() = AdminBooking(
    id = id,
    bookingCode = bookingCode,
    userId = userId,
    customerName = userName,
    tourId = tourId,
    tourName = tourTitle,
    date = tourDate,
    people = peopleCount,
    status = status,
    price = totalPrice
)

class AdminBookingsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminBookingsViewModel::class.java)) {
            return AdminBookingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
