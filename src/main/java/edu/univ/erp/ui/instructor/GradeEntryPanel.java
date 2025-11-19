// package edu.univ.erp.ui.instructor;

// import edu.univ.erp.auth.session.SessionManager;
// import edu.univ.erp.data.SectionStore;
// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.domain.Grade;
// import edu.univ.erp.domain.Section;
// import edu.univ.erp.service.EnrollmentService;
// import edu.univ.erp.service.GradeService;
// import edu.univ.erp.ui.common.MessageDialog;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class GradeEntryPanel extends JPanel {
//     private final SectionStore sectionStore;
//     private final EnrollmentService enrollmentService;
//     private final GradeService gradeService;
//     private final JComboBox<SectionItem> sectionCombo;
//     private final JTable table;
//     private final DefaultTableModel tableModel;

//     public GradeEntryPanel() {
//         this.sectionStore = new SectionStore();
//         this.enrollmentService = new EnrollmentService();
//         this.gradeService = new GradeService();
        
//         setLayout(new BorderLayout());

//         JLabel titleLabel = new JLabel("Grade Entry");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
//         add(titleLabel, BorderLayout.NORTH);

//         JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         topPanel.add(new JLabel("Select Section:"));
        
//         sectionCombo = new JComboBox<>();
//         int instructorId = SessionManager.getInstance().getCurrentUserId();
//         List<Section> sections = sectionStore.findByInstructor(instructorId);
//         for (Section section : sections) {
//             sectionCombo.addItem(new SectionItem(section));
//         }
//         sectionCombo.addActionListener(e -> loadStudents());
//         topPanel.add(sectionCombo);
        
//         add(topPanel, BorderLayout.NORTH);

//         String[] columns = {"Enrollment ID", "Student ID", "Quiz", "Midterm", "End-Sem", "Final Score", "Grade"};
//         tableModel = new DefaultTableModel(columns, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return column >= 2 && column <= 4;
//             }
//         };
//         table = new JTable(tableModel);
//         table.setRowHeight(25);
        
//         JScrollPane scrollPane = new JScrollPane(table);
//         add(scrollPane, BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
//         JButton saveButton = new JButton("Save Scores");
//         saveButton.setBackground(new Color(76, 175, 80));
//         saveButton.setForeground(Color.WHITE);
//         saveButton.addActionListener(e -> saveScores());
//         buttonPanel.add(saveButton);

//         JButton computeButton = new JButton("Compute Final Grades");
//         computeButton.setBackground(new Color(33, 150, 243));
//         computeButton.setForeground(Color.WHITE);
//         computeButton.addActionListener(e -> computeFinalGrades());
//         buttonPanel.add(computeButton);

//         add(buttonPanel, BorderLayout.SOUTH);

//         if (sectionCombo.getItemCount() > 0) {
//             loadStudents();
//         }
//     }

//     private void loadStudents() {
//         tableModel.setRowCount(0);
//         SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
//         if (selectedItem == null) return;

//         List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(selectedItem.section.getSectionId());
        
//         for (Enrollment enrollment : enrollments) {
//             List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
            
//             Map<String, Double> scoreMap = new HashMap<>();
//             String finalGrade = "";
            
//             for (Grade grade : grades) {
//                 if (grade.getScore() != null) {
//                     scoreMap.put(grade.getComponent(), grade.getScore());
//                 }
//                 if ("FINAL".equals(grade.getComponent()) && grade.getFinalGrade() != null) {
//                     finalGrade = grade.getFinalGrade();
//                 }
//             }

//             Object[] row = {
//                 enrollment.getEnrollmentId(),
//                 enrollment.getStudentId(),
//                 scoreMap.get("QUIZ"),
//                 scoreMap.get("MIDTERM"),
//                 scoreMap.get("ENDSEM"),
//                 scoreMap.get("FINAL"),
//                 finalGrade
//             };
//             tableModel.addRow(row);
//         }
//     }

//     private void saveScores() {
//         for (int i = 0; i < tableModel.getRowCount(); i++) {
//             int enrollmentId = (int) tableModel.getValueAt(i, 0);
            
//             Object quizObj = tableModel.getValueAt(i, 2);
//             Object midtermObj = tableModel.getValueAt(i, 3);
//             Object endsemObj = tableModel.getValueAt(i, 4);

//             if (quizObj != null && !quizObj.toString().isEmpty()) {
//                 try {
//                     double score = Double.parseDouble(quizObj.toString());
//                     String error = gradeService.enterScore(enrollmentId, "QUIZ", score);
//                     if (error != null) {
//                         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
//                         return;
//                     }
//                 } catch (NumberFormatException e) {
//                     MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
//                             "Invalid quiz score for row " + (i + 1));
//                     return;
//                 }
//             }

//             if (midtermObj != null && !midtermObj.toString().isEmpty()) {
//                 try {
//                     double score = Double.parseDouble(midtermObj.toString());
//                     String error = gradeService.enterScore(enrollmentId, "MIDTERM", score);
//                     if (error != null) {
//                         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
//                         return;
//                     }
//                 } catch (NumberFormatException e) {
//                     MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
//                             "Invalid midterm score for row " + (i + 1));
//                     return;
//                 }
//             }

//             if (endsemObj != null && !endsemObj.toString().isEmpty()) {
//                 try {
//                     double score = Double.parseDouble(endsemObj.toString());
//                     String error = gradeService.enterScore(enrollmentId, "ENDSEM", score);
//                     if (error != null) {
//                         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
//                         return;
//                     }
//                 } catch (NumberFormatException e) {
//                     MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
//                             "Invalid end-sem score for row " + (i + 1));
//                     return;
//                 }
//             }
//         }
        
//         MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), "Scores saved successfully!");
//         loadStudents();
//     }

//     private void computeFinalGrades() {
//         for (int i = 0; i < tableModel.getRowCount(); i++) {
//             int enrollmentId = (int) tableModel.getValueAt(i, 0);
//             String error = gradeService.computeFinalGrade(enrollmentId);
//             if (error != null) {
//                 MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
//                         "Skipping row " + (i + 1) + ": " + error);
//             }
//         }
        
//         MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
//                 "Final grades computed!");
//         loadStudents();
//     }

//     private static class SectionItem {
//         Section section;

//         SectionItem(Section section) {
//             this.section = section;
//         }

//         @Override
//         public String toString() {
//             return section.getCourseCode() + " - " + section.getDayTime();
//         }
//     }
// }

package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria; // NEW
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.InstructorService; // NEW
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList; // NEW
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeEntryPanel extends JPanel {
    private final SectionStore sectionStore;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final InstructorService instructorService; // NEW: To fetch criteria
    
    private final JComboBox<SectionItem> sectionCombo;
    private final JTable table;
    private  DefaultTableModel tableModel;

    // Stores the list of component names for the currently selected section
    private List<GradingCriteria> currentCriteria; 

    public GradeEntryPanel() {
        this.sectionStore = new SectionStore();
        this.enrollmentService = new EnrollmentService();
        this.gradeService = new GradeService();
        this.instructorService = new InstructorService(); // Initialize NEW service
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Grade Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // --- TOP PANEL: Section Selection ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section:"));
        
        sectionCombo = new JComboBox<>();
        int instructorId = SessionManager.getInstance().getCurrentUserId();
        List<Section> sections = sectionStore.findByInstructor(instructorId);
        for (Section section : sections) {
            sectionCombo.addItem(new SectionItem(section));
        }
        sectionCombo.addActionListener(e -> loadStudents());
        topPanel.add(sectionCombo);
        
        add(topPanel, BorderLayout.NORTH);

        // Initialize table with temporary columns
        String[] tempColumns = {"Enrollment ID", "Student ID", "Score 1", "Score 2", "Final Score", "Grade"};
        tableModel = new DefaultTableModel(tempColumns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Save Scores");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveScores());
        buttonPanel.add(saveButton);

        JButton computeButton = new JButton("Compute Final Grades");
        computeButton.setBackground(new Color(33, 150, 243));
        computeButton.setForeground(Color.WHITE);
        computeButton.addActionListener(e -> computeFinalGrades());
        buttonPanel.add(computeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load data for the first item on initialization
        if (sectionCombo.getItemCount() > 0) {
            loadStudents();
        }
    }

    // private void loadStudents() {
    //     tableModel.setRowCount(0);
    //     SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
    //     if (selectedItem == null) return;

    //     int sectionId = selectedItem.section.getSectionId();
        
    //     // 1. Fetch criteria and update table columns
    //     currentCriteria = instructorService.getGradingCriteria(sectionId);
    //     updateTableColumns(); // NEW CALL

    //     // 2. Fetch enrollments
    //     List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(sectionId);
        
    //     // 3. Populate rows
    //     for (Enrollment enrollment : enrollments) {
    //         List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
            
    //         Map<String, Double> scoreMap = new HashMap<>();
    //         String finalGrade = "";
    //         Double finalScore = null;
            
    //         for (Grade grade : grades) {
    //             if (grade.getScore() != null) {
    //                 scoreMap.put(grade.getComponent(), grade.getScore());
    //             }
    //             if ("FINAL".equals(grade.getComponent())) {
    //                 finalScore = grade.getScore();
    //                 if (grade.getFinalGrade() != null) {
    //                     finalGrade = grade.getFinalGrade();
    //                 }
    //             }
    //         }

    //         // Start of the dynamic row data (Enrollment ID and Student ID)
    //         List<Object> rowData = new ArrayList<>();
    //         rowData.add(enrollment.getEnrollmentId());
    //         rowData.add(enrollment.getStudentId());

    //         // Add scores dynamically based on currentCriteria
    //         for (GradingCriteria criteria : currentCriteria) {
    //             rowData.add(scoreMap.get(criteria.getComponentName()));
    //         }

    //         // Add the final columns
    //         rowData.add(finalScore);
    //         rowData.add(finalGrade);
            
    //         tableModel.addRow(rowData.toArray());
    //     }
    // }
// Inside GradeEntryPanel.java

private void loadStudents() {
    tableModel.setRowCount(0);
    SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
    if (selectedItem == null) return;

    int sectionId = selectedItem.section.getSectionId();
    
    // 1. Fetch criteria and update table columns
    currentCriteria = instructorService.getGradingCriteria(sectionId);
    updateTableColumns();

    // 2. Fetch enrollments
    List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(sectionId);
    
    // 3. Populate rows
    for (Enrollment enrollment : enrollments) {
        List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
        
        Map<String, Double> scoreMap = new HashMap<>();
        String finalGrade = ""; // Letter grade (A, B, C, etc.)
        Double finalScore = null; // Numerical final score (0.0 to 100.0)
        
        for (Grade grade : grades) {
            // Note: Component names from the database are UPPERCASE (e.g., "HOMEWORK", "FINAL")
            String componentKey = grade.getComponent(); 
            
            if (grade.getScore() != null) {
                // Map all component scores
                scoreMap.put(componentKey, grade.getScore()); 
            }
            
            // Check for the FINAL score component
            if ("FINAL".equals(componentKey)) {
                finalScore = grade.getScore();
                
                // CRITICAL FIX: Calculate the letter grade using the score, 
                // since getFinalGrade() is removed.
                if (finalScore != null) { 
                    finalGrade = gradeService.getLetterGrade(finalScore); 
                }
            }
        }

        // Start of the dynamic row data (Enrollment ID and Student ID)
        List<Object> rowData = new ArrayList<>();
        rowData.add(enrollment.getEnrollmentId());
        rowData.add(enrollment.getStudentId());

        // Add scores dynamically based on currentCriteria
        for (GradingCriteria criteria : currentCriteria) {
            // CRITICAL: Ensure we check against the UPPERCASE component key from the database
            String componentKey = criteria.getComponentName().toUpperCase();
            rowData.add(scoreMap.get(componentKey));
        }

        // Add the final columns
        rowData.add(finalScore);
        rowData.add(finalGrade);
        
        tableModel.addRow(rowData.toArray());
    }
}
    // NEW METHOD: Dynamically updates the table columns based on the criteria
    private void updateTableColumns() {
    // 1. Prepare new column identifiers
    List<String> dynamicColumns = new ArrayList<>();
    dynamicColumns.add("Enrollment ID");
    dynamicColumns.add("Student ID");
    
    for (GradingCriteria criteria : currentCriteria) {
        dynamicColumns.add(criteria.getComponentName() + 
                           String.format(" (%.0f%%)", criteria.getWeightPercentage()));
    }
    
    dynamicColumns.add("Final Score");
    dynamicColumns.add("Grade");

    // 2. Create a NEW model instance with the data and the custom editability
    // We use the raw Vector type to satisfy the constructor when dealing with getDataVector()
    @SuppressWarnings({ "unchecked", "rawtypes" })
    DefaultTableModel newModel = new DefaultTableModel((java.util.Vector) tableModel.getDataVector(), new java.util.Vector(dynamicColumns)) {
        
        // This method is mandatory to allow non-String objects (like Double) in the model
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            int criteriaCount = currentCriteria.size();
            // Columns 2 through 2 + criteriaCount - 1 (the dynamic score columns) are Double
            if (columnIndex >= 2 && columnIndex < (2 + criteriaCount)) {
                return Double.class;
            }
            // Final Score column (next index) is also a Double
            if (columnIndex == (2 + criteriaCount)) {
                return Double.class;
            }
            return String.class;
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            int criteriaCount = currentCriteria.size();
            // Allow editing for the dynamic score columns (indices 2 up to 2 + criteriaCount - 1)
            return column >= 2 && column < (2 + criteriaCount); 
        }
    };
    
    // 3. Set the new model to the table and update the reference
    table.setModel(newModel);
    this.tableModel = newModel; // CRITICAL: Update the tableModel reference
}

    private void saveScores() {
        if (currentCriteria == null || currentCriteria.isEmpty()) {
            MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
                                      "Please set grading criteria for this section first.");
            return;
        }

        // The dynamic score columns start at index 2
        int firstScoreColumnIndex = 2;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            int enrollmentId = (int) table.getValueAt(i, 0);
            
            // Loop through all dynamic score columns
            for (int j = 0; j < currentCriteria.size(); j++) {
                int column = firstScoreColumnIndex + j;
                GradingCriteria criteria = currentCriteria.get(j);
                String componentName = criteria.getComponentName().toUpperCase();
                
                Object scoreObj = table.getValueAt(i, column);
                
                if (scoreObj != null && !scoreObj.toString().isEmpty()) {
                    try {
                        double score = Double.parseDouble(scoreObj.toString());
                        String error = gradeService.enterScore(enrollmentId, componentName, score);
                        
                        if (error != null) {
                            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                                                    "Score Save Error for " + componentName + " (Row " + (i + 1) + "): " + error);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                                                "Invalid score for " + componentName + " (Row " + (i + 1) + ")");
                        return;
                    }
                }
            }
        }
        
        MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), "Scores saved successfully!");
        loadStudents();
    }

    private void computeFinalGrades() {
        if (currentCriteria == null || currentCriteria.isEmpty()) {
            MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
                                      "Please set grading criteria for this section before computing final grades.");
            return;
        }
        
        for (int i = 0; i < table.getRowCount(); i++) {
            int enrollmentId = (int) table.getValueAt(i, 0);
            String error = gradeService.computeFinalGrade(enrollmentId);
            if (error != null) {
                MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
                                          "Skipping final grade calculation for row " + (i + 1) + ": " + error);
            }
        }
        
        MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                  "Final grades computed! Scores must have been saved before this step.");
        loadStudents();
    }

    private static class SectionItem {
        Section section;

        SectionItem(Section section) {
            this.section = section;
        }

        @Override
        public String toString() {
            return section.getCourseCode() + " - " + section.getDayTime();
        }
    }
}