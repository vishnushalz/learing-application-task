package com.example.learningdashboard.domain

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class InputValidatorTest {
    @Test fun `valid email passes`() = assertNull(InputValidator.validateEmail("a.b@site.com"))
    @Test fun `blank or malformed email fails`() {
        assertNotNull(InputValidator.validateEmail(""))
        assertNotNull(InputValidator.validateEmail("not-an-email"))
    }
    @Test fun `short password fails`() = assertNotNull(InputValidator.validatePassword("123"))
    @Test fun `six char password passes`() = assertNull(InputValidator.validatePassword("abcdef"))
}
