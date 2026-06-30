package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.FavoriteApiService
import com.example.final_project.data.remote.dto.AddFavoriteRequest
import com.example.final_project.data.remote.dto.FavoriteCheckData
import com.example.final_project.data.remote.dto.FavoriteTourData
import retrofit2.HttpException
import java.io.IOException

class FavoriteRepository(
    private val favoriteApi: FavoriteApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getFavorites(): AuthResult<List<FavoriteTourData>> = authorizedCall {
        favoriteApi.getFavorites()
    }

    suspend fun addFavorite(tourId: Long): AuthResult<FavoriteTourData> = authorizedCall {
        favoriteApi.addFavorite(AddFavoriteRequest(tourId))
    }

    suspend fun checkFavorite(tourId: Long): AuthResult<FavoriteCheckData> = authorizedCall {
        favoriteApi.checkFavorite(tourId)
    }

    suspend fun removeFavorite(tourId: Long): AuthResult<Unit> {
        return try {
            sessionManager.hydrateToken()
            val response = favoriteApi.removeFavorite(tourId)
            if (response.success) {
                AuthResult.Success(Unit, response.message ?: "Removed from favorites")
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

    private suspend fun <T> authorizedCall(
        block: suspend () -> com.example.final_project.data.remote.dto.ApiResponse<T>
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
