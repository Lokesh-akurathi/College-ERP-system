package edu.univ.erp.service;

import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.domain.Instructor;

/**
 * Service layer that merges data from both Auth DB and ERP DB for instructors.
 */
public class InstructorService {
    private final InstructorStore instructorStore = new InstructorStore();
    private final AuthStore authStore = new AuthStore();

    /**
     * Fetch instructor details from ERP DB and username from Auth DB.
     */
    public Instructor getInstructorById(int userId) {
        Instructor instructor = instructorStore.findById(userId);
        if (instructor != null) {
            // Add username fetched from Auth DB
            instructor.setUsername(authStore.findUsernameByUserId(userId));
        }
        return instructor;
    }
}
