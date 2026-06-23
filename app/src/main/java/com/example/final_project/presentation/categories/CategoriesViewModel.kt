package com.example.final_project.presentation.categories

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.local.SessionManager
import com.example.final_project.data.mapper.toCategoryOptions
import com.example.final_project.data.mapper.toTourCategories
import com.example.final_project.data.remote.RetrofitClient
import com.example.final_project.data.remote.dto.CategoryData
import com.example.final_project.data.repository.AuthResult
import com.example.final_project.data.repository.CategoryRepository
import com.example.final_project.domain.model.CategoryOption
import com.example.final_project.domain.model.TourCategory
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val categories: List<CategoryData> = emptyList(),
    val selectedCategory: CategoryData? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val createSuccess: Boolean = false
) {
    val categoryOptions: List<CategoryOption> get() = categories.toCategoryOptions()
    val tourCategories: List<TourCategory> get() = categories.toTourCategories()
}

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CategoryRepository(
        categoryApi = RetrofitClient.categoryApi,
        sessionManager = SessionManager(application)
    )

    var uiState by mutableStateOf(CategoriesUiState())
        private set

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getCategories()) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        categories = result.data.sortedBy { it.categoryId }
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun loadCategoryById(id: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getCategoryById(id)) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        selectedCategory = result.data
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun createCategory(name: String) {
        if (name.isBlank()) {
            uiState = uiState.copy(errorMessage = "Category name is required")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null,
                createSuccess = false
            )
            when (val result = repository.createCategory(name)) {
                is AuthResult.Success -> {
                    val updated = (uiState.categories.filter { it.categoryId != result.data.categoryId } + result.data)
                        .sortedBy { it.categoryId }
                    uiState = uiState.copy(
                        isSaving = false,
                        categories = updated,
                        successMessage = result.message,
                        createSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun clearCreateSuccess() {
        uiState = uiState.copy(createSuccess = false)
    }

    fun clearMessages() {
        uiState = uiState.copy(errorMessage = null, successMessage = null)
    }
}

class CategoriesViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
