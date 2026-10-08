package com.example.learningdashboard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
)

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(CourseEntity::class, ["id"], ["courseId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("courseId")],
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val position: Int,
    val title: String,
    val completed: Boolean,
)

/** Query projection: course + number of completed lessons. */
data class CourseWithProgress(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedCount: Int,
)
