package com.example.learningdashboard.data.remote

data class CourseDto(
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessons: Int,
)

data class LessonDto(val title: String, val completed: Boolean)

/** Swap FakeCourseApi for a Retrofit implementation of this interface in production. */
interface CourseApi {
    suspend fun getCourses(): List<CourseDto>
    suspend fun getLessons(courseId: Long): List<LessonDto>
}
