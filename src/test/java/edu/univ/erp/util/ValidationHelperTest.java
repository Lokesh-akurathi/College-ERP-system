package edu.univ.erp.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ValidationHelperTest {

    @Test
    void isValidUsername_validInput_returnsTrue() {
        assertThat(ValidationHelper.isValidUsername("valid_user1")).isTrue();
        assertThat(ValidationHelper.isValidUsername("admin")).isTrue();
    }

    @Test
    void isValidUsername_invalidInput_returnsFalse() {
        assertThat(ValidationHelper.isValidUsername(null)).isFalse();
        assertThat(ValidationHelper.isValidUsername("ab")).isFalse(); // length < 3
        assertThat(ValidationHelper.isValidUsername("user@123")).isFalse(); // invalid char '@'
    }

    @Test
    void isValidPassword_boundaryChecks() {
        assertThat(ValidationHelper.isValidPassword(null)).isFalse();
        assertThat(ValidationHelper.isValidPassword("12345")).isFalse(); // length < 6
        assertThat(ValidationHelper.isValidPassword("123456")).isTrue();
    }

    @Test
    void validateCourseData_validAndInvalidInputs() {
        // Valid case
        assertThat(ValidationHelper.validateCourseData("CS101", "Intro to CS", 3)).isNull();

        // Invalid code
        assertThat(ValidationHelper.validateCourseData("", "Intro to CS", 3)).isEqualTo("Course code is required.");
        
        // Invalid title
        assertThat(ValidationHelper.validateCourseData("CS101", " ", 3)).isEqualTo("Course title is required.");

        // Invalid credits
        assertThat(ValidationHelper.validateCourseData("CS101", "Intro to CS", 0)).isEqualTo("Credits must be between 1 and 10.");
    }

    @Test
    void validateSectionData_roomAndCapacityValidation() {
        // Valid case
        assertThat(ValidationHelper.validateSectionData("Mon 10:00", "Room 101", 30)).isNull();

        // Missing room
        assertThat(ValidationHelper.validateSectionData("Mon 10:00", null, 30)).isEqualTo("Room is required.");

        // Invalid capacity
        assertThat(ValidationHelper.validateSectionData("Mon 10:00", "Room 101", 0)).isEqualTo("Capacity must be between 1 and 500.");
    }

    @Test
    void validateUserData_usernameAndPasswordValidation() {
        // Valid case
        assertThat(ValidationHelper.validateUserData("student1", "password123")).isNull();

        // Invalid username
        assertThat(ValidationHelper.validateUserData("st", "password123"))
                .contains("Username must be at least 3 characters");

        // Invalid password
        assertThat(ValidationHelper.validateUserData("student1", "123"))
                .isEqualTo("Password must be at least 6 characters.");
    }
}
