package com.example.learningdashboard.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.Course
import com.example.learningdashboard.domain.CourseRepository
import com.example.learningdashboard.domain.Lesson
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val course: Course? = null,
    val lessons: List<Lesson> = emptyList(),
)

class CourseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: CourseRepository,
) : ViewModel() {

    private val courseId: Long = checkNotNull(savedStateHandle["courseId"])

    val state: StateFlow<DetailUiState> =
        combine(repo.course(courseId), repo.lessons(courseId)) { c, l -> DetailUiState(c, l) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())

    fun markCompleted(lessonId: Long) {
        viewModelScope.launch { repo.markLessonCompleted(lessonId) }
    }
}
