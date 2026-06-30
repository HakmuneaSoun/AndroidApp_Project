package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.AuthApiService
import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.dto.LoginData
import com.example.final_project.data.remote.dto.LoginRequest
import com.example.final_project.data.remote.dto.RegisterRequest
import com.example.final_project.data.remote.dto.RegisterUserData
import retrofit2.HttpException
import java.io.IOException

sealed class AuthResult<out T> {
    data class Success<T>(val data: T, val message: String) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {

    suspend fun login(email: String, password: String): AuthResult<LoginData> {
        return try {
            val response = api.login(LoginRequest(email.trim(), password))
            if (response.success && response.data != null) {
                sessionManager.saveLogin(response.data)
                AuthResult.Success(response.data, response.message ?: "Login successful")
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

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult<RegisterUserData> {
        return try {
            val response = api.register(
                RegisterRequest(
                    fullName = fullName.trim(),
                    email = email.trim(),
                    password = password,
                    confirmPassword = confirmPassword,
                    role = "ROLE_USER"
                )
            )
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Registration successful")
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

    suspend fun logout() {
        try {
            sessionManager.hydrateToken()
            if (!sessionManager.getToken().isNullOrBlank()) {
                api.logout()
            }
        } catch (_: Exception) {
            // Always clear local session even if the server call fails.
        } finally {
            sessionManager.clearSession()
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
            exception.code() == 401 -> "Incorrect email or password"
            else -> "Server error (${exception.code()})"
        }
    }
}
