package com.example.final_project.presentation.profile

import android.app.Application
import android.net.Uri
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
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.data.repository.ProfileRepository
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploading: Boolean = false,
    val profile: UserProfileData? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val pendingAvatarUrl: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(
        authApi = RetrofitClient.authApi,
        fileApi = RetrofitClient.fileApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getProfile()) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        profile = result.data,
                        pendingAvatarUrl = result.data.avatarUrl
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            uiState = uiState.copy(isUploading = true, errorMessage = null, successMessage = null)
            when (val result = repository.uploadAvatar(getApplication(), uri)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isUploading = false,
                        pendingAvatarUrl = result.data,
                        successMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isUploading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun saveProfile(fullName: String, phone: String?) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null, successMessage = null)
            when (
                val result = repository.updateProfile(
                    fullName = fullName,
                    phone = phone,
                    avatarUrl = uiState.pendingAvatarUrl
                )
            ) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isSaving = false,
                        profile = result.data,
                        pendingAvatarUrl = result.data.avatarUrl,
                        successMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onComplete()
        }
    }

    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }
}

class ProfileViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
