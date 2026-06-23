package com.example.final_project.presentation.admin

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
import com.example.final_project.data.remote.dto.UserProfileData
import com.example.final_project.data.repository.AdminUserRepository
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.domain.model.AdminUser
import kotlinx.coroutines.launch

data class AdminUsersUiState(
    val isLoading: Boolean = false,
    val isUpdatingUserId: Long? = null,
    val users: List<AdminUser> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminUsersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminUserRepository(
        adminApi = RetrofitClient.adminApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(AdminUsersUiState())
        private set

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getUsers()) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        users = result.data.map { it.toAdminUser() }
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateUserStatus(userId: Long, isActive: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isUpdatingUserId = userId,
                errorMessage = null,
                successMessage = null
            )
            when (val result = repository.updateUserStatus(userId, isActive)) {
                is AuthResult.Success -> {
                    val updatedUser = result.data.toAdminUser()
                    uiState = uiState.copy(
                        isUpdatingUserId = null,
                        users = uiState.users.map { user ->
                            if (user.id == updatedUser.id) updatedUser else user
                        },
                        successMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isUpdatingUserId = null,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }
}

private fun UserProfileData.toAdminUser() = AdminUser(
    id = id.toString(),
    name = name,
    email = email,
    role = role,
    isActive = isActive
)

class AdminUsersViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminUsersViewModel::class.java)) {
            return AdminUsersViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
