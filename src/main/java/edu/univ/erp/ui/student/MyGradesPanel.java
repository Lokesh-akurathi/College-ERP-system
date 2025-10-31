package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.GradeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGradesPanel extends JPanel {
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;

    public MyGradesPanel() {
        this.enrollmentService = new EnrollmentService();
        this.gradeService = new GradeService();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Title", "Quiz", "Midterm", "End-Sem", "Final Score", "Grade"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Enrollment> enrollments = enrollmentService.getMyEnrollments();
        
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
                enrollment.getCourseCode(),
                enrollment.getCourseTitle(),
                scoreMap.containsKey("QUIZ") ? String.format("%.2f", scoreMap.get("QUIZ")) : "-",
                scoreMap.containsKey("MIDTERM") ? String.format("%.2f", scoreMap.get("MIDTERM")) : "-",
                scoreMap.containsKey("ENDSEM") ? String.format("%.2f", scoreMap.get("ENDSEM")) : "-",
                scoreMap.containsKey("FINAL") ? String.format("%.2f", scoreMap.get("FINAL")) : "-",
                finalGrade.isEmpty() ? "-" : finalGrade
            };
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Grading: Quiz (20%), Midterm (30%), End-Sem (50%)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }
}
