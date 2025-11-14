// package edu.univ.erp.ui.admin;

// import edu.univ.erp.data.InstructorStore;
// import edu.univ.erp.domain.Course;
// import edu.univ.erp.domain.Instructor;
// import edu.univ.erp.domain.Section;
// import edu.univ.erp.service.CourseService;
// import edu.univ.erp.service.AdminService;
// import edu.univ.erp.ui.common.MessageDialog;
// import edu.univ.erp.util.ValidationHelper;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.util.List;

// public class ManageSectionsPanel extends JPanel {
//     private final CourseService courseService;
//     private final InstructorStore instructorStore;
//     private final AdminService adminService;
//     private final JTable table;
//     private final DefaultTableModel tableModel;

//     public ManageSectionsPanel() {
//         this.courseService = new CourseService();
//         this.instructorStore = new InstructorStore();
//         this.adminService = new AdminService();

//         setLayout(new BorderLayout());

//         JLabel titleLabel = new JLabel("Manage Sections");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
//         add(titleLabel, BorderLayout.NORTH);

//         String[] columns = {"Section ID", "Course", "Instructor ID", "Day/Time", "Room",
//                 "Enrolled", "Capacity", "Semester", "Year"};
//         tableModel = new DefaultTableModel(columns, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };
//         table = new JTable(tableModel);
//         table.setRowHeight(25);
//         JScrollPane scrollPane = new JScrollPane(table);
//         add(scrollPane, BorderLayout.CENTER);

//         // ---- FORM PANEL (ADD NEW SECTION) ----
//         JPanel formPanel = new JPanel(new GridBagLayout());
//         formPanel.setBorder(BorderFactory.createTitledBorder("Add / Manage Sections"));
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(5, 5, 5, 5);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         List<Course> courses = courseService.getAllCourses();
//         JComboBox<CourseItem> courseCombo = new JComboBox<>();
//         for (Course course : courses) courseCombo.addItem(new CourseItem(course));

//         List<Instructor> instructors = instructorStore.findAll();
//         JComboBox<InstructorItem> instructorCombo = new JComboBox<>();
//         for (Instructor instructor : instructors) instructorCombo.addItem(new InstructorItem(instructor));

//         JTextField dayTimeField = new JTextField(15);
//         JTextField roomField = new JTextField(10);
//         JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 500, 1));
//         String[] semesters = {"Spring", "Fall", "Summer"};
//         JComboBox<String> semesterCombo = new JComboBox<>(semesters);
//         JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2020, 2030, 1));

//         int row = 0;
//         addFormField(formPanel, gbc, row++, "Course:", courseCombo);
//         addFormField(formPanel, gbc, row++, "Instructor:", instructorCombo);
//         addFormField(formPanel, gbc, row++, "Day/Time:", dayTimeField);
//         addFormField(formPanel, gbc, row++, "Room:", roomField);
//         addFormField(formPanel, gbc, row++, "Capacity:", capacitySpinner);
//         addFormField(formPanel, gbc, row++, "Semester:", semesterCombo);
//         addFormField(formPanel, gbc, row++, "Year:", yearSpinner);

//         // Add Section Button
//         JButton addButton = new JButton("Add Section");
//         addButton.setBackground(new Color(76, 175, 80));
//         addButton.setForeground(Color.WHITE);
//         addButton.addActionListener(e -> {
//             if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
//                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//                         "Please select course and instructor.");
//                 return;
//             }

//             CourseItem selectedCourse = (CourseItem) courseCombo.getSelectedItem();
//             InstructorItem selectedInstructor = (InstructorItem) instructorCombo.getSelectedItem();
//             String dayTime = dayTimeField.getText().trim();
//             String room = roomField.getText().trim();
//             int capacity = (int) capacitySpinner.getValue();
//             String semester = (String) semesterCombo.getSelectedItem();
//             int year = (int) yearSpinner.getValue();

//             String validationError = ValidationHelper.validateSectionData(dayTime, room, capacity);
//             if (validationError != null) {
//                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
//                 return;
//             }

//             String error = courseService.createSection(
//                     selectedCourse.course.getCourseId(),
//                     selectedInstructor.instructor.getUserId(),
//                     dayTime, room, capacity, semester, year);

//             if (error == null) {
//                 MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this),
//                         "Section created successfully!");
//                 dayTimeField.setText("");
//                 roomField.setText("");
//                 capacitySpinner.setValue(30);
//                 loadSections();
//             } else {
//                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
//             }
//         });
//         gbc.gridx = 1;
//         gbc.gridy = row;
//         gbc.insets = new Insets(15, 5, 5, 5);
//         formPanel.add(addButton, gbc);

//         JButton refreshButton = new JButton("Refresh");
//         refreshButton.addActionListener(e -> loadSections());
//         gbc.gridx = 2;
//         formPanel.add(refreshButton, gbc);

//         // ---- EDIT BUTTON ----
//         // JButton editButton = new JButton("Edit Selected");
//         // editButton.setBackground(new Color(33, 150, 243));
//         // editButton.setForeground(Color.WHITE);
//         // editButton.addActionListener(e -> {
//         //     int selectedRow = table.getSelectedRow();
//         //     if (selectedRow == -1) {
//         //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//         //                 "Please select a section to edit.");
//         //         return;
//         //     }

//         //     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
//         //     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
//         //     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
//         //     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
//         //     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
//         //     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

//         //     String newDayTime = JOptionPane.showInputDialog(this, "Enter new Day/Time:", currentDayTime);
//         //     if (newDayTime == null) return;
//         //     String newRoom = JOptionPane.showInputDialog(this, "Enter new Room:", currentRoom);
//         //     if (newRoom == null) return;
//         //     String newCapacityStr = JOptionPane.showInputDialog(this, "Enter new Capacity:", currentCapacity);
//         //     if (newCapacityStr == null) return;
//         //     String newSemester = JOptionPane.showInputDialog(this, "Enter new Semester:", currentSemester);
//         //     if (newSemester == null) return;
//         //     String newYearStr = JOptionPane.showInputDialog(this, "Enter new Year:", currentYear);
//         //     if (newYearStr == null) return;

//         //     try {
//         //         int newCapacity = Integer.parseInt(newCapacityStr.trim());
//         //         int newYear = Integer.parseInt(newYearStr.trim());

//         //         String result = adminService.updateSection(sectionId, newDayTime.trim(), newRoom.trim(),
//         //                 newCapacity, newSemester.trim(), newYear);
//         //         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
//         //         loadSections();
//         //     } catch (NumberFormatException ex) {
//         //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Invalid numeric value.");
//         //     }
//         // });
//         // gbc.gridx = 3;
//         // formPanel.add(editButton, gbc);
//         // ---- EDIT BUTTON ----
// JButton editButton = new JButton("Edit Selected");
// editButton.setBackground(new Color(33, 150, 243));
// editButton.setForeground(Color.WHITE);
// editButton.addActionListener(e -> {
//     int selectedRow = table.getSelectedRow();
//     if (selectedRow == -1) {
//         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//                 "Please select a section to edit.");
//         return;
//     }

//     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
//     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
//     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
//     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
//     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
//     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

//     // ✅ Use the instance field directly
//     List<Instructor> instructorsList = this.instructorStore.findAll();
//     JComboBox<InstructorItem> instructorDropdown = new JComboBox<>();
//     for (Instructor inst : instructorsList) {
//         instructorDropdown.addItem(new InstructorItem(inst));
//         // Optional: preselect the current instructor
//         if (inst.getUserId() == Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString())) {
//             instructorDropdown.setSelectedItem(new InstructorItem(inst));
//         }
//     }

//     int result = JOptionPane.showConfirmDialog(this, instructorDropdown,
//             "Select new instructor (or cancel to keep same)", JOptionPane.OK_CANCEL_OPTION);
//     if (result != JOptionPane.OK_OPTION) return;

//     InstructorItem selectedInstructor = (InstructorItem) instructorDropdown.getSelectedItem();
//     int newInstructorId = selectedInstructor != null
//             ? selectedInstructor.instructor.getUserId()
//             : Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());

//     // --- get other field updates ---
//     String newDayTime = JOptionPane.showInputDialog(this, "Enter new Day/Time:", currentDayTime);
//     if (newDayTime == null) return;
//     String newRoom = JOptionPane.showInputDialog(this, "Enter new Room:", currentRoom);
//     if (newRoom == null) return;
//     String newCapacityStr = JOptionPane.showInputDialog(this, "Enter new Capacity:", currentCapacity);
//     if (newCapacityStr == null) return;
//     String newSemester = JOptionPane.showInputDialog(this, "Enter new Semester:", currentSemester);
//     if (newSemester == null) return;
//     String newYearStr = JOptionPane.showInputDialog(this, "Enter new Year:", currentYear);
//     if (newYearStr == null) return;

//     try {
//         int newCapacity = Integer.parseInt(newCapacityStr.trim());
//         int newYear = Integer.parseInt(newYearStr.trim());

//         String message = adminService.updateSection(sectionId, newInstructorId,
//                 newDayTime.trim(), newRoom.trim(), newCapacity,
//                 newSemester.trim(), newYear);
//         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), message);
//         loadSections();
//     } catch (NumberFormatException ex) {
//         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//                 "Invalid numeric value for capacity or year.");
//     }
// });
// formPanel.add(editButton);



//         // ---- DELETE BUTTON ----
//         JButton deleteButton = new JButton("Delete Selected");
//         deleteButton.setBackground(new Color(244, 67, 54));
//         deleteButton.setForeground(Color.WHITE);
//         deleteButton.addActionListener(e -> {
//             int selectedRow = table.getSelectedRow();
//             if (selectedRow == -1) {
//                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//                         "Please select a section to delete.");
//                 return;
//             }

//             int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
//             int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this section?",
//                     "Confirm Delete", JOptionPane.YES_NO_OPTION);

//             if (confirm == JOptionPane.YES_OPTION) {
//                 String result = adminService.deleteSection(sectionId);
//                 MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
//                 loadSections();
//             }
//         });
//         gbc.gridx = 4;
//         formPanel.add(deleteButton, gbc);

//         add(formPanel, BorderLayout.SOUTH);
//         loadSections();
//     }

//     private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         panel.add(new JLabel(labelText), gbc);

//         gbc.gridx = 1;
//         gbc.gridwidth = 2;
//         panel.add(field, gbc);
//         gbc.gridwidth = 1;
//     }

//     private void loadSections() {
//         tableModel.setRowCount(0);
//         List<Section> sections = courseService.getAllSections();

//         for (Section section : sections) {
//             Object[] row = {
//                     section.getSectionId(),
//                     section.getCourseCode() + " - " + section.getCourseTitle(),
//                     section.getInstructorId(),
//                     section.getDayTime(),
//                     section.getRoom(),
//                     section.getEnrolled(),
//                     section.getCapacity(),
//                     section.getSemester(),
//                     section.getYear()
//             };
//             tableModel.addRow(row);
//         }
//     }

//     private static class CourseItem {
//         Course course;
//         CourseItem(Course course) { this.course = course; }
//         @Override
//         public String toString() { return course.getCode() + " - " + course.getTitle(); }
//     }

//     private static class InstructorItem {
//         Instructor instructor;
//         InstructorItem(Instructor instructor) { this.instructor = instructor; }
//         @Override
//         public String toString() { return "Instructor ID: " + instructor.getUserId(); }
//     }
// }
package edu.univ.erp.ui.admin;

import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
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
    private final AdminService adminService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ManageSectionsPanel() {
        this.courseService = new CourseService();
        this.instructorStore = new InstructorStore();
        this.adminService = new AdminService();

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

        // ---- FORM PANEL (ADD NEW SECTION) ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Section"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Course> courses = courseService.getAllCourses();
        JComboBox<CourseItem> courseCombo = new JComboBox<>();
        for (Course course : courses) courseCombo.addItem(new CourseItem(course));

        List<Instructor> instructors = instructorStore.findAll();
        JComboBox<InstructorItem> instructorCombo = new JComboBox<>();
        for (Instructor instructor : instructors) instructorCombo.addItem(new InstructorItem(instructor));

        JTextField dayTimeField = new JTextField(15);
        JTextField roomField = new JTextField(10);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 500, 1));
        String[] semesters = {"Monsoon", "Winter", "Summer"};
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

        add(formPanel, BorderLayout.NORTH);

        // ---- BUTTONS PANEL ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Add Section
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

            String error = courseService.createSection(
                    selectedCourse.course.getCourseId(),
                    selectedInstructor.instructor.getUserId(),
                    dayTime, room, capacity, semester, year);

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
        buttonPanel.add(addButton);

        // ---- EDIT BUTTON ----
        JButton editButton = new JButton("Edit Selected");
        editButton.setBackground(new Color(33, 150, 243));
        editButton.setForeground(Color.WHITE);
        // editButton.addActionListener(e -> {
        //     int selectedRow = table.getSelectedRow();
        //     if (selectedRow == -1) {
        //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
        //                 "Please select a section to edit.");
        //         return;
        //     }

        //     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        //     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
        //     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
        //     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
        //     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
        //     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

        //         return;
        //     }

        //     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        //     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
        //     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
        //     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
        //     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
        //     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

        //     // --- Select new course ---
        //     List<Course> coursesList = courseService.getAllCourses();
        //     JComboBox<CourseItem> courseDropdown = new JComboBox<>();
        //     for (Course c : coursesList)
        //         courseDropdown.addItem(new CourseItem(c));
        //     JOptionPane.showMessageDialog(this, courseDropdown, "Select new course (or keep same)", JOptionPane.PLAIN_MESSAGE);
        //     CourseItem selectedCourse = (CourseItem) courseDropdown.getSelectedItem();

        //     // --- Select new instructor ---
        //     List<Instructor> instructorsList = instructorStore.findAll();
        //     JComboBox<InstructorItem> instructorDropdown = new JComboBox<>();
        //     for (Instructor inst : instructorsList)
        //         instructorDropdown.addItem(new InstructorItem(inst));
        //     JOptionPane.showMessageDialog(this, instructorDropdown, "Select new instructor (or keep same)", JOptionPane.PLAIN_MESSAGE);
        //     InstructorItem selectedInstructor = (InstructorItem) instructorDropdown.getSelectedItem();

        //     int newCourseId = selectedCourse != null ? selectedCourse.course.getCourseId() : -1;
        //     int newInstructorId = selectedInstructor != null ? selectedInstructor.instructor.getUserId()
        //             : Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());

        //     // --- Get other field updates ---
        //     String newDayTime = JOptionPane.showInputDialog(this, "Enter new Day/Time:", currentDayTime);
        //     if (newDayTime == null) return;
        //     String newRoom = JOptionPane.showInputDialog(this, "Enter new Room:", currentRoom);
        //     if (newRoom == null) return;
        //     String newCapacityStr = JOptionPane.showInputDialog(this, "Enter new Capacity:", currentCapacity);
        //     if (newCapacityStr == null) return;
        //     String newSemester = JOptionPane.showInputDialog(this, "Enter new Semester:", currentSemester);
        //     if (newSemester == null) return;
        //     String newYearStr = JOptionPane.showInputDialog(this, "Enter new Year:", currentYear);
        //     if (newYearStr == null) return;

        //     try {
        //         int newCapacity = Integer.parseInt(newCapacityStr.trim());
        //         int newYear = Integer.parseInt(newYearStr.trim());

        //         String message = adminService.updateSection(sectionId, newCourseId, newInstructorId,
        //                 newDayTime.trim(), newRoom.trim(), newCapacity,
        //                 newSemester.trim(), newYear);
        //         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), message);
        //         loadSections();
        //     } catch (NumberFormatException ex) {
        //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
        //                 "Invalid numeric value for capacity or year.");
        //     }
        // });
        

        editButton.addActionListener(e -> {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
                "Please select a section to edit.");
        return;
    }

    int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
    String currentCourseText = tableModel.getValueAt(selectedRow, 1).toString();
    int currentInstructorId = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
    String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
    String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
    String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
    String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
    String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

    // --- Select new course (preselect current one) ---
    List<Course> coursesList = courseService.getAllCourses();
    JComboBox<CourseItem> courseDropdown = new JComboBox<>();
    for (Course c : coursesList) {
        CourseItem item = new CourseItem(c);
        courseDropdown.addItem(item);
        if (currentCourseText.startsWith(c.getCode())) {
            courseDropdown.setSelectedItem(item);
        }
    }
    JOptionPane.showMessageDialog(this, courseDropdown, "Select new course (or keep same)", JOptionPane.PLAIN_MESSAGE);
    CourseItem selectedCourse = (CourseItem) courseDropdown.getSelectedItem();

    // --- Select new instructor (preselect current one) ---
    List<Instructor> instructorsList = instructorStore.findAll();
    JComboBox<InstructorItem> instructorDropdown = new JComboBox<>();
    for (Instructor inst : instructorsList) {
        InstructorItem item = new InstructorItem(inst);
        instructorDropdown.addItem(item);
        if (inst.getUserId() == currentInstructorId) {
            instructorDropdown.setSelectedItem(item);
        }
    }
    JOptionPane.showMessageDialog(this, instructorDropdown, "Select new instructor (or keep same)", JOptionPane.PLAIN_MESSAGE);
    InstructorItem selectedInstructor = (InstructorItem) instructorDropdown.getSelectedItem();

    int newCourseId = selectedCourse != null ? selectedCourse.course.getCourseId() : -1;
    int newInstructorId = selectedInstructor != null ? selectedInstructor.instructor.getUserId() : currentInstructorId;

    // --- Get text field updates ---
    String newDayTime = JOptionPane.showInputDialog(this, "Enter new Day/Time:", currentDayTime);
    if (newDayTime == null) return;
    String newRoom = JOptionPane.showInputDialog(this, "Enter new Room:", currentRoom);
    if (newRoom == null) return;
    String newCapacityStr = JOptionPane.showInputDialog(this, "Enter new Capacity:", currentCapacity);
    if (newCapacityStr == null) return;
    String newSemester = JOptionPane.showInputDialog(this, "Enter new Semester:", currentSemester);
    if (newSemester == null) return;
    String newYearStr = JOptionPane.showInputDialog(this, "Enter new Year:", currentYear);
    if (newYearStr == null) return;

    try {
        int newCapacity = Integer.parseInt(newCapacityStr.trim());
        int newYear = Integer.parseInt(newYearStr.trim());

        String message = adminService.updateSection(sectionId, newCourseId, newInstructorId,
                newDayTime.trim(), newRoom.trim(), newCapacity, newSemester.trim(), newYear);
        MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), message);
        loadSections();
    } catch (NumberFormatException ex) {
        MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
                "Invalid numeric value for capacity or year.");
    }
});
        buttonPanel.add(editButton);

        // ---- DELETE BUTTON ----
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
                        "Please select a section to delete.");
                return;
            }

            int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this section?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = adminService.deleteSection(sectionId);
                MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
                loadSections();
            }
        });
        buttonPanel.add(deleteButton);

        // ---- REFRESH BUTTON ----
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSections());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

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
