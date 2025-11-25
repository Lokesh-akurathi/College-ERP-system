package edu.univ.erp.domain;

import java.util.Map;

public class GradeStats {
    private final double averageScore;
    private final double medianScore;
    private final Map<String, Integer> gradeCounts;

    public GradeStats(double averageScore, double medianScore, Map<String, Integer> gradeCounts) {
        this.averageScore = averageScore;
        this.medianScore = medianScore;
        this.gradeCounts = gradeCounts;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public double getMedianScore() {
        return medianScore;
    }

    public Map<String, Integer> getGradeCounts() {
        return gradeCounts;
    }
}