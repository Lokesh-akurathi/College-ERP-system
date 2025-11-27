
package edu.univ.erp.service;

import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.data.GradingStore;
import edu.univ.erp.data.SectionStore; 
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.domain.GradeStats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble; 


public class InstructorService {
    
    private final InstructorStore instructorStore = new InstructorStore();
    private final AuthStore authStore = new AuthStore();
    private final SectionStore sectionStore = new SectionStore(); 
    private final GradingStore gradingStore = new GradingStore();
    private final EnrollmentService enrollmentService = new EnrollmentService();
   
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

    return instructorStore.findAll(); 
}
public String exportGradesToCsv(int sectionId) {
    
    List<GradingCriteria> criteria = gradingStore.findBySectionId(sectionId);
    List<GradeStats> grades = gradingStore.findGradesBySectionId(sectionId);
    
    StringBuilder csv = new StringBuilder();


    csv.append("Enrollment_ID,Roll_Number");

    for (GradingCriteria c : criteria) {
     
        if (!"FINAL".equals(c.getComponentName())) { 
            csv.append(",")
               .append(c.getComponentName()).append("_SCORE (")
               .append(c.getWeightPercentage()).append("%)");
        }
    }
    
    csv.append(",Final_SCORE,Final_GRADE\n"); 

    for (GradeStats gs : grades) {
        csv.append(gs.getEnrollmentId()).append(",").append(gs.getRollNumber());

      
        for (GradingCriteria c : criteria) {
            if (!"FINAL".equals(c.getComponentName())) { 
                
             
                String componentKey = c.getComponentName().toLowerCase();
                
                Double score = gs.getComponentScores().get(componentKey); 
                csv.append(",").append(score != null ? score : ""); 
            }
        }
        

        Double finalScore = gs.getFinalScore();
        String finalGrade = "";
        

        if (finalScore != null) {

            if (finalScore >= 90) {
                finalGrade = "A";
            } else if (finalScore >= 85) {
                finalGrade = "A-";
            } else if (finalScore >= 80) {
                finalGrade = "B";
            } else if (finalScore >= 75) {
                finalGrade = "B-";
            } else if (finalScore >= 70) {
                finalGrade = "C";
            } else if (finalScore >= 65) {
                finalGrade = "C-";
            } else if (finalScore >= 60) {
                finalGrade = "D";
            } else {
                finalGrade = "F";
            }
        }

        csv.append(",").append(finalScore != null ? finalScore : "");
        csv.append(",").append(finalGrade);

        csv.append("\n");
    }
    
    return csv.toString();
}
public String importGradesFromCsv(int sectionId, String csvContent) {
    if (csvContent == null || csvContent.trim().isEmpty()) {
        return "Error: CSV content is empty.";
    }

    String[] lines = csvContent.split("\\r?\\n");
    if (lines.length < 2) {
        return "Error: CSV contains only headers or is empty.";
    }

    String[] header = lines[0].split(",");
    List<String> componentNames = new ArrayList<>();

    for (int i = 2; i < header.length - 2; i++) {

        String columnName = header[i];
        int underscoreIndex = columnName.indexOf("_SCORE");
        if (underscoreIndex != -1) {
            componentNames.add(columnName.substring(0, underscoreIndex).toUpperCase());
        } else {
            return "Error: Unexpected header format in column " + i + ": " + columnName;
        }
    }

    List<GradeImportData> validImportDataList = new ArrayList<>();
    List<String> importErrors = new ArrayList<>(); 

    for (int rowNum = 1; rowNum < lines.length; rowNum++) {
        String line = lines[rowNum].trim();
        if (line.isEmpty()) continue;

        String[] values = line.split(",");
        if (values.length != header.length) {
            importErrors.add("Row " + (rowNum + 1) + ": Column count mismatch.");
            continue;
        }
        
        try {
            int enrollmentId = Integer.parseInt(values[0].trim());
            String rollNumber = values[1].trim(); 

           
            String validationError = enrollmentService.validateRollNumberForEnrollment(enrollmentId, rollNumber);
            
            if (validationError != null) {
               
                importErrors.add("Row " + (rowNum + 1) + " (ID: " + enrollmentId + ", Roll: " + rollNumber + "): " + validationError);
                continue; 
            }
           
            Map<String, Double> componentScores = new HashMap<>();
            
           
            for (int i = 0; i < componentNames.size(); i++) {
                String componentName = componentNames.get(i);
                String scoreStr = values[i + 2].trim();
                
                if (!scoreStr.isEmpty()) {
                    Double score = Double.parseDouble(scoreStr);
                    componentScores.put(componentName, score);
                }
            }
            
           
            Double finalScore = null;
            String finalScoreStr = values[header.length - 2].trim();
            if (!finalScoreStr.isEmpty()) {
                finalScore = Double.parseDouble(finalScoreStr);
            }
            
            
            String finalGrade = values[header.length - 1].trim();

            
            validImportDataList.add(new GradeImportData(
                enrollmentId,
                rollNumber, 
                componentScores,
                finalScore,
                finalGrade
            ));

        } catch (NumberFormatException e) {
            importErrors.add("Row " + (rowNum + 1) + ": Invalid number format in score/ID column.");
        }
    }
    
    
   int successfullyUpdatedCount = 0;
String databaseErrorMessage = null;

if (!validImportDataList.isEmpty()) {
    try {
        gradingStore.updateGrades(validImportDataList); 
        successfullyUpdatedCount = validImportDataList.size();
    } catch (SQLException e) {
        System.err.println("Database error during grade import: " + e.getMessage());
        e.printStackTrace();
        databaseErrorMessage = "Database update failed: " + e.getMessage();
    }
}



if (!importErrors.isEmpty()) {
   
    String errorDetails = "Import encountered " + importErrors.size() + " validation issue(s):\n" + 
                          String.join("\n", importErrors);

    if (databaseErrorMessage != null) {
        
        return "Error: Import failed due to multiple issues. Only " + successfullyUpdatedCount + " rows were updated. Details:\n" + 
               databaseErrorMessage + "\n" + errorDetails;
    } 
    
    if (successfullyUpdatedCount > 0) {
     
        return "Successfully updated " + successfullyUpdatedCount + " student grades. Failed to process " + 
               importErrors.size() + " row(s). Details:\n" + errorDetails;
    }
    
    
    return "Error: Import failed. No valid grade data found or processed. Details:\n" + errorDetails;

} else if (databaseErrorMessage != null) {
    
    return "Error: " + databaseErrorMessage;
    
} else if (successfullyUpdatedCount > 0) {

    return "Successfully imported and updated " + successfullyUpdatedCount + " student grades.";
}


return "No valid grade data found to import.";
}}