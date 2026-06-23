package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.BookingApiService
import com.example.final_project.data.remote.PaymentApiService
import com.example.final_project.data.remote.ReviewApiService
import com.example.final_project.data.remote.dto.ApiResponse
import com.example.final_project.data.remote.dto.BookingData
import com.example.final_project.data.remote.dto.CreateBookingRequest
import com.example.final_project.data.remote.dto.CreatePaymentRequest
import com.example.final_project.data.remote.dto.CreateReviewRequest
import com.example.final_project.data.remote.dto.PaymentData
import com.example.final_project.data.remote.dto.ReviewData
import retrofit2.HttpException
import java.io.IOException

class BookingRepository(
    private val bookingApi: BookingApiService,
    private val paymentApi: PaymentApiService,
    private val reviewApi: ReviewApiService,
    private val sessionManager: SessionManager
) {

    suspend fun createBooking(
        tourId: Long,
        tourDate: String,
        peopleCount: Int
    ): AuthResult<BookingData> = authorizedCall {
        bookingApi.createBooking(
            CreateBookingRequest(
                tourId = tourId,
                tourDate = tourDate,
                peopleCount = peopleCount
            )
        )
    }

    suspend fun createPayment(
        bookingId: Long,
        method: String,
        transactionId: String
    ): AuthResult<PaymentData> = authorizedCall {
        paymentApi.createPayment(
            CreatePaymentRequest(
                method = method,
                bookingId = bookingId,
                transactionId = transactionId
            )
        )
    }

    suspend fun getPaymentByBooking(bookingId: Long): AuthResult<PaymentData> = authorizedCall {
        paymentApi.getPaymentByBooking(bookingId)
    }

    suspend fun getReviewsByTour(tourId: Long): AuthResult<List<ReviewData>> = authorizedCall {
        reviewApi.getReviewsByTour(tourId)
    }

    suspend fun createReview(
        tourId: Long,
        rating: Int,
        comment: String
    ): AuthResult<ReviewData> = authorizedCall {
        reviewApi.createReview(
            CreateReviewRequest(
                tourId = tourId,
                rating = rating,
                comment = comment.trim()
            )
        )
    }

    private suspend fun <T> authorizedCall(
        block: suspend () -> ApiResponse<T>
    ): AuthResult<T> {
        return try {
            sessionManager.hydrateToken()
            val response = block()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Success")
            } else {
                AuthResult.Error(formatErrorMessage(response.message, response.errors))
            }
        } catch (e: HttpException) {
            AuthResult.Error(parseHttpError(e))
        } catch (e: IOException) {
            AuthResult.Error("Network error. Check your connection and server URL.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Something went wrong")
        }
    }

    private fun formatErrorMessage(message: String?, errors: Map<String, String>?): String {
        if (!errors.isNullOrEmpty()) return errors.values.joinToString("\n")
        return message ?: "Request failed"
    }

    private fun parseHttpError(exception: HttpException): String {
        val errorBody = exception.response()?.errorBody()?.string()
        return when {
            !errorBody.isNullOrBlank() -> errorBody
            exception.code() == 401 -> "Session expired. Please sign in again."
            else -> "Server error (${exception.code()})"
        }
    }
}
