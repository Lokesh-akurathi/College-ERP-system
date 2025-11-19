// package edu.univ.erp.ui.student;

// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.domain.Grade;
// import edu.univ.erp.service.EnrollmentService;
// import edu.univ.erp.service.GradeService;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class MyGradesPanel extends JPanel {
//     private final EnrollmentService enrollmentService;
//     private final GradeService gradeService;

//     public MyGradesPanel() {
//         this.enrollmentService = new EnrollmentService();
//         this.gradeService = new GradeService();
        
//         setLayout(new BorderLayout());

//         JLabel titleLabel = new JLabel("My Grades");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
//         add(titleLabel, BorderLayout.NORTH);

//         String[] columns = {"Course Code", "Course Title", "Quiz", "Midterm", "End-Sem", "Final Score", "Grade"};
//         DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };

//         List<Enrollment> enrollments = enrollmentService.getMyEnrollments();
        
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
//                 enrollment.getCourseCode(),
//                 enrollment.getCourseTitle(),
//                 scoreMap.containsKey("QUIZ") ? String.format("%.2f", scoreMap.get("QUIZ")) : "-",
//                 scoreMap.containsKey("MIDTERM") ? String.format("%.2f", scoreMap.get("MIDTERM")) : "-",
//                 scoreMap.containsKey("ENDSEM") ? String.format("%.2f", scoreMap.get("ENDSEM")) : "-",
//                 scoreMap.containsKey("FINAL") ? String.format("%.2f", scoreMap.get("FINAL")) : "-",
//                 finalGrade.isEmpty() ? "-" : finalGrade
//             };
//             tableModel.addRow(row);
//         }

//         JTable table = new JTable(tableModel);
//         table.setRowHeight(25);
        
//         JScrollPane scrollPane = new JScrollPane(table);
//         add(scrollPane, BorderLayout.CENTER);

//         JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//         JLabel infoLabel = new JLabel("Grading: Quiz (20%), Midterm (30%), End-Sem (50%)");
//         infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
//         infoPanel.add(infoLabel);
//         add(infoPanel, BorderLayout.SOUTH);
//     }
// }

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

        // NOTE: These columns are hardcoded and will not reflect dynamic criteria. 
        // A future enhancement is required here, but we proceed with the current structure for now.
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
            String finalGrade = ""; // Letter grade (A, B, C, etc.)
            
            for (Grade grade : grades) {
                // Component names from the database are UPPERCASE (e.g., "QUIZ", "FINAL")
                String componentKey = grade.getComponent(); 
                
                if (grade.getScore() != null) {
                    scoreMap.put(componentKey, grade.getScore());
                }
                
                // CRITICAL FIX: Calculate the letter grade using the FINAL numerical score
                if ("FINAL".equals(componentKey) && grade.getScore() != null) {
                    // Call the helper method in GradeService to convert the score to a letter grade
                    finalGrade = gradeService.getLetterGrade(grade.getScore());
                }
            }

            Object[] row = {
                enrollment.getCourseCode(),
                enrollment.getCourseTitle(),
                scoreMap.containsKey("QUIZ") ? String.format("%.2f", scoreMap.get("QUIZ")) : "-",
                scoreMap.containsKey("MIDTERM") ? String.format("%.2f", scoreMap.get("MIDTERM")) : "-",
                scoreMap.containsKey("ENDSEM") ? String.format("%.2f", scoreMap.get("ENDSEM")) : "-",
                scoreMap.containsKey("FINAL EXAM") ? String.format("%.2f", scoreMap.get("FINAL EXAM")) : "-",
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
        // NOTE: This info label is hardcoded and inaccurate for dynamic criteria, but left for compilation.
        JLabel infoLabel = new JLabel("Grading: Quiz (20%), Midterm (30%), End-Sem (50%)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }
}