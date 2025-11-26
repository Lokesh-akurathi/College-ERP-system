
package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.GradeStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.data.GradingStore; 
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria; 

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradeService {
    private final GradeStore gradeStore;
    private final EnrollmentStore enrollmentStore;
    private final SectionStore sectionStore;
    private final GradingStore gradingStore;
    private final SessionManager sessionManager;

    public GradeService() {
        this.gradeStore = new GradeStore();
        this.enrollmentStore = new EnrollmentStore();
        this.sectionStore = new SectionStore();
        this.gradingStore = new GradingStore();
        this.sessionManager = SessionManager.getInstance();
    }

    public String enterScore(int enrollmentId, String component, double score) {
        if (!AccessControl.canAccessInstructorFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        if (score < 0 || score > 100) {
            return "Score must be between 0 and 100.";
        }

        Enrollment enrollment = enrollmentStore.findById(enrollmentId);
        if (enrollment == null) {
            return "Enrollment not found.";
        }

        if (!canInstructorAccessSection(enrollment.getSectionId())) {
            return "You can only enter grades for your own sections.";
        }
        
      
        String componentKey = component.toUpperCase();

        if (gradeStore.updateOrCreate(enrollmentId, componentKey, score)) {
            return null;
        } else {
            return "Failed to save score.";
        }
    }

   
    public String computeFinalGrade(int enrollmentId) {
        if (!AccessControl.canAccessInstructorFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        if (!AccessControl.canModify()) {
            return AccessControl.getMaintenanceDenialMessage();
        }

        Enrollment enrollment = enrollmentStore.findById(enrollmentId);
        if (enrollment == null) {
            return "Enrollment not found.";
        }

        if (!canInstructorAccessSection(enrollment.getSectionId())) {
            return "You can only compute grades for your own sections.";
        }

       
        List<GradingCriteria> criteriaList = gradingStore.findBySectionId(enrollment.getSectionId());
        List<Grade> rawScores = gradeStore.findByEnrollment(enrollmentId);

        if (criteriaList.isEmpty()) {
            return "Grading criteria not set for this section. Please set weights before computing final grade.";
        }

       
        Map<String, Double> scoreMap = rawScores.stream()
                .collect(Collectors.toMap(
                        Grade::getComponent,
                        Grade::getScore
                    ));

        double finalScore = 0.0;
        double totalWeight = 0.0;
        StringBuilder missingComponents = new StringBuilder();

       
double totalDefinedWeight = 0.0; 

for (GradingCriteria criteria : criteriaList) {

String component = criteria.getComponentName().toUpperCase(); 
double weight = criteria.getWeightPercentage();

 
 totalDefinedWeight += weight; 

 if (scoreMap.containsKey(component)) {
 double score = scoreMap.get(component);
finalScore += score * (weight / 100.0);
 totalWeight += weight;
} else {

missingComponents.append(criteria.getComponentName()).append(", ");
 }}

    
if (missingComponents.length() > 0) {
 String missingMsg = missingComponents.substring(0, missingComponents.length() - 2);
 
 return "Cannot compute final grade. Missing scores for components: " + missingMsg + ".";
}

 
        if (Math.abs(totalDefinedWeight - 100.0) > 0.001) {
            return "Cannot compute final grade. Total criteria weight is invalid: " 
                + String.format("%.2f%%", totalDefinedWeight) + ". Must sum to 100.00%. Please check criteria management.";
        }
       
        if (gradeStore.updateOrCreate(enrollmentId, "FINAL", finalScore)) {
             return null;
        } else {
             return "Failed to save final score (FINAL component).";
        }
    }
    
  
    public String getLetterGrade(double score) {
        return computeLetterGrade(score);
    }

    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        return gradeStore.findByEnrollment(enrollmentId);
    }

    private boolean canInstructorAccessSection(int sectionId) {
        if (AccessControl.isAdmin()) {
            return true;
        }

        Section section = sectionStore.findById(sectionId);
        return section != null && section.getInstructorId() == sessionManager.getCurrentUserId();
    }
   
public List<Grade> getFinalGradesBySection(int sectionId) {
   
    List<Enrollment> enrollments = enrollmentStore.findBySection(sectionId); 
    
    List<Grade> finalGrades = new ArrayList<>();
    
    for (Enrollment enrollment : enrollments) {
       
        List<Grade> grades = gradeStore.findByEnrollment(enrollment.getEnrollmentId());
        
       
        grades.stream()
              .filter(grade -> "FINAL".equals(grade.getComponent()))
              .findFirst() 
              .ifPresent(finalGrades::add);
    }
    
    return finalGrades;
}



    private String computeLetterGrade(double score) {
        if (score >= 90) return "A";
        if(score>= 85) return "A-";
        if (score >= 80) return "B";
        if (score >= 75) return "B-";
        if (score >= 70) return "C";
        if (score >= 65) return "C-";
        if (score >= 60) return "D";
        return "F";
    }
}