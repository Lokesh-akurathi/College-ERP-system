
package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {

    private final CourseService courseService = new CourseService();
    private final AdminService adminService = new AdminService();

    private JTable table;
    private DefaultTableModel tableModel;

   
    private JTextField codeField;
    private JTextField titleField;
    private JSpinner creditsSpinner;

   
    private int selectedCourseId = -1;

    public ManageCoursesPanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Manage Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        
        String[] columns = {"Course ID", "Code", "Title", "Credits"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedCourseIntoForm());
        add(new JScrollPane(table), BorderLayout.CENTER);

        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Course"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        codeField = new JTextField(15);
        titleField = new JTextField(20);
        creditsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

        int row = 0;
        addFormField(formPanel, gbc, row++, "Code:", codeField);
        addFormField(formPanel, gbc, row++, "Title:", titleField);
        addFormField(formPanel, gbc, row++, "Credits:", creditsSpinner);

        add(formPanel, BorderLayout.NORTH);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addButton = new JButton("Add Course");
        styleButton(addButton, new Color(76, 175, 80));
        addButton.addActionListener(e -> addCourse());
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update Course");
        styleButton(updateButton, new Color(33, 150, 243));
        updateButton.addActionListener(e -> updateCourse());
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Course");
        styleButton(deleteButton, new Color(244, 67, 54));
        deleteButton.addActionListener(e -> deleteCourse());
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCourses());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCourses();
    }

   
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row,
                              String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
        gbc.gridwidth = 1;
    }

 
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
    }

  
    private void loadCourses() {
        tableModel.setRowCount(0);
        selectedCourseId = -1;
        clearForm();

        for (Course course : courseService.getAllCourses()) {
            tableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits()
            });
        }
    }

    
    private void loadSelectedCourseIntoForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            selectedCourseId = -1;
            clearForm();
            return;
        }

        selectedCourseId = (int) tableModel.getValueAt(row, 0);
        codeField.setText(tableModel.getValueAt(row, 1).toString());
        titleField.setText(tableModel.getValueAt(row, 2).toString());
        creditsSpinner.setValue(Integer.parseInt(tableModel.getValueAt(row, 3).toString()));
    }

    
    private void addCourse() {
        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        int credits = (int) creditsSpinner.getValue();

        String error = ValidationHelper.validateCourseData(code, title, credits);
        if (error != null) {
            MessageDialog.showError(null, error);
            return;
        }

        String result = courseService.createCourse(code, title, credits);
        if (result == null) {
            MessageDialog.showSuccess(null, "Course added successfully!");
            loadCourses();
            clearForm();
        } else {
            MessageDialog.showError(null, result);
        }
    }

  
    private void updateCourse() {
        if (selectedCourseId == -1) {
            MessageDialog.showError(null, "Please select a course to update.");
            return;
        }

        String title = titleField.getText().trim();
        int credits = (int) creditsSpinner.getValue();

        if (title.isEmpty()) {
            MessageDialog.showError(null, "Title cannot be empty.");
            return;
        }

        String code = codeField.getText().trim();
        String msg = adminService.updateCourse(selectedCourseId, code, title, credits);

        MessageDialog.showInfo(null, msg);
        loadCourses();
    }

  
    private void deleteCourse() {
        int row = table.getSelectedRow();
        if (row == -1) {
            MessageDialog.showError(null, "Please select a course to delete.");
            return;
        }

        int courseId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to delete this course?",
                "Delete Course", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String msg = adminService.deleteCourse(courseId);
            MessageDialog.showInfo(null, msg);
            loadCourses();
        }
    }

 
    private void clearForm() {
        codeField.setText("");
        titleField.setText("");
        creditsSpinner.setValue(3);
    }
}
