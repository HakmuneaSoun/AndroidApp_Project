package com.example.final_project.data.mapper

import com.example.final_project.data.remote.ApiConstants
import com.example.final_project.data.remote.dto.FavoriteTourData
import com.example.final_project.domain.model.Destination

fun FavoriteTourData.toDestination(): Destination = Destination(
    id = tourId.toString(),
    name = title,
    province = location,
    category = categoryName,
    price = price,
    rating = avgRating,
    imageUrl = ApiConstants.resolveMediaUrl(imageCover),
    description = description,
    categoryId = categoryId,
    durationHours = durationHours,
    maxPeople = maxPeople,
    isActive = isActive,
    isFavorite = true
)

fun List<FavoriteTourData>.toFavoriteDestinations(): List<Destination> = map { it.toDestination() }
