package com.example.learningdashboard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.Course
import com.example.learningdashboard.domain.CourseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DashboardState {
    data object Loading : DashboardState
    data object Empty : DashboardState
    data class Error(val message: String) : DashboardState
    data class Success(val courses: List<Course>, val banner: String? = null) : DashboardState
}

class DashboardViewModel(private val repo: CourseRepository) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)

    val state: StateFlow<DashboardState> =
        combine(repo.courses, isLoading, error) { courses, loading, err ->
            when {
                courses.isNotEmpty() -> DashboardState.Success(courses, banner = err?.let { "$it – showing saved courses" })
                loading -> DashboardState.Loading
                err != null -> DashboardState.Error(err)
                else -> DashboardState.Empty
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState.Loading)

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            repo.refresh().onFailure { error.value = it.toUserMessage() }
            isLoading.value = false
        }
    }

    private fun Throwable.toUserMessage() = when (this) {
        is IOException -> message ?: "Network error"
        else -> message ?: "Something went wrong"
    }
}
