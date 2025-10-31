package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeEntryPanel extends JPanel {
    private final SectionStore sectionStore;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final JComboBox<SectionItem> sectionCombo;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public GradeEntryPanel() {
        this.sectionStore = new SectionStore();
        this.enrollmentService = new EnrollmentService();
        this.gradeService = new GradeService();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Grade Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

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

        String[] columns = {"Enrollment ID", "Student ID", "Quiz", "Midterm", "End-Sem", "Final Score", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2 && column <= 4;
            }
        };
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

        if (sectionCombo.getItemCount() > 0) {
            loadStudents();
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
        if (selectedItem == null) return;

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(selectedItem.section.getSectionId());
        
        for (Enrollment enrollment : enrollments) {
            List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
            
            Map<String, Double> scoreMap = new HashMap<>();
            String finalGrade = "";
            
            for (Grade grade : grades) {
                if (grade.getScore() != null) {
                    scoreMap.put(grade.getComponent(), grade.getScore());
                }
                if ("FINAL".equals(grade.getComponent()) && grade.getFinalGrade() != null) {
                    finalGrade = grade.getFinalGrade();
                }
            }

            Object[] row = {
                enrollment.getEnrollmentId(),
                enrollment.getStudentId(),
                scoreMap.get("QUIZ"),
                scoreMap.get("MIDTERM"),
                scoreMap.get("ENDSEM"),
                scoreMap.get("FINAL"),
                finalGrade
            };
            tableModel.addRow(row);
        }
    }

    private void saveScores() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int enrollmentId = (int) tableModel.getValueAt(i, 0);
            
            Object quizObj = tableModel.getValueAt(i, 2);
            Object midtermObj = tableModel.getValueAt(i, 3);
            Object endsemObj = tableModel.getValueAt(i, 4);

            if (quizObj != null && !quizObj.toString().isEmpty()) {
                try {
                    double score = Double.parseDouble(quizObj.toString());
                    String error = gradeService.enterScore(enrollmentId, "QUIZ", score);
                    if (error != null) {
                        MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
                        return;
                    }
                } catch (NumberFormatException e) {
                    MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                            "Invalid quiz score for row " + (i + 1));
                    return;
                }
            }

            if (midtermObj != null && !midtermObj.toString().isEmpty()) {
                try {
                    double score = Double.parseDouble(midtermObj.toString());
                    String error = gradeService.enterScore(enrollmentId, "MIDTERM", score);
                    if (error != null) {
                        MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
                        return;
                    }
                } catch (NumberFormatException e) {
                    MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                            "Invalid midterm score for row " + (i + 1));
                    return;
                }
            }

            if (endsemObj != null && !endsemObj.toString().isEmpty()) {
                try {
                    double score = Double.parseDouble(endsemObj.toString());
                    String error = gradeService.enterScore(enrollmentId, "ENDSEM", score);
                    if (error != null) {
                        MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
                        return;
                    }
                } catch (NumberFormatException e) {
                    MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                            "Invalid end-sem score for row " + (i + 1));
                    return;
                }
            }
        }
        
        MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), "Scores saved successfully!");
        loadStudents();
    }

    private void computeFinalGrades() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int enrollmentId = (int) tableModel.getValueAt(i, 0);
            String error = gradeService.computeFinalGrade(enrollmentId);
            if (error != null) {
                MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Skipping row " + (i + 1) + ": " + error);
            }
        }
        
        MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Final grades computed!");
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
