package com.example.final_project.data.mapper

import com.example.final_project.data.remote.dto.CategoryData
import com.example.final_project.domain.model.CategoryOption
import com.example.final_project.domain.model.Destination
import com.example.final_project.domain.model.TourCategory

fun CategoryData.toCategoryOption() = CategoryOption(
    id = categoryId,
    name = name
)

fun CategoryData.toTourCategory() = TourCategory(
    id = categoryId.toString(),
    name = name,
    imageRes = imageResForCategory(name)
)

fun List<CategoryData>.toCategoryOptions(): List<CategoryOption> = map { it.toCategoryOption() }

fun List<CategoryData>.toTourCategories(): List<TourCategory> = map { it.toTourCategory() }

fun tourCountForCategory(categoryId: Long, categoryName: String, destinations: List<Destination>): Int {
    return destinations.count { destination ->
        destination.categoryId == categoryId ||
            destination.category.equals(categoryName, ignoreCase = true)
    }
}
