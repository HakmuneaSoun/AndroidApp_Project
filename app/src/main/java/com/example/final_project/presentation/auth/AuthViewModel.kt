package com.example.final_project.presentation.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.remote.RetrofitClient
import com.example.final_project.data.repository.AuthRepository
import com.example.final_project.data.repository.AuthResult
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccessIsAdmin: Boolean? = null,
    val registerSuccess: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(
        api = RetrofitClient.authApi,
        sessionManager = RetrofitClient.getSessionManager()
    )

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Email and password are required")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, loginSuccessIsAdmin = null)
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> {
                    val isAdmin = result.data.role.equals("admin", ignoreCase = true) ||
                        result.data.role.equals("ROLE_ADMIN", ignoreCase = true)
                    uiState = uiState.copy(
                        isLoading = false,
                        loginSuccessIsAdmin = isAdmin
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, registerSuccess = false)
            when (
                val result = repository.register(fullName, email, password, confirmPassword)
            ) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(isLoading = false, registerSuccess = true)
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun clearLoginSuccess() {
        uiState = uiState.copy(loginSuccessIsAdmin = null)
    }

    fun clearRegisterSuccess() {
        uiState = uiState.copy(registerSuccess = false)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

class AuthViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
