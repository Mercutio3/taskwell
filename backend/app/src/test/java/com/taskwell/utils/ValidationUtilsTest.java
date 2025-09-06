package com.taskwell.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {
    @Test
    void isValidUsername_ValidUsername_ReturnsTrue() {
        assertTrue(ValidationUtils.isValidUsername("validusername"));
    }

    @Test
    void isValidUsername_InvalidUsernameWithSpaces_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidUsername("invalid username"));
    }

    @Test
    void isValidUsername_InvalidUsernameTooShort_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidUsername("no"));
    }

    @Test
    void isValidUsername_InvalidUsernameTooLong_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidUsername("thisusernameiswaytoolongtobevalidandshouldfailthetestcase"));
    }

    @Test
    void isValidUsername_NullUsername_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidUsername(null));
    }

    @Test
    void isValidUsername_EmptyUsername_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidUsername(""));
    }

    @Test
    void isValidEmail_ValidEmail_ReturnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("valid@example.com"));
    }

    @Test
    void isValidEmail_InvalidEmail_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
    }

    @Test
    void isValidEmail_NullEmail_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    void isValidEmail_EmptyEmail_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    @Test
    void isValidPassword_ValidPassword_ReturnsTrue() {
        assertTrue(ValidationUtils.isValidPassword("GoodPassword1!"));
    }

    @Test
    void isValidPassword_InvalidPasswordNoNumber_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("BadPassword!"));
    }

    @Test
    void isValidPassword_InvalidPasswordNoSpecial_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("BadPassword1"));
    }

    @Test
    void isValidPassword_InvalidPasswordNoUppercase_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("badpassword1!"));
    }

    @Test
    void isValidPassword_InvalidPasswordNoLowercase_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("BADPASSWORD1!"));
    }

    @Test
    void isValidPassword_NullPassword_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword(null));
    }

    @Test
    void isValidPassword_EmptyPassword_ReturnsFalse() {
        assertFalse(ValidationUtils.isValidPassword(""));
    }
}
