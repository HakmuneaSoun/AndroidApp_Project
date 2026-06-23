package com.example.final_project.data.repository

import android.content.Context
import android.net.Uri
import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.FileApiService
import com.example.final_project.data.remote.TourApiService
import com.example.final_project.data.remote.dto.TourData
import com.example.final_project.data.remote.dto.TourRequest
import com.example.final_project.data.remote.dto.UpdateTourRequest
import com.example.final_project.data.remote.dto.UploadType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class TourRepository(
    private val tourApi: TourApiService,
    private val fileApi: FileApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getTours(): AuthResult<List<TourData>> = apiCall {
        tourApi.getTours()
    }

    suspend fun getTourById(id: Long): AuthResult<TourData> = apiCall {
        tourApi.getTourById(id)
    }

    suspend fun createTour(request: TourRequest): AuthResult<TourData> = apiCall {
        sessionManager.hydrateToken()
        tourApi.createTour(request)
    }

    suspend fun updateTour(id: Long, request: UpdateTourRequest): AuthResult<TourData> = apiCall {
        sessionManager.hydrateToken()
        tourApi.updateTour(id, request)
    }

    suspend fun deleteTour(id: Long): AuthResult<Unit> {
        return try {
            sessionManager.hydrateToken()
            val response = tourApi.deleteTour(id)
            if (response.success) {
                AuthResult.Success(Unit, response.message ?: "Tour deleted")
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

    suspend fun uploadTourImage(context: Context, uri: Uri): AuthResult<String> {
        return try {
            sessionManager.hydrateToken()
            val part = uri.toMultipartPart(context)
            val response = fileApi.uploadFile(UploadType.TOUR, part)
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

    private suspend fun <T> apiCall(
        call: suspend () -> com.example.final_project.data.remote.dto.ApiResponse<T>
    ): AuthResult<T> {
        return try {
            val response = call()
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

    private fun Uri.toMultipartPart(context: Context): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(this) ?: "image/*"
        val bytes = contentResolver.openInputStream(this)?.use { it.readBytes() }
            ?: throw IOException("Unable to read selected image")
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", "tour.jpg", requestBody)
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
