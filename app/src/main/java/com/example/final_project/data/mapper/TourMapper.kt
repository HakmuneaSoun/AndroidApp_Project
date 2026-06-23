package com.example.final_project.data.mapper

import com.example.final_project.R
import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.dto.TourData
import com.example.final_project.domain.model.CategoryOption
import com.example.final_project.domain.model.Destination

fun TourData.toDestination(): Destination = Destination(
    id = tourId.toString(),
    name = title,
    province = location,
    category = categoryName,
    price = price,
    rating = avgRating,
    imageUrl = ApiConstants.resolveMediaUrl(imageCover),
    description = description,
    categoryId = categoryId,
    latitude = latitude,
    longitude = longitude,
    durationHours = durationHours,
    maxPeople = maxPeople,
    isActive = isActive
)

fun List<TourData>.toDestinations(): List<Destination> = map { it.toDestination() }

fun extractCategoriesFromTours(tours: List<TourData>): List<CategoryOption> {
    val fromApi = tours
        .distinctBy { it.categoryId }
        .map { CategoryOption(id = it.categoryId, name = it.categoryName) }
        .sortedBy { it.id }
    return if (fromApi.isNotEmpty()) fromApi else defaultCategoryOptions()
}

fun defaultCategoryOptions(): List<CategoryOption> = listOf(
    CategoryOption(1, "Temple"),
    CategoryOption(2, "Historical"),
    CategoryOption(3, "Nature"),
    CategoryOption(4, "Beach"),
    CategoryOption(5, "Adventure")
)

fun imageResForCategory(category: String): Int = when {
    category.contains("temple", ignoreCase = true) -> R.drawable.angkorwat
    category.contains("beach", ignoreCase = true) -> R.drawable.sihanoukville
    category.contains("nature", ignoreCase = true) -> R.drawable.kohrong
    category.contains("historical", ignoreCase = true) -> R.drawable.royalpalace
    else -> R.drawable.angkorwat1
}
