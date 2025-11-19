// package edu.univ.erp.service;

// import edu.univ.erp.auth.store.AuthStore;
// import edu.univ.erp.data.InstructorStore;
// import edu.univ.erp.domain.Instructor;

// /**
//  * Service layer that merges data from both Auth DB and ERP DB for instructors.
//  */
// public class InstructorService {
//     private final InstructorStore instructorStore = new InstructorStore();
//     private final AuthStore authStore = new AuthStore();

//     /**
//      * Fetch instructor details from ERP DB and username from Auth DB.
//      */
//     public Instructor getInstructorById(int userId) {
//         Instructor instructor = instructorStore.findById(userId);
//         if (instructor != null) {
//             // Add username fetched from Auth DB
//             instructor.setUsername(authStore.findUsernameByUserId(userId));
//         }
//         return instructor;
//     }
// }
package edu.univ.erp.service;

import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.data.GradingStore;
import edu.univ.erp.data.SectionStore; // Need this to fetch sections
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria;

import java.util.List;
import java.util.OptionalDouble; // Useful for calculating sum

/**
 * Service layer that manages instructor-related data and business logic,
 * including profile fetching, section management, and grading criteria.
 */
public class InstructorService {
    
    private final InstructorStore instructorStore = new InstructorStore();
    private final AuthStore authStore = new AuthStore();
    private final SectionStore sectionStore = new SectionStore(); // Added
    private final GradingStore gradingStore = new GradingStore();   // Added

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

    // -----------------------------------------------------------------
    // SECTION MANAGEMENT (Needed for the Grading UI dropdown)
    // -----------------------------------------------------------------

    /**
     * Fetches all sections taught by a specific instructor.
     */
    public List<Section> getSectionsByInstructorId(int instructorId) {
        // We assume SectionStore.findByInstructor(int) exists and is implemented
        return sectionStore.findByInstructor(instructorId); 
    }

    // -----------------------------------------------------------------
    // GRADING CRITERIA MANAGEMENT
    // -----------------------------------------------------------------

    /**
     * Retrieves the current grading criteria for a specific section.
     */
    public List<GradingCriteria> getGradingCriteria(int sectionId) {
        return gradingStore.findBySectionId(sectionId);
    }

    /**
     * Updates the grading criteria for a section after applying business logic validation.
     * @param sectionId The ID of the section to update.
     * @param criteriaList The new list of criteria components and weights.
     * @return true if the update was successful and validation passed, false otherwise.
     */
    public boolean updateGradingCriteria(int sectionId, List<GradingCriteria> criteriaList) {
        
        // 1. Business Logic Validation: Ensure total weight equals 100%
        OptionalDouble totalWeightOpt = criteriaList.stream()
                .mapToDouble(GradingCriteria::getWeightPercentage)
                .reduce(Double::sum);

        double totalWeight = totalWeightOpt.orElse(0.0);

        // Check against 100.0 with a small tolerance for floating-point accuracy
        if (Math.abs(totalWeight - 100.0) > 0.001) { 
            // In a UI application, this message should be captured and displayed.
            System.err.println("Validation Failed: Total grading weight must equal 100%. Current total: " + totalWeight + "%");
            return false;
        }
        
        // Basic check for empty list (should have at least one component)
        if (criteriaList.isEmpty()) {
            System.err.println("Validation Failed: Criteria list cannot be empty.");
            return false;
        }

        // 2. Data Persistence: Call the Store method to perform DELETE/INSERT in a transaction
        return gradingStore.saveOrUpdateCriteria(sectionId, criteriaList);
    }
}