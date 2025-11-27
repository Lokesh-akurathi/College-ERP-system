package edu.univ.erp.service;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.GradeStats;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GradeStatsService {
    
    
    private final GradeService gradeService = new GradeService();

    
    public GradeStats calculateStats(int sectionId) {
        
        List<Grade> finalGrades = gradeService.getFinalGradesBySection(sectionId); 
        
      
        
        List<Double> scores = finalGrades.stream()
             .filter(g -> g.getScore() != null && "FINAL".equals(g.getComponent()))
             .map(Grade::getScore)
             .collect(Collectors.toList());

        if (scores.isEmpty()) {
             return new GradeStats(0.0, 0.0, Collections.emptyMap());
        }
        
      
        double average = scores.stream().mapToDouble(d -> d).average().orElse(0.0);
        
       
        Collections.sort(scores);
        double median;
        int size = scores.size();
        if (size % 2 == 1) {
             median = scores.get(size / 2);
        } else {
             median = (scores.get(size / 2 - 1) + scores.get(size / 2)) / 2.0;
        }
        
       
        Map<String, Integer> gradeCounts = finalGrades.stream()
             .filter(g -> g.getScore() != null && "FINAL".equals(g.getComponent()))
             .collect(Collectors.groupingBy(
                 g -> gradeService.getLetterGrade(g.getScore()),
                 Collectors.summingInt(g -> 1)
             ));

        return new GradeStats(average, median, gradeCounts);
    }
}