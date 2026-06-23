package com.example.final_project.data.repository

import android.content.Context
import android.net.Uri
import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.AuthApiService
import com.example.final_project.data.remote.FileApiService
import com.example.final_project.data.remote.dto.UpdateProfileRequest
import com.example.final_project.data.remote.dto.UploadType
import com.example.final_project.data.remote.dto.UserProfileData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class ProfileRepository(
    private val authApi: AuthApiService,
    private val fileApi: FileApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getProfile(): AuthResult<UserProfileData> {
        return try {
            sessionManager.hydrateToken()
            val response = authApi.getMe()
            if (response.success && response.data != null) {
                sessionManager.saveProfile(
                    name = response.data.name,
                    phone = response.data.phone,
                    avatarUrl = response.data.avatarUrl
                )
                AuthResult.Success(response.data, response.message ?: "Profile loaded")
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

    suspend fun updateProfile(
        fullName: String,
        phone: String?,
        avatarUrl: String?
    ): AuthResult<UserProfileData> {
        return try {
            sessionManager.hydrateToken()
            val response = authApi.updateProfile(
                UpdateProfileRequest(
                    fullName = fullName.trim(),
                    phone = phone?.trim()?.ifBlank { null },
                    avatarUrl = avatarUrl?.ifBlank { null }
                )
            )
            if (response.success && response.data != null) {
                sessionManager.saveProfile(
                    name = response.data.name,
                    phone = response.data.phone,
                    avatarUrl = response.data.avatarUrl
                )
                AuthResult.Success(response.data, response.message ?: "Profile updated")
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

    suspend fun uploadAvatar(context: Context, uri: Uri): AuthResult<String> {
        return try {
            sessionManager.hydrateToken()
            val part = uri.toMultipartPart(context)
            val response = fileApi.uploadFile(UploadType.AVATAR, part)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data.imageUrl, response.message ?: "Image uploaded")
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

    suspend fun logout() {
        sessionManager.clearSession()
    }

    private fun Uri.toMultipartPart(context: Context): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(this) ?: "image/*"
        val bytes = contentResolver.openInputStream(this)?.use { it.readBytes() }
            ?: throw IOException("Unable to read selected image")
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", "avatar.jpg", requestBody)
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
