package com.example.final_project.presentation.favorite

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.mapper.toDestination
import com.example.final_project.data.mapper.toFavoriteDestinations
import com.example.final_project.data.remote.RetrofitClient
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.data.repository.FavoriteRepository
import com.example.final_project.domain.model.Destination
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Destination> = emptyList(),
    val favoriteStatusByTourId: Map<Long, Boolean> = emptyMap(),
    val togglingTourId: Long? = null,
    val errorMessage: String? = null
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FavoriteRepository(
        favoriteApi = RetrofitClient.favoriteApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(FavoritesUiState())
        private set

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getFavorites()) {
                is AuthResult.Success -> {
                    val favorites = result.data.toFavoriteDestinations()
                    val statusMap = favorites.associate { it.id.toLong() to true }
                    uiState = uiState.copy(
                        isLoading = false,
                        favorites = favorites,
                        favoriteStatusByTourId = uiState.favoriteStatusByTourId + statusMap
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun checkFavorite(tourId: Long) {
        viewModelScope.launch {
            when (val result = repository.checkFavorite(tourId)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        favoriteStatusByTourId = uiState.favoriteStatusByTourId +
                            (tourId to result.data.isFavorite)
                    )
                }
                is AuthResult.Error -> Unit
            }
        }
    }

    fun isFavorite(tourId: Long): Boolean {
        return uiState.favoriteStatusByTourId[tourId] == true
    }

    fun toggleFavorite(tourId: Long, tour: Destination? = null) {
        if (uiState.togglingTourId == tourId) return
        if (isFavorite(tourId)) {
            removeFavorite(tourId)
        } else {
            addFavorite(tourId, tour)
        }
    }

    fun addFavorite(tourId: Long, tour: Destination? = null) {
        viewModelScope.launch {
            uiState = uiState.copy(togglingTourId = tourId, errorMessage = null)
            when (val result = repository.addFavorite(tourId)) {
                is AuthResult.Success -> {
                    val destination = result.data.toDestination()
                    uiState = uiState.copy(
                        togglingTourId = null,
                        favorites = (uiState.favorites.filter { it.id != destination.id } + destination)
                            .sortedByDescending { it.rating },
                        favoriteStatusByTourId = uiState.favoriteStatusByTourId + (tourId to true)
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(togglingTourId = null, errorMessage = result.message)
                }
            }
        }
    }

    fun removeFavorite(tourId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(togglingTourId = tourId, errorMessage = null)
            when (val result = repository.removeFavorite(tourId)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        togglingTourId = null,
                        favorites = uiState.favorites.filter { it.id != tourId.toString() },
                        favoriteStatusByTourId = uiState.favoriteStatusByTourId + (tourId to false)
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(togglingTourId = null, errorMessage = result.message)
                }
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

class FavoritesViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
