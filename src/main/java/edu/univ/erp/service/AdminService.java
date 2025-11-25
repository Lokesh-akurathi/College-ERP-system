
package edu.univ.erp.service;

import java.util.List;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.CourseStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.SectionTime;


public class AdminService {

    private final CourseStore courseStore = new CourseStore();
    private final SectionStore sectionStore = new SectionStore();

 

    public String updateCourse(int courseId, String newCode, String newTitle, int newCredits) {

        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        boolean ok = courseStore.updateCourse(courseId, newCode, newTitle, newCredits);

        return ok ? "Course updated successfully!" : "Failed to update course.";
    }

    public String deleteCourse(int courseId) {

        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        return courseStore.deleteCourse(courseId)
                ? "Course deleted successfully."
                : "Failed to delete course.";
    }

   
    public String updateSection(int sectionId,
                                int courseId,
                                int instructorId,
                                List<SectionTime> times,
                                String room,
                                int capacity,
                                String semester,
                                int year) {

        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        if (capacity <= 0)
            return "Capacity must be positive.";

        if (times == null || times.isEmpty())
            return "You must specify at least one scheduled time.";

        Section section = new Section();
        section.setSectionId(sectionId);
        section.setCourseId(courseId);
        section.setInstructorId(instructorId);
        section.setRoom(room);
        section.setCapacity(capacity);
        section.setSemester(semester);
        section.setYear(year);
        section.setTimes(times);   

        boolean ok = sectionStore.update(section);

        return ok ? "Section updated successfully." : "Failed to update section.";
    }

   
    public String deleteSection(int sectionId) {

        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        if (!sectionStore.canDeleteSection(sectionId))
            return "Cannot delete: students are enrolled in this section.";

        boolean ok = sectionStore.deleteSection(sectionId);

        return ok ? "Section deleted successfully." : "Failed to delete section.";
    }
}
