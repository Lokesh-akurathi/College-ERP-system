package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.ui.ThemeConstants;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseCatalogPanel extends JPanel {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public CourseCatalogPanel() {
        this.courseService = new CourseService();
        this.enrollmentService = new EnrollmentService();
        
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeConstants.TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Section ID", "Code", "Title", "Credits", "Day/Time", "Room", "Instructor", "Enrolled", "Capacity", "Semester", "Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        if (isSelected) {
            c.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            c.setForeground(ThemeConstants.TEXT_LIGHT);
        } else {
            
            if (row % 2 == 0) {
                c.setBackground(new Color(240, 245, 250)); // Light stripe
            } else {
                c.setBackground(Color.WHITE); // White stripe
            }
            c.setForeground(ThemeConstants.TEXT_DARK);
        }
        return c;
    }
};
table.setDefaultRenderer(Object.class, rowStripeRenderer);
table.setDefaultRenderer(String.class, rowStripeRenderer);
table.setDefaultRenderer(Integer.class, rowStripeRenderer); 
table.setDefaultRenderer(Double.class, rowStripeRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        JButton registerButton = new JButton("Register for Selected Section");
        registerButton.setBackground(ThemeConstants.ACTION_BUTTON_VIBRANT);
        registerButton.setForeground(ThemeConstants.TEXT_LIGHT);
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> registerForSection());
        buttonPanel.add(registerButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        refreshButton.setForeground(ThemeConstants.TEXT_LIGHT);
        refreshButton.addActionListener(e -> loadSections());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadSections();
    }

    private void loadSections() {
        tableModel.setRowCount(0);
        List<Section> sections = courseService.getAllSections();
        
        for (Section section : sections) {
            Object[] row = {
                section.getSectionId(),
                section.getCourseCode(),
                section.getCourseTitle(),
                section.getCourseCredits(),
                section.getDayTime(),
                section.getRoom(),
                section.getInstructorName(),
                section.getEnrolled(),
                section.getCapacity(),
                section.getSemester(),
                section.getYear()
            };
            tableModel.addRow(row);
        }
    }

    private void registerForSection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Please select a section to register for.");
            return;
        }

        int sectionId = (int) tableModel.getValueAt(selectedRow, 0);
        String error = enrollmentService.registerForSection(sectionId);
        
        if (error == null) {
            MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                    "Successfully registered for section!");
            loadSections();
        } else {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
        }
    }
}
