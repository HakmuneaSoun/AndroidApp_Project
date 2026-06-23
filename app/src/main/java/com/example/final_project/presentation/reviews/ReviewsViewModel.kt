package com.example.final_project.presentation.reviews

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
import com.example.final_project.data.remote.dto.ReviewData
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.data.repository.BookingRepository
import kotlinx.coroutines.launch

data class ReviewsUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val reviews: List<ReviewData> = emptyList(),
    val errorMessage: String? = null,
    val submitSuccess: Boolean = false
)

class ReviewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository(
        bookingApi = RetrofitClient.bookingApi,
        paymentApi = RetrofitClient.paymentApi,
        reviewApi = RetrofitClient.reviewApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(ReviewsUiState())
        private set

    private var loadedTourId: Long? = null

    fun loadReviews(tourId: Long) {
        if (loadedTourId == tourId && uiState.reviews.isNotEmpty()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getReviewsByTour(tourId)) {
                is AuthResult.Success -> {
                    loadedTourId = tourId
                    uiState = uiState.copy(
                        isLoading = false,
                        reviews = result.data
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun refreshReviews(tourId: Long) {
        loadedTourId = null
        loadReviews(tourId)
    }

    fun submitReview(tourId: Long, rating: Int, comment: String) {
        if (rating < 1 || comment.isBlank()) {
            uiState = uiState.copy(errorMessage = "Rating and comment are required")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isSubmitting = true, errorMessage = null, submitSuccess = false)
            when (val result = repository.createReview(tourId, rating, comment)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isSubmitting = false,
                        reviews = listOf(result.data) + uiState.reviews.filter { it.id != result.data.id },
                        submitSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isSubmitting = false, errorMessage = result.message)
                }
            }
        }
    }

    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, submitSuccess = false)
    }
}

class ReviewsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
            return ReviewsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
