package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.AdminApiService
import com.example.final_project.data.remote.dto.UpdateUserStatusRequest
import com.example.final_project.data.remote.dto.UserProfileData
import retrofit2.HttpException
import java.io.IOException

class AdminUserRepository(
    private val adminApi: AdminApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getUsers(): AuthResult<List<UserProfileData>> {
        return try {
            sessionManager.hydrateToken()
            val response = adminApi.getUsers()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Users loaded")
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

    suspend fun updateUserStatus(userId: Long, isActive: Boolean): AuthResult<UserProfileData> {
        return try {
            sessionManager.hydrateToken()
            val response = adminApi.updateUserStatus(
                userId = userId,
                request = UpdateUserStatusRequest(isActive = isActive)
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
        if (!errors.isNullOrEmpty()) {
            return errors.values.joinToString("\n")
        }
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
