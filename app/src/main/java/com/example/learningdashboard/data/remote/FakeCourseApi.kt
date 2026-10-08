package com.example.learningdashboard.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Mock API backed by assets/courses.json. It simulates latency and throws IOException when the
 * device has no connectivity, so airplane mode really exercises the offline path.
 */
class FakeCourseApi(private val context: Context) : CourseApi {

    enum class Mode { NORMAL, EMPTY, FAIL }

    /** Flip to EMPTY / FAIL to demo the empty and error states. */
    @Volatile var mode: Mode = Mode.NORMAL

    override suspend fun getCourses(): List<CourseDto> {
        simulateNetwork()
        return when (mode) {
            Mode.EMPTY -> emptyList()
            Mode.FAIL -> throw IOException("Server error (500)")
            Mode.NORMAL -> readCourses()
        }
    }

    override suspend fun getLessons(courseId: Long): List<LessonDto> {
        simulateNetwork()
        val course = readCourses().first { it.id == courseId }
        val done = (course.progress * course.lessons / 100.0).roundToInt()
        return (0 until course.lessons).map { i ->
            val title = SAMPLE_TITLES.getOrNull(i) ?: "Lesson ${i + 1}"
            LessonDto(title, completed = i < done)
        }
    }

    private suspend fun simulateNetwork() {
        delay(1000)
        if (!isOnline()) throw IOException("No internet connection")
    }

    private suspend fun readCourses(): List<CourseDto> = withContext(Dispatchers.IO) {
        val json = context.assets.open("courses.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        List(arr.length()) { i ->
            arr.getJSONObject(i).let {
                CourseDto(it.getLong("id"), it.getString("title"), it.getString("instructor"),
                    it.getInt("progress"), it.getInt("lessons"))
            }
        }
    }

    private fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private companion object {
        val SAMPLE_TITLES = listOf("Introduction", "Variables & Data Types", "Functions", "OOP")
    }
}
