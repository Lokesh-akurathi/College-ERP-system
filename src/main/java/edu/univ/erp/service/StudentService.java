package edu.univ.erp.service;

import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.StudentStore;
import edu.univ.erp.domain.Student;

public class StudentService {
    private final StudentStore studentStore = new StudentStore();
    private final AuthStore authStore = new AuthStore();

    public Student getStudentById(int userId) {
        Student student = studentStore.findById(userId);
        if (student != null) {
            student.setUsername(authStore.findUsernameByUserId(userId));
        }
        return student;
    }
}
