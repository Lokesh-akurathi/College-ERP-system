package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.CourseStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

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

    public List<Section> getAllSections() {
        return sectionStore.findAll();
    }

    public String createCourse(String code, String title, int credits) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if (credits <= 0) {
            return "Credits must be positive.";
        }

        Course course = new Course();
        course.setCode(code);
        course.setTitle(title);
        course.setCredits(credits);

        if (courseStore.create(course)) {
            return null;
        } else {
            return "Failed to create course.";
        }
    }

    public String createSection(int courseId, int instructorId, String dayTime, String room, 
                               int capacity, String semester, int year) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if (capacity <= 0) {
            return "Capacity must be positive.";
        }

        Section section = new Section();
        section.setCourseId(courseId);
        section.setInstructorId(instructorId);
        section.setDayTime(dayTime);
        section.setRoom(room);
        section.setCapacity(capacity);
        section.setSemester(semester);
        section.setYear(year);

        if (sectionStore.create(section)) {
            return null;
        } else {
            return "Failed to create section.";
        }
    }

    public String updateSection(Section section) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if (section.getCapacity() <= 0) {
            return "Capacity must be positive.";
        }

        if (sectionStore.update(section)) {
            return null;
        } else {
            return "Failed to update section.";
        }
    }
}
