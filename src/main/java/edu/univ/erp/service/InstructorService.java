
package edu.univ.erp.service;

import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.data.GradingStore;
import edu.univ.erp.data.SectionStore; 
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria;

import java.util.List;
import java.util.OptionalDouble; 


public class InstructorService {
    
    private final InstructorStore instructorStore = new InstructorStore();
    private final AuthStore authStore = new AuthStore();
    private final SectionStore sectionStore = new SectionStore(); 
    private final GradingStore gradingStore = new GradingStore();

   
    public Instructor getInstructorById(int userId) {
        Instructor instructor = instructorStore.findById(userId);
        if (instructor != null) {
            
            instructor.setUsername(authStore.findUsernameByUserId(userId));
        }
        return instructor;
    }

   
    public List<Section> getSectionsByInstructorId(int instructorId) {
        
        return sectionStore.findByInstructor(instructorId); 
    }

    
    public List<GradingCriteria> getGradingCriteria(int sectionId) {
        return gradingStore.findBySectionId(sectionId);
    }

   
    public boolean updateGradingCriteria(int sectionId, List<GradingCriteria> criteriaList) {
        
        
        OptionalDouble totalWeightOpt = criteriaList.stream()
                .mapToDouble(GradingCriteria::getWeightPercentage)
                .reduce(Double::sum);

        double totalWeight = totalWeightOpt.orElse(0.0);

        
        if (Math.abs(totalWeight - 100.0) > 0.001) { 
            
            System.err.println("Validation Failed: Total grading weight must equal 100%. Current total: " + totalWeight + "%");
            return false;
        }
        
        
        if (criteriaList.isEmpty()) {
            System.err.println("Validation Failed: Criteria list cannot be empty.");
            return false;
        }

        
        return gradingStore.saveOrUpdateCriteria(sectionId, criteriaList);
    }
    public List<Instructor> getAllInstructors() {
    // This will call a corresponding method in your InstructorStore
    return instructorStore.findAll(); 
}

}