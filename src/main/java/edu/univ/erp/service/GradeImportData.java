package edu.univ.erp.service;

import java.util.Map;

public class GradeImportData {
    private int enrollmentId;
    private String rollNumber; 
    private Map<String, Double> componentScores;
    private Double finalScore;
    private String finalGrade;

    public GradeImportData(int enrollmentId,String rollNumber, Map<String, Double> componentScores, Double finalScore, String finalGrade) {
        this.enrollmentId = enrollmentId;
        this.componentScores = componentScores;
        this.rollNumber = rollNumber;
        this.finalScore = finalScore;
        this.finalGrade = finalGrade;
    }

   
    public int getEnrollmentId() { return enrollmentId; }
    public Map<String, Double> getComponentScores() { return componentScores; }
    public String getRollNumber() { return rollNumber; }
    public Double getFinalScore() { return finalScore; }
    public String getFinalGrade() { return finalGrade; }
}