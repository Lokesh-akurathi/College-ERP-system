// package edu.univ.erp.service;

// import edu.univ.erp.access.AccessControl;
// import edu.univ.erp.auth.session.SessionManager;
// import edu.univ.erp.data.EnrollmentStore;
// import edu.univ.erp.data.GradeStore;
// import edu.univ.erp.data.SectionStore;
// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.domain.Grade;
// import edu.univ.erp.domain.Section;

// import java.util.List;

// public class GradeService {
//     private final GradeStore gradeStore;
//     private final EnrollmentStore enrollmentStore;
//     private final SectionStore sectionStore;
//     private final SessionManager sessionManager;

//     public static final double QUIZ_WEIGHT = 0.20;
//     public static final double MIDTERM_WEIGHT = 0.30;
//     public static final double ENDSEM_WEIGHT = 0.50;

//     public GradeService() {
//         this.gradeStore = new GradeStore();
//         this.enrollmentStore = new EnrollmentStore();
//         this.sectionStore = new SectionStore();
//         this.sessionManager = SessionManager.getInstance();
//     }

//     public String enterScore(int enrollmentId, String component, double score) {
//         if (!AccessControl.canAccessInstructorFeatures()) {
//             return AccessControl.getAccessDeniedMessage();
//         }

//         if (!AccessControl.canModify()) {
//             return AccessControl.getMaintenanceDenialMessage();
//         }

//         if (score < 0 || score > 100) {
//             return "Score must be between 0 and 100.";
//         }

//         Enrollment enrollment = enrollmentStore.findById(enrollmentId);
//         if (enrollment == null) {
//             return "Enrollment not found.";
//         }

//         if (!canInstructorAccessSection(enrollment.getSectionId())) {
//             return "You can only enter grades for your own sections.";
//         }

//         if (gradeStore.updateOrCreate(enrollmentId, component, score)) {
//             return null;
//         } else {
//             return "Failed to save score.";
//         }
//     }

//     public String computeFinalGrade(int enrollmentId) {
//         if (!AccessControl.canAccessInstructorFeatures()) {
//             return AccessControl.getAccessDeniedMessage();
//         }

//         if (!AccessControl.canModify()) {
//             return AccessControl.getMaintenanceDenialMessage();
//         }

//         Enrollment enrollment = enrollmentStore.findById(enrollmentId);
//         if (enrollment == null) {
//             return "Enrollment not found.";
//         }

//         if (!canInstructorAccessSection(enrollment.getSectionId())) {
//             return "You can only compute grades for your own sections.";
//         }

//         List<Grade> grades = gradeStore.findByEnrollment(enrollmentId);
//         Double quizScore = null;
//         Double midtermScore = null;
//         Double endsemScore = null;

//         for (Grade grade : grades) {
//             if ("QUIZ".equals(grade.getComponent())) {
//                 quizScore = grade.getScore();
//             } else if ("MIDTERM".equals(grade.getComponent())) {
//                 midtermScore = grade.getScore();
//             } else if ("ENDSEM".equals(grade.getComponent())) {
//                 endsemScore = grade.getScore();
//             }
//         }

//         if (quizScore == null || midtermScore == null || endsemScore == null) {
//             return "All components (QUIZ, MIDTERM, ENDSEM) must have scores before computing final grade.";
//         }

//         double finalScore = (quizScore * QUIZ_WEIGHT) + (midtermScore * MIDTERM_WEIGHT) + (endsemScore * ENDSEM_WEIGHT);
//         String letterGrade = computeLetterGrade(finalScore);

//         Grade finalGrade = gradeStore.findByEnrollmentAndComponent(enrollmentId, "FINAL");
//         if (finalGrade == null) {
//             finalGrade = new Grade();
//             finalGrade.setEnrollmentId(enrollmentId);
//             finalGrade.setComponent("FINAL");
//         }
//         finalGrade.setScore(finalScore);
//         finalGrade.setFinalGrade(letterGrade);

//         if (finalGrade.getGradeId() == 0) {
//             if (gradeStore.create(finalGrade)) {
//                 return null;
//             } else {
//                 return "Failed to save final grade.";
//             }
//         } else {
//             if (gradeStore.update(finalGrade)) {
//                 return null;
//             } else {
//                 return "Failed to update final grade.";
//             }
//         }
//     }

//     public List<Grade> getGradesForEnrollment(int enrollmentId) {
//         return gradeStore.findByEnrollment(enrollmentId);
//     }

//     private boolean canInstructorAccessSection(int sectionId) {
//         if (AccessControl.isAdmin()) {
//             return true;
//         }

//         Section section = sectionStore.findById(sectionId);
//         return section != null && section.getInstructorId() == sessionManager.getCurrentUserId();
//     }

//     private String computeLetterGrade(double score) {
//         if (score >= 90) return "A";
//         if (score >= 80) return "B";
//         if (score >= 70) return "C";
//         if (score >= 60) return "D";
//         return "F";
//     }
// }

// package edu.univ.erp.service;

// import edu.univ.erp.access.AccessControl;
// import edu.univ.erp.auth.session.SessionManager;
// import edu.univ.erp.data.EnrollmentStore;
// import edu.univ.erp.data.GradeStore;
// import edu.univ.erp.data.SectionStore;
// import edu.univ.erp.data.GradingStore;          // NEW: Import GradingStore
// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.domain.Grade;
// import edu.univ.erp.domain.Section;
// import edu.univ.erp.domain.GradingCriteria;    // NEW: Import GradingCriteria

// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// public class GradeService {
//     private final GradeStore gradeStore;
//     private final EnrollmentStore enrollmentStore;
//     private final SectionStore sectionStore;
//     private final GradingStore gradingStore;       // NEW: Instance of GradingStore
//     private final SessionManager sessionManager;

//     // REMOVED: Hardcoded weight constants (QUIZ_WEIGHT, MIDTERM_WEIGHT, ENDSEM_WEIGHT)

//     public GradeService() {
//         this.gradeStore = new GradeStore();
//         this.enrollmentStore = new EnrollmentStore();
//         this.sectionStore = new SectionStore();
//         this.gradingStore = new GradingStore();    // NEW: Initialize GradingStore
//         this.sessionManager = SessionManager.getInstance();
//     }

//     public String enterScore(int enrollmentId, String component, double score) {
//         if (!AccessControl.canAccessInstructorFeatures()) {
//             return AccessControl.getAccessDeniedMessage();
//         }

//         if (!AccessControl.canModify()) {
//             return AccessControl.getMaintenanceDenialMessage();
//         }

//         if (score < 0 || score > 100) {
//             return "Score must be between 0 and 100.";
//         }

//         Enrollment enrollment = enrollmentStore.findById(enrollmentId);
//         if (enrollment == null) {
//             return "Enrollment not found.";
//         }

//         if (!canInstructorAccessSection(enrollment.getSectionId())) {
//             return "You can only enter grades for your own sections.";
//         }

//         if (gradeStore.updateOrCreate(enrollmentId, component, score)) {
//             return null;
//         } else {
//             return "Failed to save score.";
//         }
//     }

//     // -----------------------------------------------------------------
//     // UPDATED: computeFinalGrade now uses custom criteria
//     // -----------------------------------------------------------------
//     public String computeFinalGrade(int enrollmentId) {
//         if (!AccessControl.canAccessInstructorFeatures()) {
//             return AccessControl.getAccessDeniedMessage();
//         }

//         if (!AccessControl.canModify()) {
//             return AccessControl.getMaintenanceDenialMessage();
//         }

//         Enrollment enrollment = enrollmentStore.findById(enrollmentId);
//         if (enrollment == null) {
//             return "Enrollment not found.";
//         }

//         if (!canInstructorAccessSection(enrollment.getSectionId())) {
//             return "You can only compute grades for your own sections.";
//         }

//         // 1. Fetch criteria and raw grades
//         List<GradingCriteria> criteriaList = gradingStore.findBySectionId(enrollment.getSectionId());
//         List<Grade> rawScores = gradeStore.findByEnrollment(enrollmentId);

//         if (criteriaList.isEmpty()) {
//             return "Grading criteria not set for this section. Please set weights before computing final grade.";
//         }

//         // 2. Map raw scores for quick lookup (Component Name -> Score)
//         Map<String, Double> scoreMap = rawScores.stream()
//                 .collect(Collectors.toMap(
//                     Grade::getComponent,
//                     Grade::getScore
//                 ));

//         double finalScore = 0.0;
//         double totalWeight = 0.0;
//         StringBuilder missingComponents = new StringBuilder();

//         // 3. Calculate weighted average using custom criteria
//         for (GradingCriteria criteria : criteriaList) {
//             String component = criteria.getComponentName();
//             double weight = criteria.getWeightPercentage();

//             if (scoreMap.containsKey(component)) {
//                 double score = scoreMap.get(component);
//                 finalScore += score * (weight / 100.0);
//                 totalWeight += weight;
//             } else {
//                 // Track missing components for an error message
//                 missingComponents.append(component).append(", ");
//             }
//         }

//         // 4. Validation: Check if all components have scores
//         // We only proceed if the total weight of *scored* components equals the total defined weight (100.0)
//         // Check for floating point equality using a small tolerance
//         if (Math.abs(totalWeight - 100.0) > 0.001 || missingComponents.length() > 0) {
            
//             String missingMsg = "";
//             if (missingComponents.length() > 0) {
//                  // Remove trailing comma and space
//                 missingMsg = " Missing scores for components: " + missingComponents.substring(0, missingComponents.length() - 2) + ".";
//             }
//             // This error indicates either missing scores or the criteria weights don't sum to 100 (if totalWeight != 100)
//             return "Not all required components have scores or criteria weights are invalid. Total weight of scored components: " + String.format("%.2f%%", totalWeight) + "." + missingMsg;
//         }


//         String letterGrade = computeLetterGrade(finalScore);

//         // 5. Update/Create the "FINAL" grade entry
//         // NOTE: The database structure change suggested removing final_grade column from the grades table.
//         // If your database still has the final_grade column in the grades table, this code is fine.
//         // If the final_grade column was moved/removed, you might need to update the final letter grade
//         // in the Enrollment table instead. Assuming the Grade object still supports setFinalGrade().
        
//         Grade finalGrade = gradeStore.findByEnrollmentAndComponent(enrollmentId, "FINAL");
//         if (finalGrade == null) {
//             finalGrade = new Grade();
//             finalGrade.setEnrollmentId(enrollmentId);
//             finalGrade.setComponent("FINAL");
//         }
//         finalGrade.setScore(finalScore);
//         finalGrade.setFinalGrade(letterGrade);

//         if (finalGrade.getGradeId() == 0) {
//             if (gradeStore.create(finalGrade)) {
//                 return null;
//             } else {
//                 return "Failed to save final grade.";
//             }
//         } else {
//             if (gradeStore.update(finalGrade)) {
//                 return null;
//             } else {
//                 return "Failed to update final grade.";
//             }
//         }
//     }

//     public List<Grade> getGradesForEnrollment(int enrollmentId) {
//         return gradeStore.findByEnrollment(enrollmentId);
//     }

//     private boolean canInstructorAccessSection(int sectionId) {
//         if (AccessControl.isAdmin()) {
//             return true;
//         }

//         Section section = sectionStore.findById(sectionId);
//         return section != null && section.getInstructorId() == sessionManager.getCurrentUserId();
//     }

//     private String computeLetterGrade(double score) {
//         if (score >= 90) return "A";
//         if (score >= 80) return "B";
//         if (score >= 70) return "C";
//         if (score >= 60) return "D";
//         return "F";
//     }
// }

package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.GradeStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.data.GradingStore; // NEW: Import GradingStore
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria; // NEW: Import GradingCriteria

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradeService {
    private final GradeStore gradeStore;
    private final EnrollmentStore enrollmentStore;
    private final SectionStore sectionStore;
    private final GradingStore gradingStore; // NEW: Instance of GradingStore
    private final SessionManager sessionManager;

    public GradeService() {
        this.gradeStore = new GradeStore();
        this.enrollmentStore = new EnrollmentStore();
        this.sectionStore = new SectionStore();
        this.gradingStore = new GradingStore(); // NEW: Initialize GradingStore
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
        
        // CRITICAL FIX: Ensure component name is UPPERCASE to match GradeStore logic
        String componentKey = component.toUpperCase();

        if (gradeStore.updateOrCreate(enrollmentId, componentKey, score)) {
            return null;
        } else {
            return "Failed to save score.";
        }
    }

    // -----------------------------------------------------------------
    // UPDATED: computeFinalGrade now uses custom criteria and simplifies persistence
    // -----------------------------------------------------------------
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

        // 1. Fetch criteria and raw grades
        List<GradingCriteria> criteriaList = gradingStore.findBySectionId(enrollment.getSectionId());
        List<Grade> rawScores = gradeStore.findByEnrollment(enrollmentId);

        if (criteriaList.isEmpty()) {
            return "Grading criteria not set for this section. Please set weights before computing final grade.";
        }

        // 2. Map raw scores for quick lookup (Component Name -> Score)
        Map<String, Double> scoreMap = rawScores.stream()
                .collect(Collectors.toMap(
                        Grade::getComponent,
                        Grade::getScore
                    ));

        double finalScore = 0.0;
        double totalWeight = 0.0;
        StringBuilder missingComponents = new StringBuilder();

        // 3. Calculate weighted average using custom criteria
        for (GradingCriteria criteria : criteriaList) {
            // CRITICAL: Ensure we check against the UPPERCASE component key from the database
            String component = criteria.getComponentName().toUpperCase(); 
            double weight = criteria.getWeightPercentage();

            if (scoreMap.containsKey(component)) {
                double score = scoreMap.get(component);
                finalScore += score * (weight / 100.0);
                totalWeight += weight;
            } else {
                // Track missing components for an error message using the user-friendly name
                missingComponents.append(criteria.getComponentName()).append(", ");
            }
        }

        // 4. Validation: Check if all components have scores (or if the criteria sums to 100)
        if (Math.abs(totalWeight - 100.0) > 0.001 || missingComponents.length() > 0) {
            
            String missingMsg = "";
            if (missingComponents.length() > 0) {
                // Remove trailing comma and space
                missingMsg = " Missing scores for components: " + missingComponents.substring(0, missingComponents.length() - 2) + ".";
            }
            return "Not all required components have scores or criteria weights are invalid. Total weight of scored components: " + String.format("%.2f%%", totalWeight) + "." + missingMsg;
        }


        // String letterGrade = computeLetterGrade(finalScore); // Calculate letter grade but do NOT persist it here.

        // 5. Update/Create the "FINAL" grade entry
        // We use gradeStore.updateOrCreate which handles both cases, simplifying the logic.
        
        // This persists the calculated final score (0-100) under the component name "FINAL"
        if (gradeStore.updateOrCreate(enrollmentId, "FINAL", finalScore)) {
             return null;
        } else {
             return "Failed to save final score (FINAL component).";
        }
    }
    
    // This helper method needs to remain for the UI to display the letter grade.
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

    private String computeLetterGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}