package com.example.final_project.domain.model

import com.example.final_project.R

data class TourCategory(
    val id: String,
    val name: String,
    val imageRes: Int,
    val matchTags: List<String> = emptyList()
)

fun tourCountForCategory(category: TourCategory, destinations: List<Destination>): Int {
    if (category.matchTags.isEmpty()) {
        return destinations.count { it.category.equals(category.name, ignoreCase = true) }
    }
    return destinations.count { destination ->
        category.matchTags.any { tag ->
            destination.category.contains(tag, ignoreCase = true)
        }
    }
}

val defaultTourCategories = listOf(
    TourCategory(
        id = "1",
        name = "Temples & Historical Sites",
        imageRes = R.drawable.angkorwat,
        matchTags = listOf("Temple", "Historical")
    ),
    TourCategory(
        id = "2",
        name = "Nature & Adventure",
        imageRes = R.drawable.kohrong,
        matchTags = listOf("Nature", "Adventure")
    ),
    TourCategory(
        id = "3",
        name = "Culture & Heritage",
        imageRes = R.drawable.royalpalace,
        matchTags = listOf("Culture", "Heritage", "Historical")
    ),
    TourCategory(
        id = "4",
        name = "Water Activities",
        imageRes = R.drawable.sihanoukville,
        matchTags = listOf("Beach", "Water")
    )
)
