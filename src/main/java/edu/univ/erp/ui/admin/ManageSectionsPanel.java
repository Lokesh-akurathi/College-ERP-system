package edu.univ.erp.ui.admin;

import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSectionsPanel extends JPanel {
    private final CourseService courseService;
    private final InstructorStore instructorStore;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ManageSectionsPanel() {
        this.courseService = new CourseService();
        this.instructorStore = new InstructorStore();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Manage Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Section ID", "Course", "Instructor ID", "Day/Time", "Room", 
                           "Enrolled", "Capacity", "Semester", "Year"};
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

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Section"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Course> courses = courseService.getAllCourses();
        JComboBox<CourseItem> courseCombo = new JComboBox<>();
        for (Course course : courses) {
            courseCombo.addItem(new CourseItem(course));
        }

        List<Instructor> instructors = instructorStore.findAll();
        JComboBox<InstructorItem> instructorCombo = new JComboBox<>();
        for (Instructor instructor : instructors) {
            instructorCombo.addItem(new InstructorItem(instructor));
        }

        JTextField dayTimeField = new JTextField(15);
        JTextField roomField = new JTextField(10);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 500, 1));
        String[] semesters = {"Spring", "Fall", "Summer"};
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2020, 2030, 1));

        int row = 0;
        addFormField(formPanel, gbc, row++, "Course:", courseCombo);
        addFormField(formPanel, gbc, row++, "Instructor:", instructorCombo);
        addFormField(formPanel, gbc, row++, "Day/Time:", dayTimeField);
        addFormField(formPanel, gbc, row++, "Room:", roomField);
        addFormField(formPanel, gbc, row++, "Capacity:", capacitySpinner);
        addFormField(formPanel, gbc, row++, "Semester:", semesterCombo);
        addFormField(formPanel, gbc, row++, "Year:", yearSpinner);

        JButton addButton = new JButton("Add Section");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> {
            if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Please select course and instructor.");
                return;
            }

            CourseItem selectedCourse = (CourseItem) courseCombo.getSelectedItem();
            InstructorItem selectedInstructor = (InstructorItem) instructorCombo.getSelectedItem();
            String dayTime = dayTimeField.getText().trim();
            String room = roomField.getText().trim();
            int capacity = (int) capacitySpinner.getValue();
            String semester = (String) semesterCombo.getSelectedItem();
            int year = (int) yearSpinner.getValue();

            String validationError = ValidationHelper.validateSectionData(dayTime, room, capacity);
            if (validationError != null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
                return;
            }

            String error = courseService.createSection(selectedCourse.course.getCourseId(), 
                    selectedInstructor.instructor.getUserId(), dayTime, room, capacity, semester, year);
            
            if (error == null) {
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Section created successfully!");
                dayTimeField.setText("");
                roomField.setText("");
                capacitySpinner.setValue(30);
                loadSections();
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(addButton, gbc);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSections());
        gbc.gridx = 2;
        formPanel.add(refreshButton, gbc);

        add(formPanel, BorderLayout.SOUTH);

        loadSections();
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
        gbc.gridwidth = 1;
    }

    private void loadSections() {
        tableModel.setRowCount(0);
        List<Section> sections = courseService.getAllSections();
        
        for (Section section : sections) {
            Object[] row = {
                section.getSectionId(),
                section.getCourseCode() + " - " + section.getCourseTitle(),
                section.getInstructorId(),
                section.getDayTime(),
                section.getRoom(),
                section.getEnrolled(),
                section.getCapacity(),
                section.getSemester(),
                section.getYear()
            };
            tableModel.addRow(row);
        }
    }

    private static class CourseItem {
        Course course;
        CourseItem(Course course) { this.course = course; }
        @Override
        public String toString() { return course.getCode() + " - " + course.getTitle(); }
    }

    private static class InstructorItem {
        Instructor instructor;
        InstructorItem(Instructor instructor) { this.instructor = instructor; }
        @Override
        public String toString() { return "Instructor ID: " + instructor.getUserId(); }
    }
}
