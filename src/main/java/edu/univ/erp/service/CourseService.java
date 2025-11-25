package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.CourseStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.SectionTime;

import java.util.List;

public class CourseService {
    private final CourseStore courseStore;
    private final SectionStore sectionStore;

    public CourseService() {
        this.courseStore = new CourseStore();
        this.sectionStore = new SectionStore();
    }

    

    public List<Course> getAllCourses() {
        return courseStore.findAll();
    }

    public String createCourse(String code, String title, int credits) {
        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        if (credits <= 0)
            return "Credits must be positive.";

        Course course = new Course();
        course.setCode(code);
        course.setTitle(title);
        course.setCredits(credits);

        return courseStore.create(course)
                ? null 
                : "Failed to create course.";
    }

    

    public List<Section> getAllSections() {
        return sectionStore.findAll();
    }

    
    public String createSection(int courseId,
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
            return "Section must have at least one scheduled time.";

        Section section = new Section();
        section.setCourseId(courseId);
        section.setInstructorId(instructorId);
        section.setRoom(room);
        section.setCapacity(capacity);
        section.setSemester(semester);
        section.setYear(year);
        section.setTimes(times);                      

        return sectionStore.create(section)
                ? null
                : "Failed to create section.";
    }

   
    public String updateSection(Section section) {

        if (!AccessControl.canAccessAdminFeatures())
            return AccessControl.getAccessDeniedMessage();

        if (!AccessControl.canModify())
            return AccessControl.getMaintenanceDenialMessage();

        if (section.getCapacity() <= 0)
            return "Capacity must be positive.";

        if (section.getTimes() == null || section.getTimes().isEmpty())
            return "Section must have at least one scheduled time.";

        return sectionStore.update(section)
                ? null
                : "Failed to update section.";
    }
}
