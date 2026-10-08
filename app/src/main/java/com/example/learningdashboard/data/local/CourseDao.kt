package com.example.learningdashboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query(
        """SELECT c.id, c.title, c.instructor, c.lessonCount,
            (SELECT COUNT(*) FROM lessons l WHERE l.courseId = c.id AND l.completed = 1) AS completedCount
           FROM courses c ORDER BY c.id"""
    )
    fun observeCourses(): Flow<List<CourseWithProgress>>

    @Query(
        """SELECT c.id, c.title, c.instructor, c.lessonCount,
            (SELECT COUNT(*) FROM lessons l WHERE l.courseId = c.id AND l.completed = 1) AS completedCount
           FROM courses c WHERE c.id = :id"""
    )
    fun observeCourse(id: Long): Flow<CourseWithProgress?>

    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY position")
    fun observeLessons(courseId: Long): Flow<List<LessonEntity>>

    @Upsert
    suspend fun upsertCourses(courses: List<CourseEntity>)

    @Insert
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("SELECT COUNT(*) FROM lessons WHERE courseId = :courseId")
    suspend fun lessonCount(courseId: Long): Int

    @Query("UPDATE lessons SET completed = 1 WHERE id = :lessonId")
    suspend fun markCompleted(lessonId: Long)
}
