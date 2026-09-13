package edu.univ.erp.service;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.SectionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EnrollmentServiceTest {

    @Mock private EnrollmentStore enrollmentStore;
    @Mock private SectionStore sectionStore;

    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        enrollmentService = new EnrollmentService(enrollmentStore, sectionStore, SessionManager.getInstance());
    }

    @Test
    void validateRollNumberForEnrollment_nonExistentEnrollment_returnsError() {
        when(enrollmentStore.findRollNumberByEnrollmentId(999)).thenReturn(null);

        String result = enrollmentService.validateRollNumberForEnrollment(999, "2023CS01");
        assertThat(result).isEqualTo("Enrollment ID does not exist.");
    }

    @Test
    void validateRollNumberForEnrollment_mismatchedRollNumber_returnsMismatchError() {
        when(enrollmentStore.findRollNumberByEnrollmentId(101)).thenReturn("2023CS01");

        String result = enrollmentService.validateRollNumberForEnrollment(101, "2023CS99");
        assertThat(result).isEqualTo("Roll number mismatch (Expected: 2023CS01).");
    }

    @Test
    void validateRollNumberForEnrollment_matchingRollNumber_returnsNull() {
        when(enrollmentStore.findRollNumberByEnrollmentId(101)).thenReturn("2023CS01");

        String result = enrollmentService.validateRollNumberForEnrollment(101, "2023CS01");
        assertThat(result).isNull();
    }
}
