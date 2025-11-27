package edu.univ.erp.domain;

import java.util.Map;
import java.util.HashMap;

public class GradeStats {
    
  
    private int enrollmentId;
    private String rollNumber;
    private String firstName;    
    private String lastName;     
    private Map<String, Double> componentScores;
    private Double finalScore;   
    private String letterGrade;  
    
   
    private double averageScore;
    private double medianScore;
    private Map<String, Integer> gradeCounts;


    
    public GradeStats(int enrollmentId, String rollNumber, String firstName, String lastName, 
                      Map<String, Double> componentScores, Double finalScore, String letterGrade) {
        
        this.enrollmentId = enrollmentId;
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.componentScores = componentScores != null ? componentScores : new HashMap<>();
        this.finalScore = finalScore;
        this.letterGrade = letterGrade;
        
        
        this.averageScore = 0.0;
        this.medianScore = 0.0;
        this.gradeCounts = new HashMap<>();
    }
    
   
    public GradeStats(double averageScore, double medianScore, Map<String, Integer> gradeCounts) {
       
        this(0, null, null, null, null, null, null); 
        
        
        this.averageScore = averageScore;
        this.medianScore = medianScore;
        this.gradeCounts = gradeCounts;
    }


   
    public int getEnrollmentId() { return enrollmentId; }
    public String getRollNumber() { return rollNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    
    
    public Map<String, Double> getComponentScores() { return componentScores; } 
    
    
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