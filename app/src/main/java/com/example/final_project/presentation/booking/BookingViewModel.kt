package com.example.final_project.presentation.booking

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
import com.example.final_project.data.remote.dto.BookingData
import com.example.final_project.data.remote.dto.PaymentData
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.data.repository.BookingRepository
import com.example.final_project.domain.model.PaymentMethod
import kotlinx.coroutines.launch
import java.util.UUID

data class BookingFlowUiState(
    val isSubmittingBooking: Boolean = false,
    val isProcessingPayment: Boolean = false,
    val createdBooking: BookingData? = null,
    val payment: PaymentData? = null,
    val errorMessage: String? = null,
    val paymentSuccess: Boolean = false
)

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository(
        bookingApi = RetrofitClient.bookingApi,
        paymentApi = RetrofitClient.paymentApi,
        reviewApi = RetrofitClient.reviewApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(BookingFlowUiState())
        private set

    fun createBooking(tourId: Long, tourDate: String, peopleCount: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isSubmittingBooking = true,
                errorMessage = null,
                paymentSuccess = false
            )
            when (val result = repository.createBooking(tourId, tourDate, peopleCount)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isSubmittingBooking = false,
                        createdBooking = result.data
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isSubmittingBooking = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun processPayment(method: PaymentMethod) {
        val booking = uiState.createdBooking ?: return
        viewModelScope.launch {
            uiState = uiState.copy(
                isProcessingPayment = true,
                errorMessage = null,
                paymentSuccess = false
            )
            val transactionId = "TXN-${UUID.randomUUID().toString().take(12).uppercase()}"
            when (
                val result = repository.createPayment(
                    bookingId = booking.id,
                    method = method.apiValue,
                    transactionId = transactionId
                )
            ) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isProcessingPayment = false,
                        payment = result.data,
                        paymentSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isProcessingPayment = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun loadPaymentForBooking(bookingId: Long) {
        viewModelScope.launch {
            when (val result = repository.getPaymentByBooking(bookingId)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(payment = result.data)
                }
                is AuthResult.Error -> Unit
            }
        }
    }

    fun resetFlow() {
        uiState = BookingFlowUiState()
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun clearPaymentSuccess() {
        uiState = uiState.copy(paymentSuccess = false)
    }
}

class BookingViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
