package com.example.final_project.presentation.tours

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.mapper.extractCategoriesFromTours
import com.example.final_project.data.mapper.toDestination
import com.example.final_project.data.mapper.toDestinations
import com.example.final_project.data.remote.RetrofitClient
import com.example.final_project.data.remote.dto.TourData
import com.example.final_project.data.remote.dto.TourRequest
import com.example.final_project.data.remote.dto.UpdateTourRequest
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.data.repository.TourRepository
import com.example.final_project.domain.model.CategoryOption
import com.example.final_project.domain.model.Destination
import kotlinx.coroutines.launch

data class ToursUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeletingTourId: Long? = null,
    val isUploadingImage: Boolean = false,
    val tours: List<Destination> = emptyList(),
    val categories: List<CategoryOption> = emptyList(),
    val selectedTour: Destination? = null,
    val pendingImageUrl: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val saveCompleted: Boolean = false
)

data class TourFormData(
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    val categoryId: Long,
    val durationHours: Int,
    val maxPeople: Int,
    val imageCover: String,
    val isActive: Boolean = true
)

class ToursViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TourRepository(
        tourApi = RetrofitClient.tourApi,
        fileApi = RetrofitClient.fileApi,
        sessionManager = RetrofitClient.getSessionManager()
    )

    var uiState by mutableStateOf(ToursUiState())
        private set

    fun loadTours() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getTours()) {
                is AuthResult.Success -> {
                    val destinations = result.data.toDestinations()
                    uiState = uiState.copy(
                        isLoading = false,
                        tours = destinations,
                        categories = extractCategoriesFromTours(result.data)
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun loadTourById(id: Long) {
        val cached = uiState.tours.find { it.id == id.toString() }
        if (cached != null) {
            uiState = uiState.copy(selectedTour = cached)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getTourById(id)) {
                is AuthResult.Success -> {
                    val destination = result.data.toDestination()
                    uiState = uiState.copy(
                        isLoading = false,
                        selectedTour = destination,
                        tours = uiState.tours.filter { it.id != destination.id } + destination
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun createTour(form: TourFormData) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null, saveCompleted = false)
            when (val result = repository.createTour(form.toCreateRequest())) {
                is AuthResult.Success -> handleSavedTour(result.data, result.message)
                is AuthResult.Error -> {
                    uiState = uiState.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateTour(tourId: Long, form: TourFormData) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null, saveCompleted = false)
            when (val result = repository.updateTour(tourId, form.toUpdateRequest())) {
                is AuthResult.Success -> handleSavedTour(result.data, result.message)
                is AuthResult.Error -> {
                    uiState = uiState.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun uploadTourImage(uri: Uri) {
        viewModelScope.launch {
            uiState = uiState.copy(isUploadingImage = true, errorMessage = null)
            when (val result = repository.uploadTourImage(getApplication(), uri)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isUploadingImage = false,
                        pendingImageUrl = result.data,
                        successMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isUploadingImage = false, errorMessage = result.message)
                }
            }
        }
    }

    fun deleteTour(tourId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isDeletingTourId = tourId,
                errorMessage = null,
                successMessage = null
            )
            when (val result = repository.deleteTour(tourId)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isDeletingTourId = null,
                        tours = uiState.tours.filter { it.id != tourId.toString() },
                        selectedTour = uiState.selectedTour
                            ?.takeIf { it.id != tourId.toString() },
                        successMessage = result.message
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isDeletingTourId = null,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun prepareTourForm(destination: Destination?) {
        uiState = uiState.copy(
            pendingImageUrl = destination?.imageUrl,
            saveCompleted = false,
            errorMessage = null,
            successMessage = null
        )
    }

    fun clearSaveCompleted() {
        uiState = uiState.copy(saveCompleted = false)
    }

    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }

    private fun handleSavedTour(data: TourData, message: String) {
        val destination = data.toDestination()
        val updatedTours = (uiState.tours.filter { it.id != destination.id } + destination)
            .sortedByDescending { it.rating }
        val categories = uiState.categories.toMutableList()
        if (categories.none { it.id == data.categoryId }) {
            categories.add(CategoryOption(data.categoryId, data.categoryName))
        }
        uiState = uiState.copy(
            isSaving = false,
            tours = updatedTours,
            categories = categories.sortedBy { it.id },
            selectedTour = destination,
            pendingImageUrl = destination.imageUrl,
            successMessage = message,
            saveCompleted = true
        )
    }

    private fun TourFormData.toCreateRequest() = TourRequest(
        title = title.trim(),
        description = description.trim(),
        location = location.trim(),
        latitude = latitude,
        longitude = longitude,
        price = price,
        categoryId = categoryId,
        durationHours = durationHours,
        maxPeople = maxPeople,
        imageCover = imageCover
    )

    private fun TourFormData.toUpdateRequest() = UpdateTourRequest(
        title = title.trim(),
        description = description.trim(),
        location = location.trim(),
        latitude = latitude,
        longitude = longitude,
        price = price,
        categoryId = categoryId,
        durationHours = durationHours,
        maxPeople = maxPeople,
        imageCover = imageCover,
        isActive = isActive
    )
}

class ToursViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToursViewModel::class.java)) {
            return ToursViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
