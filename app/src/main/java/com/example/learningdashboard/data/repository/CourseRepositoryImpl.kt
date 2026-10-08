package com.example.learningdashboard.data.repository

import androidx.room.withTransaction
import com.example.learningdashboard.data.local.AppDatabase
import com.example.learningdashboard.data.local.CourseEntity
import com.example.learningdashboard.data.local.CourseWithProgress
import com.example.learningdashboard.data.local.LessonEntity
import com.example.learningdashboard.data.remote.CourseApi
import com.example.learningdashboard.domain.Course
import com.example.learningdashboard.domain.CourseRepository
import com.example.learningdashboard.domain.Lesson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepositoryImpl(
    private val api: CourseApi,
    private val db: AppDatabase,
) : CourseRepository {

    private val dao = db.courseDao()

    override val courses: Flow<List<Course>> = dao.observeCourses().map { list -> list.map { it.toDomain() } }

    override fun course(id: Long): Flow<Course?> = dao.observeCourse(id).map { it?.toDomain() }

    override fun lessons(courseId: Long): Flow<List<Lesson>> =
        dao.observeLessons(courseId).map { list -> list.map { Lesson(it.id, it.title, it.completed) } }

    override suspend fun refresh(): Result<Unit> = try {
        val courses = api.getCourses()
        // Only fetch lessons for courses we don't have yet, so local completions are never overwritten.
        val missing = courses.filter { dao.lessonCount(it.id) == 0 }
        val newLessons = missing.associate { it.id to api.getLessons(it.id) }

        db.withTransaction {
            dao.upsertCourses(courses.map { CourseEntity(it.id, it.title, it.instructor, it.lessons) })
            newLessons.forEach { (courseId, lessons) ->
                dao.insertLessons(lessons.mapIndexed { i, l ->
                    LessonEntity(courseId = courseId, position = i, title = l.title, completed = l.completed)
                })
            }
        }
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markLessonCompleted(lessonId: Long) = dao.markCompleted(lessonId)

    private fun CourseWithProgress.toDomain() = Course(id, title, instructor, lessonCount, completedCount)
}
