package com.example.learningdashboard

import android.app.Application
import androidx.room.Room
import com.example.learningdashboard.data.local.AppDatabase
import com.example.learningdashboard.data.remote.FakeCourseApi
import com.example.learningdashboard.data.repository.CourseRepositoryImpl
import com.example.learningdashboard.data.repository.FakeAuthRepository
import com.example.learningdashboard.domain.AuthRepository
import com.example.learningdashboard.domain.CourseRepository

/** Manual DI container. In a larger app this would be Hilt modules. */
class AppContainer(app: Application) {
    private val db = Room.databaseBuilder(app, AppDatabase::class.java, "learning.db").build()
    val authRepository: AuthRepository = FakeAuthRepository()
    val courseRepository: CourseRepository = CourseRepositoryImpl(FakeCourseApi(app), db)
}

class LearningApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
