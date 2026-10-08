package com.example.learningdashboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learningdashboard.ui.dashboard.DashboardScreen
import com.example.learningdashboard.ui.details.CourseDetailScreen
import com.example.learningdashboard.ui.login.LoginScreen

private object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val DETAIL = "course/{courseId}"
    fun detail(id: Long) = "course/$id"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoggedIn = {
                nav.navigate(Routes.DASHBOARD) { popUpTo(Routes.LOGIN) { inclusive = true } }
            })
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(onCourseClick = { nav.navigate(Routes.detail(it)) })
        }
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("courseId") { type = NavType.LongType }),
        ) {
            CourseDetailScreen(onBack = { nav.popBackStack() })
        }
    }
}
