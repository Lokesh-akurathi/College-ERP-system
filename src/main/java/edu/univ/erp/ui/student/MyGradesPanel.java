
package edu.univ.erp.ui.student;

import edu.univ.erp.data.GradeStore;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.GradeService;
import edu.univ.erp.data.GradingStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

class ScoreWithWeight {
    double score;
    double weight;

    ScoreWithWeight(double score, double weight) {
        this.score = score;
        this.weight = weight;
    }
}


public class MyGradesPanel extends JPanel {

    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final GradingStore gradingStore;

    public MyGradesPanel() {
        this.enrollmentService = new EnrollmentService();
        this.gradeService = new GradeService();
        this.gradingStore = new GradingStore();

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        
        JPanel courseListPanel = new JPanel();
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(courseListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        
        List<Enrollment> enrollments = enrollmentService.getMyEnrollments();

        if (enrollments.isEmpty()) {
            JLabel noCourses = new JLabel("You are not enrolled in any courses.");
            noCourses.setFont(new Font("Arial", Font.ITALIC, 14));
            courseListPanel.add(noCourses);
            return;
        }


        for (Enrollment enrollment : enrollments) {

         
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

          
            JLabel header = new JLabel(
                    enrollment.getCourseCode() + " — " + enrollment.getCourseTitle()
            );
            header.setFont(new Font("Arial", Font.BOLD, 16));
            header.setBorder(new EmptyBorder(0, 0, 10, 0));
            card.add(header, BorderLayout.NORTH);

           
            List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
            Map<String, ScoreWithWeight> componentScores = new LinkedHashMap<>();
            String letterGrade = "-";

            Integer section_id = enrollment.getSectionId();
            List<GradingCriteria> gradingcriteria = gradingStore.findBySectionId(section_id);

            
            Map<String, Double> weightByComponent = new HashMap<>();
            if (gradingcriteria != null) {
                for (GradingCriteria gc : gradingcriteria) {
                    if (gc != null && gc.getComponentName() != null) {
                        String key = gc.getComponentName().trim().toUpperCase();
                        weightByComponent.put(key, gc.getWeightPercentage());
                    }
                }
            }

            if (grades != null) {
                for (Grade g : grades) {
                    String comp = (g.getComponent() == null) ? "-" : g.getComponent().trim();

                    double weightpercentage = weightByComponent.getOrDefault(comp.toUpperCase(), 0.0);

                    if (g.getScore() != null) {
                        componentScores.put(comp, new ScoreWithWeight(g.getScore(), weightpercentage));
                    }
                    if ((comp.equalsIgnoreCase("FINAL") || comp.equalsIgnoreCase("FINAL SCORE") || comp.equalsIgnoreCase("TOTAL")&& g.getScore() != null)){
                        letterGrade = gradeService.getLetterGrade(g.getScore());
                    }
                }
            }


            String[] colNames = {"Component", "Score","weight %"};
            DefaultTableModel model = new DefaultTableModel(colNames, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            if (componentScores.isEmpty()) {
                model.addRow(new Object[]{"No grades yet", "-","-"});
            } else {
                for (String comp : componentScores.keySet()) {
                    ScoreWithWeight sw = componentScores.get(comp);

                    model.addRow(new Object[]{
                            comp,
                            String.format("%.2f", sw.score),
                            String.format("%.1f", sw.weight)
                    });
                }
            }

            JTable table = new JTable(model);
            table.setRowHeight(24);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

            JScrollPane tableScrollPane = new JScrollPane(table);
            card.add(tableScrollPane, BorderLayout.CENTER);


            JLabel finalGradeLabel = new JLabel("Final Grade: " + letterGrade);
            finalGradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            finalGradeLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
            card.add(finalGradeLabel, BorderLayout.SOUTH);


            courseListPanel.add(card);

            courseListPanel.add(Box.createVerticalStrut(15));
        }
    }
}
