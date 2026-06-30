package com.example.final_project.data.remote

object ApiConstants {

    // Public cloud server — works on any network; no PC or local.properties setup needed.
    const val BASE_URL = "https://tour-recomendation-backend.onrender.com/"

    fun resolveMediaUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return url
            .replace("http://localhost:8081", "https://tour-recomendation-backend.onrender.com")
            .replace("http://127.0.0.1:8081", "https://tour-recomendation-backend.onrender.com")
            .replace("http://10.0.2.2:8081", "https://tour-recomendation-backend.onrender.com")
            .replace("localhost", "tour-recomendation-backend.onrender.com")
            .replace("127.0.0.1", "tour-recomendation-backend.onrender.com")
    }

    fun connectionErrorMessage(): String =
        "Cannot reach server. Check your internet connection and try again in a moment."
}
