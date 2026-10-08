package com.example.learningdashboard.ui.dashboard

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.learningdashboard.LearningApp
import com.example.learningdashboard.domain.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onCourseClick: (Long) -> Unit) {
    val app = LocalContext.current.applicationContext as LearningApp
    val vm: DashboardViewModel = viewModel(factory = viewModelFactory {
        initializer { DashboardViewModel(app.container.courseRepository) }
    })
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("My Courses") }) }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                DashboardState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                DashboardState.Empty -> CenteredMessage("No courses yet", "Check back later.", "Refresh", vm::refresh)
                is DashboardState.Error -> CenteredMessage("Couldn't load courses", s.message, "Retry", vm::refresh)
                is DashboardState.Success -> Column {
                    s.banner?.let {
                        Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                            Text(it, Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(s.courses, key = { it.id }) { CourseCard(it, onCourseClick) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(course: Course, onClick: (Long) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(course.title, style = MaterialTheme.typography.titleMedium)
            Text("by ${course.instructor}", style = MaterialTheme.typography.bodyMedium)
            Text("${course.totalLessons} lessons", style = MaterialTheme.typography.bodySmall)
            LinearProgressIndicator(progress = { course.progress / 100f }, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("${course.progress}% complete", Modifier.weight(1f))
                Button(onClick = { onClick(course.id) }) { Text("Continue") }
            }
        }
    }
}

@Composable
private fun BoxScope.CenteredMessage(title: String, body: String, action: String, onAction: () -> Unit) {
    Column(
        Modifier.align(Alignment.Center).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(body, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = onAction) { Text(action) }
    }
}
