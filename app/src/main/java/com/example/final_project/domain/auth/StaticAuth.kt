package com.example.final_project.domain.auth

enum class UserRole {
    USER,
    ADMIN
}

object StaticAuth {
    const val USER_EMAIL = "user@gmail.com"
    const val USER_PASSWORD = "user123"
    const val ADMIN_EMAIL = "admin@gmail.com"
    const val ADMIN_PASSWORD = "admin123"

    fun authenticate(email: String, password: String): UserRole? {
        val normalizedEmail = email.trim().lowercase()
        return when {
            normalizedEmail == ADMIN_EMAIL.lowercase() && password == ADMIN_PASSWORD -> UserRole.ADMIN
            normalizedEmail == USER_EMAIL.lowercase() && password == USER_PASSWORD -> UserRole.USER
            else -> null
        }
    }
}
