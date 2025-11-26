package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;

import java.util.List;

public class EnrollmentService {
    private final EnrollmentStore enrollmentStore;
    private final SectionStore sectionStore;
    private final SessionManager sessionManager;

    public EnrollmentService() {
        this.enrollmentStore = new EnrollmentStore();
        this.sectionStore = new SectionStore();
        this.sessionManager = SessionManager.getInstance();
    }

    public String registerForSection(int sectionId) {
        if (!AccessControl.canAccessStudentFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if(!AccessControl.isAddDropPeriod()){
            return AccessControl.getAddDropDeniedMessage();
        }

        int studentId = sessionManager.getCurrentUserId();

        if (enrollmentStore.exists(studentId, sectionId)) {
            return "You are already registered for this section.";
        }

        Section section = sectionStore.findById(sectionId);
        if (section == null) {
            return "Section not found.";
        }

        if (section.isFull()) {
            return "Section is full. No seats available.";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setSectionId(sectionId);
        enrollment.setStatus("ACTIVE");

        if (enrollmentStore.create(enrollment)) {
            return null;
        } else {
            return "Failed to register for section.";
        }
    }

    public String dropSection(int enrollmentId) {
        if (!AccessControl.canAccessStudentFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if(!AccessControl.isAddDropPeriod()){
            return AccessControl.getAddDropDeniedMessage();
        }

        int studentId = sessionManager.getCurrentUserId();
        Enrollment enrollment = enrollmentStore.findById(enrollmentId);

        if (enrollment == null) {
            return "Enrollment not found.";
        }

        if (enrollment.getStudentId() != studentId && !AccessControl.isAdmin()) {
            return "You can only drop your own enrollments.";
        }

        if (enrollmentStore.drop(enrollmentId)) {
            return null;
        } else {
            return "Failed to drop section.";
        }
    }

    public List<Enrollment> getMyEnrollments() {
        int studentId = sessionManager.getCurrentUserId();
        return enrollmentStore.findByStudent(studentId);
    }

    public List<Enrollment> getEnrollmentsBySection(int sectionId) {
        return enrollmentStore.findBySection(sectionId);
    }
}
