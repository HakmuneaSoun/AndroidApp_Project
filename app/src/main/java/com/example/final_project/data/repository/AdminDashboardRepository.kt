package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.AdminApiService
import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.dto.DashboardData
import retrofit2.HttpException
import java.io.IOException

class AdminDashboardRepository(
    private val adminApi: AdminApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getDashboard(period: String, limit: Int = 10): AuthResult<DashboardData> {
        return try {
            sessionManager.hydrateToken()
            val response = adminApi.getDashboard(period = period, limit = limit)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Dashboard loaded")
            } else {
                AuthResult.Error(formatErrorMessage(response.message, response.errors))
            }
        } catch (e: HttpException) {
            AuthResult.Error(parseHttpError(e))
        } catch (e: IOException) {
            AuthResult.Error(ApiConstants.connectionErrorMessage())
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
