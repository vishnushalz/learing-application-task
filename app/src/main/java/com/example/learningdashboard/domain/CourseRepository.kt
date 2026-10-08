package com.example.learningdashboard.domain

import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    /** Single source of truth: always emits from the local DB. */
    val courses: Flow<List<Course>>
    fun course(id: Long): Flow<Course?>
    fun lessons(courseId: Long): Flow<List<Lesson>>

    /** Fetch from remote and update the cache. Failure leaves cached data untouched. */
    suspend fun refresh(): Result<Unit>
    suspend fun markLessonCompleted(lessonId: Long)
}

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
}
