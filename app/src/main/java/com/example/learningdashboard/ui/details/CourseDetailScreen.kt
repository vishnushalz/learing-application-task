package com.example.learningdashboard.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.learningdashboard.LearningApp
import com.example.learningdashboard.domain.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as LearningApp
    val vm: CourseDetailViewModel = viewModel(factory = viewModelFactory {
        initializer { CourseDetailViewModel(createSavedStateHandle(), app.container.courseRepository) }
    })
    val state by vm.state.collectAsStateWithLifecycle()
    val course = state.course

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(course?.title ?: "Course") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
        )
    }) { padding ->
        if (course == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Progress: ${course.progress}%", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(progress = { course.progress / 100f }, modifier = Modifier.fillMaxWidth())
                    Text("${course.completedLessons} of ${course.totalLessons} lessons completed")
                    Spacer(Modifier.height(8.dp))
                }
            }
            items(state.lessons, key = { it.id }) { LessonRow(it) { vm.markCompleted(it.id) } }
        }
    }
}

@Composable
private fun LessonRow(lesson: Lesson, onComplete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(lesson.title, style = MaterialTheme.typography.bodyLarge)
                Text(if (lesson.isCompleted) "✓ Completed" else "○ Pending", style = MaterialTheme.typography.bodySmall)
            }
            if (!lesson.isCompleted) {
                OutlinedButton(onClick = onComplete) { Text("Mark complete") }
            }
        }
    }
}
