// package edu.univ.erp.domain;

// import java.util.Map;

// public class GradeStats {
//     private final double averageScore;
//     private final double medianScore;
//     private final Map<String, Integer> gradeCounts;

//     public GradeStats(double averageScore, double medianScore, Map<String, Integer> gradeCounts) {
//         this.averageScore = averageScore;
//         this.medianScore = medianScore;
//         this.gradeCounts = gradeCounts;
//     }

//     public double getAverageScore() {
//         return averageScore;
//     }

//     public double getMedianScore() {
//         return medianScore;
//     }

//     public Map<String, Integer> getGradeCounts() {
//         return gradeCounts;
//     }
// }

package edu.univ.erp.domain;

import java.util.Map;
import java.util.HashMap;

public class GradeStats {
    
    // --- 1. INDIVIDUAL STUDENT DATA (Required by GradingStore) ---
    private int enrollmentId;
    private String rollNumber;
    private String firstName;    // FIX: Added Field
    private String lastName;     // FIX: Added Field
    private Map<String, Double> componentScores;
    private Double finalScore;   // FIX: Added Field
    private String letterGrade;  // FIX: Added Field
    
    // --- 2. SUMMARY STATISTICS (Original Fields) ---
    // NOTE: Removed 'final' modifier to allow initialization across constructors
    private double averageScore;
    private double medianScore;
    private Map<String, Integer> gradeCounts;


    // FIX 2: Comprehensive Constructor for Individual Student Data 
    // This signature matches the call in GradingStore.java
    public GradeStats(int enrollmentId, String rollNumber, String firstName, String lastName, 
                      Map<String, Double> componentScores, Double finalScore, String letterGrade) {
        
        this.enrollmentId = enrollmentId;
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.componentScores = componentScores != null ? componentScores : new HashMap<>();
        this.finalScore = finalScore;
        this.letterGrade = letterGrade;
        
        // Initialize summary fields to defaults for individual records
        this.averageScore = 0.0;
        this.medianScore = 0.0;
        this.gradeCounts = new HashMap<>();
    }
    
    // Existing Constructor for Summary Stats
    public GradeStats(double averageScore, double medianScore, Map<String, Integer> gradeCounts) {
        // Initializes student/individual fields to defaults
        this(0, null, null, null, null, null, null); 
        
        // Overwrite the summary fields
        this.averageScore = averageScore;
        this.medianScore = medianScore;
        this.gradeCounts = gradeCounts;
    }


    // --- Getters ---
    
    public int getEnrollmentId() { return enrollmentId; }
    public String getRollNumber() { return rollNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    
    // FIX 3: Crucial Getter used by GradingStore to add scores
    public Map<String, Double> getComponentScores() { return componentScores; } 
    
    // Existing/Needed Getters for Scores
    public Double getFinalScore() { return finalScore; }
    public String getLetterGrade() { return letterGrade; }
    
    public double getAverageScore() { return averageScore; }
    public double getMedianScore() { return medianScore; }
    public Map<String, Integer> getGradeCounts() { return gradeCounts; }
    
    public Double getScoreForCriteriaName(String criteriaName) {
        if (componentScores != null && componentScores.containsKey(criteriaName)) {
            return componentScores.get(criteriaName);
        }
        return null;
    }
}