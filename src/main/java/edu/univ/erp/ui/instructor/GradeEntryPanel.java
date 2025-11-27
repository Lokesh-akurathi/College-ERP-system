package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.ui.ThemeConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader; 
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeEntryPanel extends JPanel {
    
    
    private final SectionStore sectionStore;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final InstructorService instructorService; 
    
    private final JComboBox<SectionItem> sectionCombo;
    private final JTable table;
    private DefaultTableModel tableModel;

    private Section selectedSection; 
    private List<GradingCriteria> currentCriteria; 

   
    public GradeEntryPanel() {
        this.sectionStore = new SectionStore();
        this.enrollmentService = new EnrollmentService();
        this.gradeService = new GradeService();
        this.instructorService = new InstructorService(); 
        
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JLabel titleLabel = new JLabel("Grade Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(ThemeConstants.TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
       JLabel selectLabel = new JLabel("Select Section:");
        selectLabel.setForeground(ThemeConstants.TEXT_DARK);
        topPanel.add(selectLabel);
        
        sectionCombo = new JComboBox<>();
        int instructorId = SessionManager.getInstance().getCurrentUserId();
        List<Section> sections = sectionStore.findByInstructor(instructorId);
        for (Section section : sections) {
            sectionCombo.addItem(new SectionItem(section));
        }
        
        sectionCombo.addActionListener(e -> loadStudents()); 
        topPanel.add(sectionCombo);
        
        add(topPanel, BorderLayout.NORTH);


        String[] tempColumns = {"Enrollment ID", "Roll Number", "Score 1", "Score 2", "Final Score", "Grade"};
        tableModel = new DefaultTableModel(tempColumns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(ThemeConstants.PRIMARY_NAVY);
        table.getTableHeader().setForeground(ThemeConstants.TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        table.setSelectionForeground(ThemeConstants.TEXT_LIGHT);
        GradeCellRenderer renderer = new GradeCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);

     
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        
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
        
      
        JButton exportButton = new JButton("Export Scores Template");
        exportButton.setBackground(new Color(100, 181, 246)); 
        exportButton.setForeground(Color.BLACK);
        exportButton.addActionListener(e -> exportScoresAction());
        buttonPanel.add(exportButton); 

        
        JButton importButton = new JButton("Import Scores");
        importButton.setBackground(new Color(255, 193, 7)); 
        importButton.setForeground(Color.BLACK);
        importButton.addActionListener(e -> importScoresAction()); 
        buttonPanel.add(importButton);

        add(buttonPanel, BorderLayout.SOUTH);

      
        if (sectionCombo.getItemCount() > 0) {
            loadStudents();
        }
    }

    
    private void loadStudents() {
        tableModel.setRowCount(0);
        SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
        
        if (selectedItem == null) {
            this.selectedSection = null; 
            this.currentCriteria = new ArrayList<>();
            updateTableColumns(); 
            return;
        }

        this.selectedSection = selectedItem.section; 
        int sectionId = selectedSection.getSectionId();
        
        
        currentCriteria = instructorService.getGradingCriteria(sectionId);
        updateTableColumns();


        List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(sectionId);
        

        for (Enrollment enrollment : enrollments) {
            List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
            
            Map<String, Double> scoreMap = new HashMap<>();
            String finalGrade = "";
            Double finalScore = null; 
            
            for (Grade grade : grades) {
             
                String componentKey = grade.getComponent(); 
                
                if (grade.getScore() != null) {
                    scoreMap.put(componentKey, grade.getScore()); 
                }
                if ("FINAL".equals(componentKey)) {
                    finalScore = grade.getScore();

                    if (finalScore != null) { 
                        finalGrade = gradeService.getLetterGrade(finalScore); 
                    }
                }
            }

            
            List<Object> rowData = new ArrayList<>();
            rowData.add(enrollment.getEnrollmentId());
            rowData.add(enrollment.getRollNumber());

            
            for (GradingCriteria criteria : currentCriteria) {
                
                
                String componentKey = criteria.getComponentName().toUpperCase();
                rowData.add(scoreMap.get(componentKey));
            }


            rowData.add(finalScore);
            rowData.add(finalGrade);
            
            tableModel.addRow(rowData.toArray());
        }
    }
   
private class GradeCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column) {
        
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
       
        int criteriaCount = currentCriteria.size();
        int firstScoreColumn = 2; 
        int finalScoreColumn = firstScoreColumn + criteriaCount;
        int finalGradeColumn = finalScoreColumn + 1; 

     
        if (column >= firstScoreColumn && column <= finalScoreColumn) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        
        if (isSelected) {
            
            c.setForeground(Color.WHITE); 
            
            return c; 
        }

      
        c.setForeground(ThemeConstants.TEXT_DARK);
        
        
        if (column >= firstScoreColumn && column < finalScoreColumn) {
            c.setBackground(Color.WHITE);
        } 
        
       
        else if (column == finalGradeColumn) {
            if (value != null) {
                String grade = value.toString();
                if (grade.startsWith("A")) {
                    c.setBackground(new Color(200, 255, 200)); // Light Green (Success)
                } else if (grade.startsWith("F")) {
                    c.setBackground(new Color(255, 200, 200)); // Light Red (Failure)
                } else {
                    c.setBackground(new Color(230, 240, 245)); // Light Blue/Gray (Default Pass)
                }
            } else {
                 c.setBackground(ThemeConstants.SECONDARY_BACKGROUND); 
            }
        }
        
        
        else { 
           
            c.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        }
        ((JComponent)c).setOpaque(true);
        return c;
    }
}
   
    private void updateTableColumns() {
    
        List<String> dynamicColumns = new ArrayList<>();
        dynamicColumns.add("Enrollment ID");
        dynamicColumns.add("Roll Number");
        
        for (GradingCriteria criteria : currentCriteria) {
            dynamicColumns.add(criteria.getComponentName() + 
                                 String.format(" (%.0f%%)", criteria.getWeightPercentage()));
        }
        
        dynamicColumns.add("Final Score");
        dynamicColumns.add("Grade");

        @SuppressWarnings({ "unchecked", "rawtypes" })
        DefaultTableModel newModel = new DefaultTableModel((java.util.Vector) tableModel.getDataVector(), new java.util.Vector(dynamicColumns)) {
            
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                int criteriaCount = currentCriteria.size();
            
                if (columnIndex >= 2 && columnIndex < (2 + criteriaCount)) {
                    return Double.class;
                }
            
                if (columnIndex == (2 + criteriaCount)) {
                    return Double.class;
                }
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                int criteriaCount = currentCriteria.size();
            
                return column >= 2 && column < (2 + criteriaCount); 
            }
        };
        
        
        table.setModel(newModel);
        this.tableModel = newModel; 
    }

  
    private void saveScores() {
        if (currentCriteria == null || currentCriteria.isEmpty()) {
            MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
                                     "Please set grading criteria for this section first.");
            return;
        }

        
        int firstScoreColumnIndex = 2;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            int enrollmentId = (int) table.getValueAt(i, 0);
            
            
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
    
  
    private void exportScoresAction() {
        if (selectedSection == null) {
            MessageDialog.showError(null, "Please select a section first.");
            return;
        }
        
       
        String csvData = instructorService.exportGradesToCsv(selectedSection.getSectionId());
        
        if (csvData == null || csvData.trim().isEmpty()) {
            MessageDialog.showInfo(null, "No student data or criteria defined to export.");
            return;
        }

    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Grade Template");
        
       
        String defaultFileName = selectedSection.getCourseCode() + "_" + 
                                 selectedSection.getSemester() + selectedSection.getYear() + "_Grades.csv";
        fileChooser.setSelectedFile(new File(defaultFileName));

       
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
           
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }
            
          
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(csvData);
                MessageDialog.showSuccess(null, "Template saved successfully to:\n" + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                MessageDialog.showError(null, "Error saving file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
   
    private void importScoresAction() {
        if (selectedSection == null) {
            MessageDialog.showError(null, "Please select a section first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Grade CSV File to Import");
        
     
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            
           
            String csvContent;
            try (BufferedReader br = new BufferedReader(new FileReader(fileToOpen))) {
                
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                csvContent = sb.toString();

            } catch (IOException ex) {
                MessageDialog.showError(null, "Error reading file: " + ex.getMessage());
                ex.printStackTrace();
                return;
            }
            
            if (csvContent.trim().isEmpty()) {
                MessageDialog.showWarning(null, "The selected file is empty.");
                return;
            }

           
            int sectionId = selectedSection.getSectionId();
            
            
            String resultMessage = instructorService.importGradesFromCsv(sectionId, csvContent);
            
            
            if (resultMessage.startsWith("Error:")) {
               
                MessageDialog.showError(null, "Import Failed: " + resultMessage.substring(6)); 
            } else {
                MessageDialog.showSuccess(null, resultMessage);
                loadStudents(); 
            }
        }
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