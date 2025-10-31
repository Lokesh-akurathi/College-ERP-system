package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.GradeStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;

import java.util.List;

public class GradeService {
    private final GradeStore gradeStore;
    private final EnrollmentStore enrollmentStore;
    private final SectionStore sectionStore;
    private final SessionManager sessionManager;

    public static final double QUIZ_WEIGHT = 0.20;
    public static final double MIDTERM_WEIGHT = 0.30;
    public static final double ENDSEM_WEIGHT = 0.50;

    public GradeService() {
        this.gradeStore = new GradeStore();
        this.enrollmentStore = new EnrollmentStore();
        this.sectionStore = new SectionStore();
        this.sessionManager = SessionManager.getInstance();
    }

    public String enterScore(int enrollmentId, String component, double score) {
        if (!AccessControl.canAccessInstructorFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if (score < 0 || score > 100) {
            return "Score must be between 0 and 100.";
        }

        Enrollment enrollment = enrollmentStore.findById(enrollmentId);
        if (enrollment == null) {
            return "Enrollment not found.";
        }

        if (!canInstructorAccessSection(enrollment.getSectionId())) {
            return "You can only enter grades for your own sections.";
        }

        if (gradeStore.updateOrCreate(enrollmentId, component, score)) {
            return null;
        } else {
            return "Failed to save score.";
        }
    }

    public String computeFinalGrade(int enrollmentId) {
        if (!AccessControl.canAccessInstructorFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        Enrollment enrollment = enrollmentStore.findById(enrollmentId);
        if (enrollment == null) {
            return "Enrollment not found.";
        }

        if (!canInstructorAccessSection(enrollment.getSectionId())) {
            return "You can only compute grades for your own sections.";
        }

        List<Grade> grades = gradeStore.findByEnrollment(enrollmentId);
        Double quizScore = null;
        Double midtermScore = null;
        Double endsemScore = null;

        for (Grade grade : grades) {
            if ("QUIZ".equals(grade.getComponent())) {
                quizScore = grade.getScore();
            } else if ("MIDTERM".equals(grade.getComponent())) {
                midtermScore = grade.getScore();
            } else if ("ENDSEM".equals(grade.getComponent())) {
                endsemScore = grade.getScore();
            }
        }

        if (quizScore == null || midtermScore == null || endsemScore == null) {
            return "All components (QUIZ, MIDTERM, ENDSEM) must have scores before computing final grade.";
        }

        double finalScore = (quizScore * QUIZ_WEIGHT) + (midtermScore * MIDTERM_WEIGHT) + (endsemScore * ENDSEM_WEIGHT);
        String letterGrade = computeLetterGrade(finalScore);

        Grade finalGrade = gradeStore.findByEnrollmentAndComponent(enrollmentId, "FINAL");
        if (finalGrade == null) {
            finalGrade = new Grade();
            finalGrade.setEnrollmentId(enrollmentId);
            finalGrade.setComponent("FINAL");
        }
        finalGrade.setScore(finalScore);
        finalGrade.setFinalGrade(letterGrade);

        if (finalGrade.getGradeId() == 0) {
            if (gradeStore.create(finalGrade)) {
                return null;
            } else {
                return "Failed to save final grade.";
            }
        } else {
            if (gradeStore.update(finalGrade)) {
                return null;
            } else {
                return "Failed to update final grade.";
            }
        }
    }

    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        return gradeStore.findByEnrollment(enrollmentId);
    }

    private boolean canInstructorAccessSection(int sectionId) {
        if (AccessControl.isAdmin()) {
            return true;
        }

        Section section = sectionStore.findById(sectionId);
        return section != null && section.getInstructorId() == sessionManager.getCurrentUserId();
    }

    private String computeLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}
