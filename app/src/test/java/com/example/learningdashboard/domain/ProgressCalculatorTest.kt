package com.example.learningdashboard.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test fun `65 percent when 13 of 20 lessons are done`() =
        assertEquals(65, ProgressCalculator.percent(13, 20))

    @Test fun `completing one more lesson raises progress`() {
        val before = Course(1, "Python", "John", totalLessons = 20, completedLessons = 13)
        val after = before.copy(completedLessons = 14)
        assertEquals(65, before.progress)
        assertEquals(70, after.progress)
    }

    @Test fun `rounds to nearest percent`() =
        assertEquals(38, ProgressCalculator.percent(6, 16)) // 37.5 -> 38

    @Test fun `course with no lessons is 0 percent and does not divide by zero`() =
        assertEquals(0, ProgressCalculator.percent(0, 0))

    @Test fun `out of range input is clamped`() {
        assertEquals(100, ProgressCalculator.percent(25, 20))
        assertEquals(0, ProgressCalculator.percent(-3, 20))
    }
}
