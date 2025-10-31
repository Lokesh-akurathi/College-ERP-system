package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {
    private final CourseService courseService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ManageCoursesPanel() {
        this.courseService = new CourseService();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Manage Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Course ID", "Code", "Title", "Credits"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Course"));

        formPanel.add(new JLabel("Code:"));
        JTextField codeField = new JTextField(10);
        formPanel.add(codeField);

        formPanel.add(new JLabel("Title:"));
        JTextField titleField = new JTextField(20);
        formPanel.add(titleField);

        formPanel.add(new JLabel("Credits:"));
        JSpinner creditsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        formPanel.add(creditsSpinner);

        JButton addButton = new JButton("Add Course");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            int credits = (int) creditsSpinner.getValue();

            String validationError = ValidationHelper.validateCourseData(code, title, credits);
            if (validationError != null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
                return;
            }

            String error = courseService.createCourse(code, title, credits);
            if (error == null) {
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Course created successfully!");
                codeField.setText("");
                titleField.setText("");
                creditsSpinner.setValue(3);
                loadCourses();
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
            }
        });
        formPanel.add(addButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCourses());
        formPanel.add(refreshButton);

        add(formPanel, BorderLayout.SOUTH);

        loadCourses();
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();
        
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCode(),
                course.getTitle(),
                course.getCredits()
            };
            tableModel.addRow(row);
        }
    }
}
