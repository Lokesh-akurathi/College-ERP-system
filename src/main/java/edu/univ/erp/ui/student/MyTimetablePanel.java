package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyTimetablePanel extends JPanel {
    private final EnrollmentService enrollmentService;

    public MyTimetablePanel() {
        this.enrollmentService = new EnrollmentService();
        
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JLabel titleLabel = new JLabel("My Timetable");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeConstants.TEXT_DARK);
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
        table.setGridColor(ThemeConstants.PRIMARY_NAVY);
        table.setForeground(ThemeConstants.TEXT_DARK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(ThemeConstants.PRIMARY_NAVY);
        table.getTableHeader().setForeground(ThemeConstants.TEXT_LIGHT);
        DefaultTableCellRenderer rowStripeRenderer = new DefaultTableCellRenderer() {
    private static final long serialVersionUID = 1L;
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        ((JComponent)c).setOpaque(true); 

     
        setHorizontalAlignment(SwingConstants.LEFT); 
        if (isSelected) {
            c.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            c.setForeground(ThemeConstants.TEXT_LIGHT);
        } else {
            // Zebra Striping Logic
            if (row % 2 == 0) {
                c.setBackground(new Color(240, 245, 250)); // Light bluish/gray stripe
            } else {
                c.setBackground(Color.WHITE); // White stripe
            }
            c.setForeground(ThemeConstants.TEXT_DARK);
        }
        return c;
    }
}; table.setDefaultRenderer(Object.class, rowStripeRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}
