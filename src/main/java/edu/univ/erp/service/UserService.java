package edu.univ.erp.service;

import java.util.List;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.data.StudentStore;
import edu.univ.erp.data.UserStore;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.User;
import static edu.univ.erp.auth.hash.PasswordHasher.hashPassword;
public class UserService {
    private final AuthStore authStore;
    private final StudentStore studentStore;
    private final InstructorStore instructorStore;
    private final UserStore userStore;

    public UserService() {
        this.authStore = new AuthStore();
        this.studentStore = new StudentStore();
        this.instructorStore = new InstructorStore();
        this.userStore = new UserStore();
    }

    public String createStudent(String username, String password, String rollNo, String program, int year, String firstName, String lastName) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        if (!authStore.createUser(username, "STUDENT", passwordHash)) {
            return "Failed to create user account. User Name already exists.";
        }

        int userId = authStore.getUserIdByUsername(username);
        if (userId == -1) {
            return "Failed to retrieve user ID.";
        }

        Student student = new Student(userId, rollNo, program, year);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        if (studentStore.create(student)) {
            return null;
        } else {
            return "Failed to create student profile. Roll number already exists.";
        }
    }

    public String createInstructor(String username, String password, String department, String firstName, String lastName , String salutation) {
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
         instructor.setSalutation(salutation);
        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
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
    public List<User> getUsersByType(String userType) {
    
        return userStore.findAllByType(userType);
    }
    public boolean resetUserPassword(int userId, String newRawPassword) {
   
        String newHashedPassword = hashPassword(newRawPassword.trim()); 

        return userStore.updatePassword(userId, newHashedPassword);
    }
    public String changePassword(int userId, String oldPassword, String newPassword) {
       
        String currentHash = userStore.getPasswordHash(userId);
        
        if (currentHash == null) {
            return "User account not found.";
        }
        
      
        if (!PasswordHasher.verifyPassword(oldPassword, currentHash)) {
            return "Incorrect old password.";
        }
        
        
        String newHashedPassword = PasswordHasher.hashPassword(newPassword);
        
       
        if (userStore.updatePassword(userId, newHashedPassword)) {
            return null; 
        } else {
            return "Failed to update password due to a system error.";
        }
    }
    public boolean unlockUser(int userId) {
   
    return authStore.unlockUser(userId);
}
    
}
