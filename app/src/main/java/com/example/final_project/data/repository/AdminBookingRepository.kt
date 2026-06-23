package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.AdminApiService
import com.example.final_project.data.remote.dto.AdminBookingData
import com.example.final_project.data.remote.dto.UpdateBookingStatusRequest
import retrofit2.HttpException
import java.io.IOException

class AdminBookingRepository(
    private val adminApi: AdminApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getBookings(): AuthResult<List<AdminBookingData>> {
        return try {
            sessionManager.hydrateToken()
            val response = adminApi.getBookings()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Bookings loaded")
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

    suspend fun updateBookingStatus(bookingId: Long, status: String): AuthResult<AdminBookingData> {
        return try {
            sessionManager.hydrateToken()
            val response = adminApi.updateBookingStatus(
                bookingId = bookingId,
                request = UpdateBookingStatusRequest(status = status)
            )
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Status updated")
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
