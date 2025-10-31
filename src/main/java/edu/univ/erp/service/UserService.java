package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.data.StudentStore;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;

public class UserService {
    private final AuthStore authStore;
    private final StudentStore studentStore;
    private final InstructorStore instructorStore;

    public UserService() {
        this.authStore = new AuthStore();
        this.studentStore = new StudentStore();
        this.instructorStore = new InstructorStore();
    }

    public String createStudent(String username, String password, String rollNo, String program, int year) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        if (!authStore.createUser(username, "STUDENT", passwordHash)) {
            return "Failed to create user account.";
        }

        int userId = authStore.getUserIdByUsername(username);
        if (userId == -1) {
            return "Failed to retrieve user ID.";
        }

        Student student = new Student(userId, rollNo, program, year);
        if (studentStore.create(student)) {
            return null;
        } else {
            return "Failed to create student profile.";
        }
    }

    public String createInstructor(String username, String password, String department) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        if (!authStore.createUser(username, "INSTRUCTOR", passwordHash)) {
            return "Failed to create user account.";
        }

        int userId = authStore.getUserIdByUsername(username);
        if (userId == -1) {
            return "Failed to retrieve user ID.";
        }

        Instructor instructor = new Instructor(userId, department);
        if (instructorStore.create(instructor)) {
            return null;
        } else {
            return "Failed to create instructor profile.";
        }
    }

    public String createAdmin(String username, String password) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        if (authStore.createUser(username, "ADMIN", passwordHash)) {
            return null;
        } else {
            return "Failed to create admin account.";
        }
    }
}
