package com.example.learningdashboard.domain

import kotlin.math.roundToInt

object ProgressCalculator {
    /** Returns 0..100. Safe for empty courses and out-of-range input. */
    fun percent(completed: Int, total: Int): Int {
        if (total <= 0) return 0
        return (completed.coerceIn(0, total) * 100.0 / total).roundToInt()
    }
}
