package edu.univ.erp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.CourseStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Section;
import edu.univ.erp.util.DatabaseConfig;

/**
 * Handles admin-level operations on courses and sections.
 * Enforces access and maintenance checks before making ERP DB changes.
 */
public class AdminService {

    private final CourseStore courseStore = new CourseStore();
    private final SectionStore sectionStore = new SectionStore();

    /** --------------------- COURSE MANAGEMENT --------------------- */

    // public String updateCourse(int courseId, String title, int credits) {
    //     if (!AccessControl.canAccessAdminFeatures())
    //         return AccessControl.getAccessDeniedMessage();

    //     if (!AccessControl.canModify())
    //         return AccessControl.getMaintenanceDenialMessage();

    //     boolean success = courseStore.updateCourse(courseId, title, credits);
    //     return success ? "Course updated successfully." : "Failed to update course.";
    // }
    public String updateCourse(int courseId, String newCode, String newTitle, int newCredits) {
    if (!AccessControl.canAccessAdminFeatures()) {
        return AccessControl.getAccessDeniedMessage();
    }
    if (!AccessControl.canModify()) {
        return AccessControl.getMaintenanceDenialMessage();
    }

    String sql = "UPDATE courses SET code = ?, title = ?, credits = ? WHERE course_id = ?";

    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, newCode);
        pstmt.setString(2, newTitle);
        pstmt.setInt(3, newCredits);
        pstmt.setInt(4, courseId);

        int rows = pstmt.executeUpdate();
        return (rows > 0) ? "Course updated successfully!" : "No changes made.";
    } catch (SQLException e) {
        e.printStackTrace();
        return "Error updating course: " + e.getMessage();
    }
}

    public String deleteCourse(int courseId) {
        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        boolean success = courseStore.deleteCourse(courseId);
        return success ? "Course deleted successfully." : "Failed to delete course.";
    }

    /** --------------------- SECTION MANAGEMENT --------------------- */

    public String updateSection(int sectionId, int courseId, int instructorId,
                            String dayTime, String room, int capacity,
                            String semester, int year) {
    if (!AccessControl.canAccessAdminFeatures())
        return AccessControl.getAccessDeniedMessage();

    if (!AccessControl.canModify())
        return AccessControl.getMaintenanceDenialMessage();

    Section section = new Section();
    section.setSectionId(sectionId);
    section.setCourseId(courseId);
    section.setInstructorId(instructorId);
    section.setDayTime(dayTime);
    section.setRoom(room);
    section.setCapacity(capacity);
    section.setSemester(semester);
    section.setYear(year);

    boolean success = sectionStore.update(section);
    return success ? "Section updated successfully." : "Failed to update section.";
}


    public String deleteSection(int sectionId) {
        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        // Optional safety check — only delete if no enrollments exist
        if (!sectionStore.canDeleteSection(sectionId)) {
            return "Cannot delete: students are enrolled in this section.";
        }

        boolean success = sectionStore.deleteSection(sectionId);
        return success ? "Section deleted successfully." : "Failed to delete section.";
    }
}
