package com.example.final_project.data.repository

import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.remote.CategoryApiService
import com.example.final_project.data.remote.dto.CategoryData
import com.example.final_project.data.remote.dto.CreateCategoryRequest
import retrofit2.HttpException
import java.io.IOException

class CategoryRepository(
    private val categoryApi: CategoryApiService,
    private val sessionManager: SessionManager
) {

    suspend fun getCategories(): AuthResult<List<CategoryData>> {
        return try {
            val response = categoryApi.getCategories()
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Categories loaded")
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

    suspend fun getCategoryById(id: Long): AuthResult<CategoryData> {
        return try {
            val response = categoryApi.getCategoryById(id)
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Category loaded")
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

    suspend fun createCategory(name: String): AuthResult<CategoryData> {
        return try {
            sessionManager.hydrateToken()
            val response = categoryApi.createCategory(CreateCategoryRequest(name.trim()))
            if (response.success && response.data != null) {
                AuthResult.Success(response.data, response.message ?: "Category created")
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
