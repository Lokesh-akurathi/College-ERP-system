// // package edu.univ.erp.ui.admin;

// // import edu.univ.erp.data.InstructorStore;
// // import edu.univ.erp.domain.Course;
// // import edu.univ.erp.domain.Instructor;
// // import edu.univ.erp.domain.Section;
// // import edu.univ.erp.service.CourseService;
// // import edu.univ.erp.service.AdminService;
// // import edu.univ.erp.ui.common.MessageDialog;
// // import edu.univ.erp.util.ValidationHelper;

// // import javax.swing.*;
// // import javax.swing.table.DefaultTableModel;
// // import java.awt.*;
// // import java.util.List;

// // public class ManageSectionsPanel extends JPanel {
// //     private final CourseService courseService;
// //     private final InstructorStore instructorStore;
// //     private final AdminService adminService;
// //     private final JTable table;
// //     private final DefaultTableModel tableModel;

// //     public ManageSectionsPanel() {
// //         this.courseService = new CourseService();
// //         this.instructorStore = new InstructorStore();
// //         this.adminService = new AdminService();

// //         setLayout(new BorderLayout());

// //         JLabel titleLabel = new JLabel("Manage Sections");
// //         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
// //         titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
// //         add(titleLabel, BorderLayout.NORTH);

// //         String[] columns = {"Section ID", "Course", "Instructor ID", "Day/Time", "Room",
// //                 "Enrolled", "Capacity", "Semester", "Year"};
// //         tableModel = new DefaultTableModel(columns, 0) {
// //             @Override
// //             public boolean isCellEditable(int row, int column) {
// //                 return false;
// //             }
// //         };
// //         table = new JTable(tableModel);
// //         table.setRowHeight(25);
// //         JScrollPane scrollPane = new JScrollPane(table);
// //         add(scrollPane, BorderLayout.CENTER);

// //         // ---- FORM PANEL (ADD NEW SECTION) ----
// //         JPanel formPanel = new JPanel(new GridBagLayout());
// //         formPanel.setBorder(BorderFactory.createTitledBorder("Add / Manage Sections"));
// //         GridBagConstraints gbc = new GridBagConstraints();
// //         gbc.insets = new Insets(5, 5, 5, 5);
// //         gbc.fill = GridBagConstraints.HORIZONTAL;

// //         List<Course> courses = courseService.getAllCourses();
// //         JComboBox<CourseItem> courseCombo = new JComboBox<>();
// //         for (Course course : courses) courseCombo.addItem(new CourseItem(course));

// //         List<Instructor> instructors = instructorStore.findAll();
// //         JComboBox<InstructorItem> instructorCombo = new JComboBox<>();
// //         for (Instructor instructor : instructors) instructorCombo.addItem(new InstructorItem(instructor));

// //         JTextField dayTimeField = new JTextField(15);
// //         JTextField roomField = new JTextField(10);
// //         JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 500, 1));
// //         String[] semesters = {"Spring", "Fall", "Summer"};
// //         JComboBox<String> semesterCombo = new JComboBox<>(semesters);
// //         JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2020, 2030, 1));

// //         int row = 0;
// //         addFormField(formPanel, gbc, row++, "Course:", courseCombo);
// //         addFormField(formPanel, gbc, row++, "Instructor:", instructorCombo);
// //         addFormField(formPanel, gbc, row++, "Day/Time:", dayTimeField);
// //         addFormField(formPanel, gbc, row++, "Room:", roomField);
// //         addFormField(formPanel, gbc, row++, "Capacity:", capacitySpinner);
// //         addFormField(formPanel, gbc, row++, "Semester:", semesterCombo);
// //         addFormField(formPanel, gbc, row++, "Year:", yearSpinner);

// //         // Add Section Button
// //         JButton addButton = new JButton("Add Section");
// //         addButton.setBackground(new Color(76, 175, 80));
// //         addButton.setForeground(Color.WHITE);
// //         addButton.addActionListener(e -> {
// //             if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
// //                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
// //                         "Please select course and instructor.");
// //                 return;
// //             }

// //             CourseItem selectedCourse = (CourseItem) courseCombo.getSelectedItem();
// //             InstructorItem selectedInstructor = (InstructorItem) instructorCombo.getSelectedItem();
// //             String dayTime = dayTimeField.getText().trim();
// //             String room = roomField.getText().trim();
// //             int capacity = (int) capacitySpinner.getValue();
// //             String semester = (String) semesterCombo.getSelectedItem();
// //             int year = (int) yearSpinner.getValue();

// //             String validationError = ValidationHelper.validateSectionData(dayTime, room, capacity);
// //             if (validationError != null) {
// //                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
// //                 return;
// //             }

// //             String error = courseService.createSection(
// //                     selectedCourse.course.getCourseId(),
// //                     selectedInstructor.instructor.getUserId(),
// //                     dayTime, room, capacity, semester, year);

// //             if (error == null) {
// //                 MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this),
// //                         "Section created successfully!");
// //                 dayTimeField.setText("");
// //                 roomField.setText("");
// //                 capacitySpinner.setValue(30);
// //                 loadSections();
// //             } else {
// //                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
// //             }
// //         });
// //         gbc.gridx = 1;
// //         gbc.gridy = row;
// //         gbc.insets = new Insets(15, 5, 5, 5);
// //         formPanel.add(addButton, gbc);

// //         JButton refreshButton = new JButton("Refresh");
// //         refreshButton.addActionListener(e -> loadSections());
// //         gbc.gridx = 2;
// //         formPanel.add(refreshButton, gbc);

// //         // ---- EDIT BUTTON ----
// //         // JButton editButton = new JButton("Edit Selected");
// //         // editButton.setBackground(new Color(33, 150, 243));
// //         // editButton.setForeground(Color.WHITE);
// //         // editButton.addActionListener(e -> {
// //         //     int selectedRow = table.getSelectedRow();
// //         //     if (selectedRow == -1) {
// //         //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
// //         //                 "Please select a section to edit.");
// //         //         return;
// //         //     }

// //         //     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
// //         //     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
// //         //     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
// //         //     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
// //         //     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
// //         //     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

// //         //     String newDayTime = JOptionPane.showInputDialog(this, "Enter new Day/Time:", currentDayTime);
// //         //     if (newDayTime == null) return;
// //         //     String newRoom = JOptionPane.showInputDialog(this, "Enter new Room:", currentRoom);
// //         //     if (newRoom == null) return;
// //         //     String newCapacityStr = JOptionPane.showInputDialog(this, "Enter new Capacity:", currentCapacity);
// //         //     if (newCapacityStr == null) return;
// //         //     String newSemester = JOptionPane.showInputDialog(this, "Enter new Semester:", currentSemester);
// //         //     if (newSemester == null) return;
// //         //     String newYearStr = JOptionPane.showInputDialog(this, "Enter new Year:", currentYear);
// //         //     if (newYearStr == null) return;

// //         //     try {
// //         //         int newCapacity = Integer.parseInt(newCapacityStr.trim());
// //         //         int newYear = Integer.parseInt(newYearStr.trim());

// //         //         String result = adminService.updateSection(sectionId, newDayTime.trim(), newRoom.trim(),
// //         //                 newCapacity, newSemester.trim(), newYear);
// //         //         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
// //         //         loadSections();
// //         //     } catch (NumberFormatException ex) {
// //         //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Invalid numeric value.");
// //         //     }
// //         // });
// //         // gbc.gridx = 3;
// //         // formPanel.add(editButton, gbc);
// //         // ---- EDIT BUTTON ----
// // JButton editButton = new JButton("Edit Selected");
// // editButton.setBackground(new Color(33, 150, 243));
// // editButton.setForeground(Color.WHITE);
// // editButton.addActionListener(e -> {
// //     int selectedRow = table.getSelectedRow();
// //     if (selectedRow == -1) {
// //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
// //                 "Please select a section to edit.");
// //         return;
// //     }

// //     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
// //     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
// //     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
// //     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
// //     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
// //     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

// //     // ✅ Use the instance field directly
// //     List<Instructor> instructorsList = this.instructorStore.findAll();
// //     JComboBox<InstructorItem> instructorDropdown = new JComboBox<>();
// //     for (Instructor inst : instructorsList) {
// //         instructorDropdown.addItem(new InstructorItem(inst));
// //         // Optional: preselect the current instructor
// //         if (inst.getUserId() == Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString())) {
// //             instructorDropdown.setSelectedItem(new InstructorItem(inst));
// //         }
// //     }

// //     int result = JOptionPane.showConfirmDialog(this, instructorDropdown,
// //             "Select new instructor (or cancel to keep same)", JOptionPane.OK_CANCEL_OPTION);
// //     if (result != JOptionPane.OK_OPTION) return;

// //     InstructorItem selectedInstructor = (InstructorItem) instructorDropdown.getSelectedItem();
// //     int newInstructorId = selectedInstructor != null
// //             ? selectedInstructor.instructor.getUserId()
// //             : Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());

// //     // --- get other field updates ---
// //     String newDayTime = JOptionPane.showInputDialog(this, "Enter new Day/Time:", currentDayTime);
// //     if (newDayTime == null) return;
// //     String newRoom = JOptionPane.showInputDialog(this, "Enter new Room:", currentRoom);
// //     if (newRoom == null) return;
// //     String newCapacityStr = JOptionPane.showInputDialog(this, "Enter new Capacity:", currentCapacity);
// //     if (newCapacityStr == null) return;
// //     String newSemester = JOptionPane.showInputDialog(this, "Enter new Semester:", currentSemester);
// //     if (newSemester == null) return;
// //     String newYearStr = JOptionPane.showInputDialog(this, "Enter new Year:", currentYear);
// //     if (newYearStr == null) return;

// //     try {
// //         int newCapacity = Integer.parseInt(newCapacityStr.trim());
// //         int newYear = Integer.parseInt(newYearStr.trim());

// //         String message = adminService.updateSection(sectionId, newInstructorId,
// //                 newDayTime.trim(), newRoom.trim(), newCapacity,
// //                 newSemester.trim(), newYear);
// //         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), message);
// //         loadSections();
// //     } catch (NumberFormatException ex) {
// //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
// //                 "Invalid numeric value for capacity or year.");
// //     }
// // });
// // formPanel.add(editButton);



// //         // ---- DELETE BUTTON ----
// //         JButton deleteButton = new JButton("Delete Selected");
// //         deleteButton.setBackground(new Color(244, 67, 54));
// //         deleteButton.setForeground(Color.WHITE);
// //         deleteButton.addActionListener(e -> {
// //             int selectedRow = table.getSelectedRow();
// //             if (selectedRow == -1) {
// //                 MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
// //                         "Please select a section to delete.");
// //                 return;
// //             }

// //             int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
// //             int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this section?",
// //                     "Confirm Delete", JOptionPane.YES_NO_OPTION);

// //             if (confirm == JOptionPane.YES_OPTION) {
// //                 String result = adminService.deleteSection(sectionId);
// //                 MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
// //                 loadSections();
// //             }
// //         });
// //         gbc.gridx = 4;
// //         formPanel.add(deleteButton, gbc);

// //         add(formPanel, BorderLayout.SOUTH);
// //         loadSections();
// //     }

// //     private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
// //         gbc.gridx = 0;
// //         gbc.gridy = row;
// //         panel.add(new JLabel(labelText), gbc);

// //         gbc.gridx = 1;
// //         gbc.gridwidth = 2;
// //         panel.add(field, gbc);
// //         gbc.gridwidth = 1;
// //     }

// //     private void loadSections() {
// //         tableModel.setRowCount(0);
// //         List<Section> sections = courseService.getAllSections();

// //         for (Section section : sections) {
// //             Object[] row = {
// //                     section.getSectionId(),
// //                     section.getCourseCode() + " - " + section.getCourseTitle(),
// //                     section.getInstructorId(),
// //                     section.getDayTime(),
// //                     section.getRoom(),
// //                     section.getEnrolled(),
// //                     section.getCapacity(),
// //                     section.getSemester(),
// //                     section.getYear()
// //             };
// //             tableModel.addRow(row);
// //         }
// //     }

// //     private static class CourseItem {
// //         Course course;
// //         CourseItem(Course course) { this.course = course; }
// //         @Override
// //         public String toString() { return course.getCode() + " - " + course.getTitle(); }
// //     }

// //     private static class InstructorItem {
// //         Instructor instructor;
// //         InstructorItem(Instructor instructor) { this.instructor = instructor; }
// //         @Override
// //         public String toString() { return "Instructor ID: " + instructor.getUserId(); }
// //     }
// // }
// package edu.univ.erp.ui.admin;

// import edu.univ.erp.data.InstructorStore;
// import edu.univ.erp.domain.Course;
// import edu.univ.erp.domain.Instructor;
// import edu.univ.erp.domain.Section;
// import edu.univ.erp.service.AdminService;
// import edu.univ.erp.service.CourseService;
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
//         formPanel.setBorder(BorderFactory.createTitledBorder("Add New Section"));
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

//         add(formPanel, BorderLayout.NORTH);

//         // ---- BUTTONS PANEL ----
//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

//         // Add Section
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
//         buttonPanel.add(addButton);

//         // ---- EDIT BUTTON ----
//         JButton editButton = new JButton("Edit Selected");
//         editButton.setBackground(new Color(33, 150, 243));
//         editButton.setForeground(Color.WHITE);
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

//         //         return;
//         //     }

//         //     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
//         //     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
//         //     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
//         //     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
//         //     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
//         //     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

//         //     // --- Select new course ---
//         //     List<Course> coursesList = courseService.getAllCourses();
//         //     JComboBox<CourseItem> courseDropdown = new JComboBox<>();
//         //     for (Course c : coursesList)
//         //         courseDropdown.addItem(new CourseItem(c));
//         //     JOptionPane.showMessageDialog(this, courseDropdown, "Select new course (or keep same)", JOptionPane.PLAIN_MESSAGE);
//         //     CourseItem selectedCourse = (CourseItem) courseDropdown.getSelectedItem();

//         //     // --- Select new instructor ---
//         //     List<Instructor> instructorsList = instructorStore.findAll();
//         //     JComboBox<InstructorItem> instructorDropdown = new JComboBox<>();
//         //     for (Instructor inst : instructorsList)
//         //         instructorDropdown.addItem(new InstructorItem(inst));
//         //     JOptionPane.showMessageDialog(this, instructorDropdown, "Select new instructor (or keep same)", JOptionPane.PLAIN_MESSAGE);
//         //     InstructorItem selectedInstructor = (InstructorItem) instructorDropdown.getSelectedItem();

//         //     int newCourseId = selectedCourse != null ? selectedCourse.course.getCourseId() : -1;
//         //     int newInstructorId = selectedInstructor != null ? selectedInstructor.instructor.getUserId()
//         //             : Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());

//         //     // --- Get other field updates ---
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

//         //         String message = adminService.updateSection(sectionId, newCourseId, newInstructorId,
//         //                 newDayTime.trim(), newRoom.trim(), newCapacity,
//         //                 newSemester.trim(), newYear);
//         //         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), message);
//         //         loadSections();
//         //     } catch (NumberFormatException ex) {
//         //         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//         //                 "Invalid numeric value for capacity or year.");
//         //     }
//         // });
        

//         editButton.addActionListener(e -> {
//     int selectedRow = table.getSelectedRow();
//     if (selectedRow == -1) {
//         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//                 "Please select a section to edit.");
//         return;
//     }

//     int sectionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
//     String currentCourseText = tableModel.getValueAt(selectedRow, 1).toString();
//     int currentInstructorId = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
//     String currentDayTime = tableModel.getValueAt(selectedRow, 3).toString();
//     String currentRoom = tableModel.getValueAt(selectedRow, 4).toString();
//     String currentCapacity = tableModel.getValueAt(selectedRow, 6).toString();
//     String currentSemester = tableModel.getValueAt(selectedRow, 7).toString();
//     String currentYear = tableModel.getValueAt(selectedRow, 8).toString();

//     // --- Select new course (preselect current one) ---
//     List<Course> coursesList = courseService.getAllCourses();
//     JComboBox<CourseItem> courseDropdown = new JComboBox<>();
//     for (Course c : coursesList) {
//         CourseItem item = new CourseItem(c);
//         courseDropdown.addItem(item);
//         if (currentCourseText.startsWith(c.getCode())) {
//             courseDropdown.setSelectedItem(item);
//         }
//     }
//     JOptionPane.showMessageDialog(this, courseDropdown, "Select new course (or keep same)", JOptionPane.PLAIN_MESSAGE);
//     CourseItem selectedCourse = (CourseItem) courseDropdown.getSelectedItem();

//     // --- Select new instructor (preselect current one) ---
//     List<Instructor> instructorsList = instructorStore.findAll();
//     JComboBox<InstructorItem> instructorDropdown = new JComboBox<>();
//     for (Instructor inst : instructorsList) {
//         InstructorItem item = new InstructorItem(inst);
//         instructorDropdown.addItem(item);
//         if (inst.getUserId() == currentInstructorId) {
//             instructorDropdown.setSelectedItem(item);
//         }
//     }
//     JOptionPane.showMessageDialog(this, instructorDropdown, "Select new instructor (or keep same)", JOptionPane.PLAIN_MESSAGE);
//     InstructorItem selectedInstructor = (InstructorItem) instructorDropdown.getSelectedItem();

//     int newCourseId = selectedCourse != null ? selectedCourse.course.getCourseId() : -1;
//     int newInstructorId = selectedInstructor != null ? selectedInstructor.instructor.getUserId() : currentInstructorId;

//     // --- Get text field updates ---
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
//                 newDayTime.trim(), newRoom.trim(), newCapacity, newSemester.trim(), newYear);
//         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), message);
//         loadSections();
//     } catch (NumberFormatException ex) {
//         MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
//                 "Invalid numeric value for capacity or year.");
//     }
// });
//         buttonPanel.add(editButton);

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
//             int confirm = JOptionPane.showConfirmDialog(this,
//                     "Are you sure you want to delete this section?",
//                     "Confirm Delete", JOptionPane.YES_NO_OPTION);

//             if (confirm == JOptionPane.YES_OPTION) {
//                 String result = adminService.deleteSection(sectionId);
//                 MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
//                 loadSections();
//             }
//         });
//         buttonPanel.add(deleteButton);

//         // ---- REFRESH BUTTON ----
//         JButton refreshButton = new JButton("Refresh");
//         refreshButton.addActionListener(e -> loadSections());
//         buttonPanel.add(refreshButton);

//         add(buttonPanel, BorderLayout.SOUTH);

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

// package edu.univ.erp.ui.admin;

// import edu.univ.erp.data.InstructorStore;
// import edu.univ.erp.domain.Course;
// import edu.univ.erp.domain.Instructor;
// import edu.univ.erp.domain.Section;
// import edu.univ.erp.service.AdminService;
// import edu.univ.erp.service.CourseService;
// import edu.univ.erp.ui.common.MessageDialog;
// import edu.univ.erp.util.ValidationHelper;

// import javax.swing.*;
// import javax.swing.event.ListSelectionEvent;
// import javax.swing.event.ListSelectionListener;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.util.List;

// public class ManageSectionsPanel extends JPanel {
//     private final CourseService courseService;
//     private final InstructorStore instructorStore;
//     private final AdminService adminService;
//     private final JTable table;
//     private final DefaultTableModel tableModel;

//     // Form components (shared for add & update)
//     private final JComboBox<CourseItem> courseCombo;
//     private final JComboBox<InstructorItem> instructorCombo;
//     private final JTextField dayTimeField;
//     private final JTextField roomField;
//     private final JSpinner capacitySpinner;
//     private final JComboBox<String> semesterCombo;
//     private final JSpinner yearSpinner;

//     // Buttons
//     private final JButton addButton;
//     private final JButton updateButton;
//     private final JButton deleteButton;
//     private final JButton refreshButton;

//     // Currently selected section id for update (or -1 when none selected)
//     private int selectedSectionId = -1;

//     public ManageSectionsPanel() {
//         this.courseService = new CourseService();
//         this.instructorStore = new InstructorStore();
//         this.adminService = new AdminService();

//         setLayout(new BorderLayout(8, 8));

//         JLabel titleLabel = new JLabel("Manage Sections");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
//         add(titleLabel, BorderLayout.NORTH);

//         // Table setup
//         String[] columns = {"Section ID", "Course", "Instructor ID", "Day/Time", "Room",
//                 "Enrolled", "Capacity", "Semester", "Year"};
//         tableModel = new DefaultTableModel(columns, 0) {
//             @Override public boolean isCellEditable(int row, int column) { return false; }
//         };
//         table = new JTable(tableModel);
//         table.setRowHeight(24);
//         JScrollPane scrollPane = new JScrollPane(table);
//         add(scrollPane, BorderLayout.CENTER);

//         // Form panel (top)
//         JPanel formPanel = new JPanel(new GridBagLayout());
//         formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Section"));
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(6, 8, 6, 8);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         // Course Combo
//         List<Course> courses = courseService.getAllCourses();
//         courseCombo = new JComboBox<>();
//         for (Course c : courses) courseCombo.addItem(new CourseItem(c));

//         // Instructor Combo
//         List<Instructor> instructors = instructorStore.findAll();
//         instructorCombo = new JComboBox<>();
//         for (Instructor ins : instructors) instructorCombo.addItem(new InstructorItem(ins));

//         dayTimeField = new JTextField(18);
//         roomField = new JTextField(10);
//         capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));
//         String[] semesters = {"Spring", "Fall", "Summer"};
//         semesterCombo = new JComboBox<>(semesters);
//         yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2000, 2100, 1));

//         int row = 0;
//         addFormField(formPanel, gbc, row++, "Course:", courseCombo);
//         addFormField(formPanel, gbc, row++, "Instructor:", instructorCombo);
//         addFormField(formPanel, gbc, row++, "Day/Time:", dayTimeField);
//         addFormField(formPanel, gbc, row++, "Room:", roomField);
//         addFormField(formPanel, gbc, row++, "Capacity:", capacitySpinner);
//         addFormField(formPanel, gbc, row++, "Semester:", semesterCombo);
//         addFormField(formPanel, gbc, row++, "Year:", yearSpinner);

//         add(formPanel, BorderLayout.NORTH);

//         // Buttons panel (single horizontal line)
//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

//         addButton = new JButton("Add Section");
//         setButtonStyles(addButton, new Color(76, 175, 80));
//         addButton.addActionListener(e -> addSection());
//         buttonPanel.add(addButton);

//         updateButton = new JButton("Update Section");
//         setButtonStyles(updateButton, new Color(33, 150, 243));
//         updateButton.addActionListener(e -> updateSection());
//         updateButton.setEnabled(true); // disabled until a row is selected
//         buttonPanel.add(updateButton);

//         deleteButton = new JButton("Delete Selected");
//         setButtonStyles(deleteButton, new Color(244, 67, 54));
//         deleteButton.addActionListener(e -> deleteSelectedSection());
//         buttonPanel.add(deleteButton);

//         refreshButton = new JButton("Refresh");
//         refreshButton.addActionListener(e -> {
//             refreshFormData(); // refresh combo boxes
//             loadSections();
//         });
//         buttonPanel.add(refreshButton);

//         add(buttonPanel, BorderLayout.SOUTH);

//         // Table selection: populate form when a row is selected
//         table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//             @Override public void valueChanged(ListSelectionEvent e) {
//                 if (!e.getValueIsAdjusting()) {
//                     int sel = table.getSelectedRow();
//                     if (sel >= 0) {
//                         populateFormFromSelectedRow(sel);
//                     } else {
//                         clearFormSelection();
//                     }
//                 }
//             }
//         });

//         // initial load
//         refreshFormData();
//         loadSections();
//     }

//     private void setButtonStyles(JButton btn, Color bg) {
//         btn.setBackground(bg);
//         btn.setForeground(Color.WHITE);
//         btn.setFocusPainted(false);
//     }

//     private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         gbc.weightx = 0;
//         panel.add(new JLabel(labelText), gbc);

//         gbc.gridx = 1;
//         gbc.weightx = 1;
//         gbc.gridwidth = 2;
//         panel.add(field, gbc);
//         gbc.gridwidth = 1;
//     }

//     private void refreshFormData() {
//         // reload course and instructor lists (in case DB changed)
//         courseCombo.removeAllItems();
//         for (Course c : courseService.getAllCourses()) courseCombo.addItem(new CourseItem(c));

//         instructorCombo.removeAllItems();
//         for (Instructor i : instructorStore.findAll()) instructorCombo.addItem(new InstructorItem(i));
//     }

//     private void populateFormFromSelectedRow(int rowIndex) {
//         try {
//             selectedSectionId = Integer.parseInt(tableModel.getValueAt(rowIndex, 0).toString());
//         } catch (Exception ex) {
//             selectedSectionId = -1;
//         }

//         String courseText = tableModel.getValueAt(rowIndex, 1).toString();
//         int instructorId = Integer.parseInt(tableModel.getValueAt(rowIndex, 2).toString());
//         String dayTime = tableModel.getValueAt(rowIndex, 3).toString();
//         String room = tableModel.getValueAt(rowIndex, 4).toString();
//         String capacity = tableModel.getValueAt(rowIndex, 6).toString();
//         String semester = tableModel.getValueAt(rowIndex, 7).toString();
//         String year = tableModel.getValueAt(rowIndex, 8).toString();

//         // Set courseCombo selection by matching code prefix (course displayed as "CODE - TITLE")
//         boolean courseSelected = false;
//         for (int i = 0; i < courseCombo.getItemCount(); i++) {
//             CourseItem item = courseCombo.getItemAt(i);
//             if (courseText.startsWith(item.course.getCode())) {
//                 courseCombo.setSelectedIndex(i);
//                 courseSelected = true;
//                 break;
//             }
//         }
//         if (!courseSelected && courseCombo.getItemCount() > 0) courseCombo.setSelectedIndex(0);

//         // Set instructorCombo selection by id
//         boolean instSelected = false;
//         for (int i = 0; i < instructorCombo.getItemCount(); i++) {
//             InstructorItem it = instructorCombo.getItemAt(i);
//             if (it.instructor.getUserId() == instructorId) {
//                 instructorCombo.setSelectedIndex(i);
//                 instSelected = true;
//                 break;
//             }
//         }
//         if (!instSelected && instructorCombo.getItemCount() > 0) instructorCombo.setSelectedIndex(0);

//         dayTimeField.setText(dayTime);
//         roomField.setText(room);
//         try { capacitySpinner.setValue(Integer.parseInt(capacity)); } catch (Exception ex) { capacitySpinner.setValue(30); }
//         semesterCombo.setSelectedItem(semester);
//         try { yearSpinner.setValue(Integer.parseInt(year)); } catch (Exception ex) { yearSpinner.setValue(2025); }

//         // enable update button when a row is selected
//         updateButton.setEnabled(true);
//     }

//     private void clearFormSelection() {
//         selectedSectionId = -1;
//         dayTimeField.setText("");
//         roomField.setText("");
//         capacitySpinner.setValue(30);
//         semesterCombo.setSelectedIndex(0);
//         yearSpinner.setValue(2025);
//         // keep combos intact; leave current selections as-is or reset
//         updateButton.setEnabled(false);
//         table.clearSelection();
//     }

//     private void addSection() {
//         if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select course and instructor.");
//             return;
//         }
//         CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
//         InstructorItem ii = (InstructorItem) instructorCombo.getSelectedItem();

//         String dayTime = dayTimeField.getText().trim();
//         String room = roomField.getText().trim();
//         int capacity = (int) capacitySpinner.getValue();
//         String semester = (String) semesterCombo.getSelectedItem();
//         int year = (int) yearSpinner.getValue();

//         String validationError = ValidationHelper.validateSectionData(dayTime, room, capacity);
//         if (validationError != null) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
//             return;
//         }

//         String error = courseService.createSection(ci.course.getCourseId(), ii.instructor.getUserId(),
//                 dayTime, room, capacity, semester, year);

//         if (error == null) {
//             MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), "Section created successfully!");
//             clearFormFields();
//             loadSections();
//         } else {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
//         }
//     }

//     private void updateSection() {
//         if (selectedSectionId == -1) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select a section from the table to update.");
//             return;
//         }

//         if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select course and instructor.");
//             return;
//         }

//         CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
//         InstructorItem ii = (InstructorItem) instructorCombo.getSelectedItem();

//         String dayTime = dayTimeField.getText().trim();
//         String room = roomField.getText().trim();
//         int capacity = (int) capacitySpinner.getValue();
//         String semester = (String) semesterCombo.getSelectedItem();
//         int year = (int) yearSpinner.getValue();

//         String validationError = ValidationHelper.validateSectionData(dayTime, room, capacity);
//         if (validationError != null) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
//             return;
//         }

//         String result = adminService.updateSection(selectedSectionId,
//                 ci.course.getCourseId(), ii.instructor.getUserId(),
//                 dayTime, room, capacity, semester, year);

//         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
//         clearFormFields();
//         loadSections();
//         // After update, clear selection
//         clearFormSelection();
//     }

//     private void deleteSelectedSection() {
//         int sel = table.getSelectedRow();
//         if (sel == -1) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select a section to delete.");
//             return;
//         }

//         int sectionId;
//         try {
//             sectionId = Integer.parseInt(tableModel.getValueAt(sel, 0).toString());
//         } catch (Exception ex) {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Invalid section selected.");
//             return;
//         }

//         int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this section?",
//                 "Confirm Delete", JOptionPane.YES_NO_OPTION);
//         if (confirm != JOptionPane.YES_OPTION) return;

//         String res = adminService.deleteSection(sectionId);
//         MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), res);
//         loadSections();
//         clearFormSelection();
//     }

//     private void clearFormFields() {
//         // Do not clear combos (so user can keep selections), but clear text fields
//         dayTimeField.setText("");
//         roomField.setText("");
//         capacitySpinner.setValue(30);
//         semesterCombo.setSelectedIndex(0);
//         yearSpinner.setValue(2025);
//         selectedSectionId = -1;
//         updateButton.setEnabled(false);
//         table.clearSelection();
//     }

//     private void loadSections() {
//         tableModel.setRowCount(0);
//         List<Section> sections = courseService.getAllSections();
//         for (Section s : sections) {
//             Object[] row = {
//                     s.getSectionId(),
//                     s.getCourseCode() + " - " + s.getCourseTitle(),
//                     s.getInstructorId(),
//                     s.getDayTime(),
//                     s.getRoom(),
//                     s.getEnrolled(),
//                     s.getCapacity(),
//                     s.getSemester(),
//                     s.getYear()
//             };
//             tableModel.addRow(row);
//         }
//     }

//     private static class CourseItem {
//         Course course;
//         CourseItem(Course course) { this.course = course; }
//         @Override public String toString() { return course.getCode() + " - " + course.getTitle(); }
//     }

//     private static class InstructorItem {
//         Instructor instructor;
//         InstructorItem(Instructor instructor) { this.instructor = instructor; }
//         @Override public String toString() {
//             // show username if available otherwise id
//             if (instructor.getUsername() != null && !instructor.getUsername().isEmpty()) {
//                 return instructor.getUsername() + " (ID: " + instructor.getUserId() + ")";
//             }
//             return "Instructor ID: " + instructor.getUserId();
//         }
//     }
// }
package edu.univ.erp.ui.admin;

import edu.univ.erp.data.InstructorStore;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.SectionTime;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ManageSectionsPanel with structured SectionTime UI:
 * - Drop-down day, start, end (08:00 - 18:00, 30-min steps)
 * - Add multiple time slots
 * - Slot table with remove button
 * - Works for Add and Update (uses SectionTime list)
 */
public class ManageSectionsPanel extends JPanel {
    private final CourseService courseService;
    private final InstructorStore instructorStore;
    private final AdminService adminService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Form components (shared for add & update)
    private final JComboBox<CourseItem> courseCombo;
    private final JComboBox<InstructorItem> instructorCombo;
    private final JTextField roomField;
    private final JSpinner capacitySpinner;
    private final JComboBox<String> semesterCombo;
    private final JSpinner yearSpinner;

    // Time-slot inputs (single row for adding)
    private final JComboBox<String> daySlotCombo;
    private final JComboBox<String> startSlotCombo;
    private final JComboBox<String> endSlotCombo;
    private final DefaultTableModel slotsTableModel;
    private final JTable slotsTable;

    // Buttons
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton refreshButton;
    private final JButton addSlotButton;
    private final JButton removeSlotButton;

    // Currently selected section id for update (or -1 when none selected)
    private int selectedSectionId = -1;

    public ManageSectionsPanel() {
        this.courseService = new CourseService();
        this.instructorStore = new InstructorStore();
        this.adminService = new AdminService();

        setLayout(new BorderLayout(8, 8));

        JLabel titleLabel = new JLabel("Manage Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(titleLabel, BorderLayout.NORTH);

        // Main sections table
        String[] columns = {"Section ID", "Course", "Instructor ID", "Day/Time", "Room",
                "Enrolled", "Capacity", "Semester", "Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel (top)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Section"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Course combo
        courseCombo = new JComboBox<>();
        // Instructor combo
        instructorCombo = new JComboBox<>();

        // Other fields
        roomField = new JTextField(10);
        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));
        String[] semesters = {"Monsoon", "Winter", "Summer"};
        semesterCombo = new JComboBox<>(semesters);
        yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2000, 2100, 1));

        // time slot controls
        daySlotCombo = new JComboBox<>(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"});
        startSlotCombo = new JComboBox<>(generateTimeOptions());
        endSlotCombo = new JComboBox<>(generateTimeOptions());
        addSlotButton = new JButton("+ Add Slot");
        removeSlotButton = new JButton("Remove Selected Slot");

        // slots table
        String[] slotCols = {"Day", "Start", "End"};
        slotsTableModel = new DefaultTableModel(slotCols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        slotsTable = new JTable(slotsTableModel);
        slotsTable.setRowHeight(20);

        // Put fields into form panel
        int row = 0;
        addFormField(formPanel, gbc, row++, "Course:", courseCombo);
        addFormField(formPanel, gbc, row++, "Instructor:", instructorCombo);
        // room / capacity / semester / year
        addFormField(formPanel, gbc, row++, "Room:", roomField);
        addFormField(formPanel, gbc, row++, "Capacity:", capacitySpinner);
        addFormField(formPanel, gbc, row++, "Semester:", semesterCombo);
        addFormField(formPanel, gbc, row++, "Year:", yearSpinner);

        // Time slot label and controls just below year (inside same form)
        JPanel slotInputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        slotInputRow.add(new JLabel("Day:"));
        slotInputRow.add(daySlotCombo);
        slotInputRow.add(new JLabel("Start:"));
        slotInputRow.add(startSlotCombo);
        slotInputRow.add(new JLabel("End:"));
        slotInputRow.add(endSlotCombo);
        slotInputRow.add(addSlotButton);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        formPanel.add(slotInputRow, gbc);
        gbc.gridwidth = 1;

        // slots table (small, placed under the slot inputs)
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        JScrollPane slotScroll = new JScrollPane(slotsTable);
        slotScroll.setPreferredSize(new Dimension(600, 90));
        formPanel.add(slotScroll, gbc);
        gbc.gridwidth = 1;

        // remove slot button
        gbc.gridx = 0;
        gbc.gridy = row++;
        formPanel.add(removeSlotButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Buttons panel (single horizontal line)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        addButton = new JButton("Add Section");
        setButtonStyles(addButton, new Color(76, 175, 80));
        addButton.addActionListener(e -> addSection());
        buttonPanel.add(addButton);

        updateButton = new JButton("Update Section");
        setButtonStyles(updateButton, new Color(33, 150, 243));
        updateButton.addActionListener(e -> updateSection());
        updateButton.setEnabled(false); // enabled when row selected
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete Selected");
        setButtonStyles(deleteButton, new Color(244, 67, 54));
        deleteButton.addActionListener(e -> deleteSelectedSection());
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshFormData();
            loadSections();
        });
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // addSlot logic
        addSlotButton.addActionListener(e -> {
            String day = (String) daySlotCombo.getSelectedItem();
            String start = (String) startSlotCombo.getSelectedItem();
            String end = (String) endSlotCombo.getSelectedItem();

            if (day == null || start == null || end == null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Choose day, start and end.");
                return;
            }
            if (!isEndAfterStart(start, end)) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "End time must be after start time.");
                return;
            }

            // Add to slots table
            slotsTableModel.addRow(new Object[]{day, start, end});
        });

        // remove slot logic
        removeSlotButton.addActionListener(e -> {
            int sel = slotsTable.getSelectedRow();
            if (sel == -1) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Select a slot to remove.");
                return;
            }
            slotsTableModel.removeRow(sel);
        });

        // Table selection: populate form when a row is selected
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int sel = table.getSelectedRow();
                if (sel >= 0) populateFormFromSelectedRow(sel);
                else clearFormSelection();
            }
        });

        // initial load
        refreshFormData();
        loadSections();
    }

    /* ---------- Helpers ---------- */

    private void setButtonStyles(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
        gbc.gridwidth = 1;
    }

    private String[] generateTimeOptions() {
        List<String> times = new ArrayList<>();
        // 08:00 -> 18:00 inclusive, 30-min steps
        int startMinutes = 8 * 60;
        int endMinutes = 18 * 60;
        for (int t = startMinutes; t <= endMinutes; t += 30) {
            int hh = t / 60;
            int mm = t % 60;
            times.add(String.format("%02d:%02d", hh, mm));
        }
        return times.toArray(new String[0]);
    }

    private boolean isEndAfterStart(String start, String end) {
        try {
            String[] sp = start.split(":");
            String[] ep = end.split(":");
            int s = Integer.parseInt(sp[0]) * 60 + Integer.parseInt(sp[1]);
            int e = Integer.parseInt(ep[0]) * 60 + Integer.parseInt(ep[1]);
            return e > s;
        } catch (Exception ex) {
            return false;
        }
    }

    private void refreshFormData() {
        // reload course and instructor lists (in case DB changed)
        courseCombo.removeAllItems();
        for (Course c : courseService.getAllCourses()) courseCombo.addItem(new CourseItem(c));

        instructorCombo.removeAllItems();
        for (Instructor i : instructorStore.findAll()) instructorCombo.addItem(new InstructorItem(i));
    }

    private void populateFormFromSelectedRow(int rowIndex) {
        try {
            selectedSectionId = Integer.parseInt(tableModel.getValueAt(rowIndex, 0).toString());
        } catch (Exception ex) {
            selectedSectionId = -1;
        }

        String courseText = tableModel.getValueAt(rowIndex, 1).toString();
        int instructorId = Integer.parseInt(tableModel.getValueAt(rowIndex, 2).toString());
        String dayTime = tableModel.getValueAt(rowIndex, 3).toString(); // formatted
        String room = tableModel.getValueAt(rowIndex, 4).toString();
        String capacity = tableModel.getValueAt(rowIndex, 6).toString();
        String semester = tableModel.getValueAt(rowIndex, 7).toString();
        String year = tableModel.getValueAt(rowIndex, 8).toString();

        // set courseCombo by code prefix
        boolean courseSelected = false;
        for (int i = 0; i < courseCombo.getItemCount(); i++) {
            CourseItem item = courseCombo.getItemAt(i);
            if (courseText.startsWith(item.course.getCode())) {
                courseCombo.setSelectedIndex(i);
                courseSelected = true;
                break;
            }
        }
        if (!courseSelected && courseCombo.getItemCount() > 0) courseCombo.setSelectedIndex(0);

        // set instructorCombo selection by id
        boolean instSelected = false;
        for (int i = 0; i < instructorCombo.getItemCount(); i++) {
            InstructorItem it = instructorCombo.getItemAt(i);
            if (it.instructor.getUserId() == instructorId) {
                instructorCombo.setSelectedIndex(i);
                instSelected = true;
                break;
            }
        }
        if (!instSelected && instructorCombo.getItemCount() > 0) instructorCombo.setSelectedIndex(0);

        roomField.setText(room);
        try { capacitySpinner.setValue(Integer.parseInt(capacity)); } catch (Exception ex) { capacitySpinner.setValue(30); }
        semesterCombo.setSelectedItem(semester);
        try { yearSpinner.setValue(Integer.parseInt(year)); } catch (Exception ex) { yearSpinner.setValue(2025); }

        // Load structured times from Section object if available (preferred)
        slotsTableModel.setRowCount(0);
        Section s = courseService.getAllSections()
                .stream()
                .filter(x -> x.getSectionId() == selectedSectionId)
                .findFirst()
                .orElse(null);

        if (s != null && s.getTimes() != null && !s.getTimes().isEmpty()) {
            for (SectionTime st : s.getTimes()) {
                slotsTableModel.addRow(new Object[]{st.getDayOfWeek(), st.getStartTime(), st.getEndTime()});
            }
        } else {
            // fallback: attempt to parse/display dayTime string (if older sections still contain it)
            if (dayTime != null && !dayTime.isEmpty()) {
                // show the whole dayTime string as a single slot (user can re-add properly)
                slotsTableModel.addRow(new Object[]{"", dayTime, ""});
            }
        }

        updateButton.setEnabled(true);
    }

    private void clearFormSelection() {
        selectedSectionId = -1;
        roomField.setText("");
        capacitySpinner.setValue(30);
        semesterCombo.setSelectedIndex(0);
        yearSpinner.setValue(2025);
        slotsTableModel.setRowCount(0);
        updateButton.setEnabled(false);
        table.clearSelection();
    }

    private void addSection() {
        if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select course and instructor.");
            return;
        }
        CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
        InstructorItem ii = (InstructorItem) instructorCombo.getSelectedItem();

        String room = roomField.getText().trim();
        int capacity = (int) capacitySpinner.getValue();
        String semester = (String) semesterCombo.getSelectedItem();
        int year = (int) yearSpinner.getValue();

        // build SectionTime list from slots table
        List<SectionTime> times = getTimesFromSlotsTable();
        if (times.isEmpty()) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Add at least one meeting time slot.");
            return;
        }

        String validationError = ValidationHelper.validateSectionData(null, room, capacity); // day/time validated via slots
        if (validationError != null) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
            return;
        }

        String error = courseService.createSection(ci.course.getCourseId(), ii.instructor.getUserId(),
                times, room, capacity, semester, year);

        if (error == null) {
            MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), "Section created successfully!");
            clearFormFields();
            loadSections();
        } else {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
        }
    }

    private void updateSection() {
        if (selectedSectionId == -1) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select a section from the table to update.");
            return;
        }

        if (courseCombo.getSelectedItem() == null || instructorCombo.getSelectedItem() == null) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select course and instructor.");
            return;
        }

        CourseItem ci = (CourseItem) courseCombo.getSelectedItem();
        InstructorItem ii = (InstructorItem) instructorCombo.getSelectedItem();

        String room = roomField.getText().trim();
        int capacity = (int) capacitySpinner.getValue();
        String semester = (String) semesterCombo.getSelectedItem();
        int year = (int) yearSpinner.getValue();

        List<SectionTime> times = getTimesFromSlotsTable();
        if (times.isEmpty()) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Add at least one meeting time slot.");
            return;
        }

        String validationError = ValidationHelper.validateSectionData(null, room, capacity);
        if (validationError != null) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
            return;
        }

        String result = adminService.updateSection(selectedSectionId,
                ci.course.getCourseId(), ii.instructor.getUserId(),
                times, room, capacity, semester, year);

        MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), result);
        clearFormFields();
        loadSections();
        clearFormSelection();
    }

    private void deleteSelectedSection() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Please select a section to delete.");
            return;
        }

        int sectionId;
        try {
            sectionId = Integer.parseInt(tableModel.getValueAt(sel, 0).toString());
        } catch (Exception ex) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Invalid section selected.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this section?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String res = adminService.deleteSection(sectionId);
        MessageDialog.showInfo((JFrame) SwingUtilities.getWindowAncestor(this), res);
        loadSections();
        clearFormSelection();
    }

    private List<SectionTime> getTimesFromSlotsTable() {
        List<SectionTime> times = new ArrayList<>();
        for (int r = 0; r < slotsTableModel.getRowCount(); r++) {
            String day = String.valueOf(slotsTableModel.getValueAt(r, 0));
            String start = String.valueOf(slotsTableModel.getValueAt(r, 1));
            String end = String.valueOf(slotsTableModel.getValueAt(r, 2));
            if (day == null) day = "";
            if (start == null) start = "";
            if (end == null) end = "";
            // Only accept well-formed slots
            if (!start.isEmpty() && !end.isEmpty()) {
                times.add(new SectionTime(day, start, end));
            }
        }
        return times;
    }

    private void clearFormFields() {
        // keep combos so user can reuse selection if desired
        roomField.setText("");
        capacitySpinner.setValue(30);
        semesterCombo.setSelectedIndex(0);
        yearSpinner.setValue(2025);
        slotsTableModel.setRowCount(0);
        selectedSectionId = -1;
        updateButton.setEnabled(false);
        table.clearSelection();
    }

    private void loadSections() {
        tableModel.setRowCount(0);
        List<Section> sections = courseService.getAllSections();
        for (Section s : sections) {
            String timesText = "";
            if (s.getTimes() != null && !s.getTimes().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (SectionTime st : s.getTimes()) {
                    if (sb.length() > 0) sb.append("; ");
                    sb.append(st.getDayOfWeek()).append(" ").append(st.getStartTime()).append("-").append(st.getEndTime());
                }
                timesText = sb.toString();
            } else if (s.getDayTime() != null && !s.getDayTime().isEmpty()) {
                timesText = s.getDayTime();
            }
            Object[] row = {
                    s.getSectionId(),
                    s.getCourseCode() + " - " + s.getCourseTitle(),
                    s.getInstructorId(),
                    timesText,
                    s.getRoom(),
                    s.getEnrolled(),
                    s.getCapacity(),
                    s.getSemester(),
                    s.getYear()
            };
            tableModel.addRow(row);
        }
    }

    /* ---------- Simple lightweight value wrappers for Combos ---------- */
    private static class CourseItem {
        Course course;
        CourseItem(Course course) { this.course = course; }
        @Override public String toString() { return course.getCode() + " - " + course.getTitle(); }
    }

    private static class InstructorItem {
        Instructor instructor;
        InstructorItem(Instructor instructor) { this.instructor = instructor; }
        @Override public String toString() {
            if (instructor.getUsername() != null && !instructor.getUsername().isEmpty()) {
                return instructor.getUsername() + " (ID: " + instructor.getUserId() + ")";
            }
            return "Instructor ID: " + instructor.getUserId();
        }
    }
}
