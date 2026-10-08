package com.example.learningdashboard.domain

data class Course(
    val id: Long,
    val title: String,
    val instructor: String,
    val totalLessons: Int,
    val completedLessons: Int,
) {
    val progress: Int get() = ProgressCalculator.percent(completedLessons, totalLessons)
}

data class Lesson(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
)
