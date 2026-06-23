package com.example.final_project.domain.model

import com.example.final_project.R

data class CategoryOption(
    val id: Long,
    val name: String
)

data class Destination(
    val id: String,
    val name: String,
    val province: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val imageRes: Int = R.drawable.angkorwat,
    val imageUrl: String? = null,
    val description: String = "",
    val categoryId: Long? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val durationHours: Int = 8,
    val maxPeople: Int = 20,
    val isActive: Boolean = true,
    val isFavorite: Boolean = false
)

val mockDestinations = listOf(
    Destination("1", "Angkor Wat", "Siem Reap", "Temple", 37.0, 4.9, R.drawable.angkorwat, "The largest religious monument in the world, built in the 12th century"),
    Destination("2", "Bayon Temple", "Siem Reap", "Temple", 37.0, 4.8, R.drawable.bayon, "Famous for its smiling stone faces and intricate bas-reliefs"),
    Destination("3", "Sihanoukville Beach", "Sihanoukville", "Beach", 0.0, 4.6, R.drawable.sihanoukville, "Beautiful white sand beaches and crystal clear waters"),
    Destination("4", "Koh Rong", "Sihanoukville", "Beach", 25.0, 4.7, R.drawable.kohrong, "Paradise island with bioluminescent plankton and pristine beaches"),
    Destination("5", "Killing Fields", "Phnom Penh", "Historical", 6.0, 4.5, R.drawable.killingfield, "Historical memorial site and museum of the Khmer Rouge regime"),
    Destination("6", "Royal Palace", "Phnom Penh", "Historical", 10.0, 4.7, R.drawable.royalpalace, "Official residence of the King of Cambodia with beautiful architecture")
)
