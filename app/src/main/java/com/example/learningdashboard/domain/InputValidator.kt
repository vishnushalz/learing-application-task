package com.example.learningdashboard.domain

object InputValidator {
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /** @return error message or null if valid */
    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required"
        !EMAIL.matches(email.trim()) -> "Enter a valid email"
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}
