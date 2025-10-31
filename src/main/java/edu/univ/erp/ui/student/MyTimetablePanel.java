package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.EnrollmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyTimetablePanel extends JPanel {
    private final EnrollmentService enrollmentService;

    public MyTimetablePanel() {
        this.enrollmentService = new EnrollmentService();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Timetable");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Course Code", "Course Title", "Day/Time", "Room", "Instructor"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        List<Enrollment> enrollments = enrollmentService.getMyEnrollments();
        for (Enrollment enrollment : enrollments) {
            Object[] row = {
                enrollment.getCourseCode(),
                enrollment.getCourseTitle(),
                enrollment.getSectionDayTime(),
                enrollment.getSectionRoom(),
                enrollment.getInstructorName()
            };
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}
