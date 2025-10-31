package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
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

        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
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
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerButton = new JButton("Register for Selected Section");
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> registerForSection());
        buttonPanel.add(registerButton);

        JButton refreshButton = new JButton("Refresh");
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
