package com.example.learningdashboard.data.repository

import com.example.learningdashboard.domain.AuthRepository
import kotlinx.coroutines.delay

/** Mock login. Any valid email works, except wrong@test.com which simulates bad credentials. */
class FakeAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Result<String> {
        delay(1200)
        return if (email.trim().equals("wrong@test.com", ignoreCase = true)) {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        } else {
            Result.success("mock-token-${email.hashCode()}")
        }
    }
}
