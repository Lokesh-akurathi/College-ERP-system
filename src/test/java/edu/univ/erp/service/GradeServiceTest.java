package edu.univ.erp.service;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.GradeStore;
import edu.univ.erp.data.GradingStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GradeServiceTest {

    @Mock private GradeStore gradeStore;
    @Mock private EnrollmentStore enrollmentStore;
    @Mock private SectionStore sectionStore;
    @Mock private GradingStore gradingStore;

    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SessionManager.getInstance().logout();
        gradeService = new GradeService(gradeStore, enrollmentStore, sectionStore, gradingStore, SessionManager.getInstance());
    }

    @Test
    void getLetterGrade_scoreBoundaries_returnsCorrectGrades() {
        assertThat(gradeService.getLetterGrade(95.0)).isEqualTo("A");
        assertThat(gradeService.getLetterGrade(87.0)).isEqualTo("A-");
        assertThat(gradeService.getLetterGrade(82.0)).isEqualTo("B");
        assertThat(gradeService.getLetterGrade(77.0)).isEqualTo("B-");
        assertThat(gradeService.getLetterGrade(72.0)).isEqualTo("C");
        assertThat(gradeService.getLetterGrade(67.0)).isEqualTo("C-");
        assertThat(gradeService.getLetterGrade(62.0)).isEqualTo("D");
        assertThat(gradeService.getLetterGrade(55.0)).isEqualTo("F");
    }

    @Test
    void enterScore_accessDenied_returnsDeniedMessage() {
        // No session / student session
        String result = gradeService.enterScore(10, "MIDTERM", 85.0);
        assertThat(result).isEqualTo("Access Denied. You do not have permission for this action.");
    }

    @Test
    void enterScore_invalidScoreRange_returnsErrorMessage() {
        User instructor = new User(1, "inst1", "INSTRUCTOR", "ACTIVE");
        SessionManager.getInstance().setCurrentUser(instructor);

        String resultLow = gradeService.enterScore(10, "MIDTERM", -5.0);
        String resultHigh = gradeService.enterScore(10, "MIDTERM", 105.0);

        assertThat(resultLow).isEqualTo("Score must be between 0 and 100.");
        assertThat(resultHigh).isEqualTo("Score must be between 0 and 100.");
    }

    @Test
    void computeFinalGrade_missingComponents_returnsMissingErrorMessage() {
        User instructor = new User(1, "inst1", "INSTRUCTOR", "ACTIVE");
        SessionManager.getInstance().setCurrentUser(instructor);

        Enrollment enrollment = new Enrollment(10, 1, 101, "ACTIVE", "2023CS01");
        when(enrollmentStore.findById(10)).thenReturn(enrollment);

        edu.univ.erp.domain.Section section = new edu.univ.erp.domain.Section();
        section.setSectionId(101);
        section.setInstructorId(1);
        when(sectionStore.findById(101)).thenReturn(section);

        GradingCriteria criteria = new GradingCriteria(101, "QUIZ", 100.0, 1);
        when(gradingStore.findBySectionId(101)).thenReturn(List.of(criteria));
        when(gradeStore.findByEnrollment(10)).thenReturn(Collections.emptyList());

        String result = gradeService.computeFinalGrade(10);
        assertThat(result).contains("Missing scores for components: QUIZ");
    }

    @Test
    void computeFinalGrade_invalidTotalWeight_returnsWeightMismatchError() {
        User instructor = new User(1, "inst1", "INSTRUCTOR", "ACTIVE");
        SessionManager.getInstance().setCurrentUser(instructor);

        Enrollment enrollment = new Enrollment(10, 1, 101, "ACTIVE", "2023CS01");
        when(enrollmentStore.findById(10)).thenReturn(enrollment);

        edu.univ.erp.domain.Section section = new edu.univ.erp.domain.Section();
        section.setSectionId(101);
        section.setInstructorId(1);
        when(sectionStore.findById(101)).thenReturn(section);

        GradingCriteria criteria = new GradingCriteria(101, "QUIZ", 40.0, 1);
        when(gradingStore.findBySectionId(101)).thenReturn(List.of(criteria));
        
        edu.univ.erp.domain.Grade grade = new edu.univ.erp.domain.Grade(1, 10, "QUIZ", 80.0);
        when(gradeStore.findByEnrollment(10)).thenReturn(List.of(grade));

        String result = gradeService.computeFinalGrade(10);
        assertThat(result).contains("Total criteria weight is invalid: 40.00%. Must sum to 100.00%");
    }
}
