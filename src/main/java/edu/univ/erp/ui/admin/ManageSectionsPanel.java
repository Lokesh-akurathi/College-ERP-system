
package edu.univ.erp.ui.admin;

import edu.univ.erp.service.InstructorService;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.SectionTime;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;
import edu.univ.erp.ui.ThemeConstants;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ManageSectionsPanel extends JPanel {
    private final CourseService courseService;
    private final InstructorService instructorService;
    private final AdminService adminService;
    private final JTable table;
    private final DefaultTableModel tableModel;

   
    private final JComboBox<CourseItem> courseCombo;
    private final JComboBox<InstructorItem> instructorCombo;
    private final JTextField roomField;
    private final JSpinner capacitySpinner;
    private final JComboBox<String> semesterCombo;
    private final JSpinner yearSpinner;

  
    private final JComboBox<String> daySlotCombo;
    private final JComboBox<String> startSlotCombo;
    private final JComboBox<String> endSlotCombo;
    private final DefaultTableModel slotsTableModel;
    private final JTable slotsTable;

    private final JButton addButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton refreshButton;
    private final JButton addSlotButton;
    private final JButton removeSlotButton;

  
    private int selectedSectionId = -1;

    public ManageSectionsPanel() {
        this.courseService = new CourseService();
        this.instructorService = new InstructorService();
        this.adminService = new AdminService();

        setLayout(new BorderLayout(8, 8));
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JLabel titleLabel = new JLabel("Manage Sections");
       titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
       titleLabel.setForeground(ThemeConstants.TEXT_DARK);

        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(titleLabel, BorderLayout.NORTH);

       
        String[] columns = {"Section ID", "Course", "Instructor", "Day/Time", "Room",
                "Enrolled", "Capacity", "Semester", "Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            c.setForeground(ThemeConstants.TEXT_LIGHT);
        } else {
            if (row % 2 == 0) {
                c.setBackground(new Color(240, 245, 250)); // light bluish
            } else {
                c.setBackground(Color.WHITE);
            }
            c.setForeground(ThemeConstants.TEXT_DARK);
        }
        return c;
    }
});

        table.getTableHeader().setBackground(ThemeConstants.PRIMARY_NAVY);
        table.getTableHeader().setForeground(ThemeConstants.TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        table.setSelectionForeground(ThemeConstants.TEXT_LIGHT);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

   
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Section"));
        formPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

      
        courseCombo = new JComboBox<>();
       
        instructorCombo = new JComboBox<>();

        roomField = new JTextField(10);
        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));
        String[] semesters = {"Monsoon", "Winter", "Summer"};
        semesterCombo = new JComboBox<>(semesters);
        yearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2000, 2100, 1));


        daySlotCombo = new JComboBox<>(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"});
        startSlotCombo = new JComboBox<>(generateTimeOptions());
        endSlotCombo = new JComboBox<>(generateTimeOptions());
        addSlotButton = new JButton("+ Add Slot");
        removeSlotButton = new JButton("Remove Selected Slot");


        String[] slotCols = {"Day", "Start", "End"};
        slotsTableModel = new DefaultTableModel(slotCols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        slotsTable = new JTable(slotsTableModel);
        slotsTable.setRowHeight(20);


        int row = 0;
        addFormField(formPanel, gbc, row++, "Course:", courseCombo);
        addFormField(formPanel, gbc, row++, "Instructor:", instructorCombo);
   
        addFormField(formPanel, gbc, row++, "Room:", roomField);
        addFormField(formPanel, gbc, row++, "Capacity:", capacitySpinner);
        addFormField(formPanel, gbc, row++, "Semester:", semesterCombo);
        addFormField(formPanel, gbc, row++, "Year:", yearSpinner);


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

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        JScrollPane slotScroll = new JScrollPane(slotsTable);
        slotScroll.setPreferredSize(new Dimension(600, 90));
        formPanel.add(slotScroll, gbc);
        gbc.gridwidth = 1;

  
        gbc.gridx = 0;
        gbc.gridy = row++;
        formPanel.add(removeSlotButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttonPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        addButton = new JButton("Add Section");
        setButtonStyles(addButton, new Color(76, 175, 80));
        addButton.addActionListener(e -> addSection());
        buttonPanel.add(addButton);

        updateButton = new JButton("Update Section");
        setButtonStyles(updateButton, new Color(33, 150, 243));
        updateButton.addActionListener(e -> updateSection());
        updateButton.setEnabled(false); 
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

           
            slotsTableModel.addRow(new Object[]{day, start, end});
        });

        
        removeSlotButton.addActionListener(e -> {
            int sel = slotsTable.getSelectedRow();
            if (sel == -1) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), "Select a slot to remove.");
                return;
            }
            slotsTableModel.removeRow(sel);
        });

        
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int sel = table.getSelectedRow();
                if (sel >= 0) populateFormFromSelectedRow(sel);
                else clearFormSelection();
            }
        });

        
        refreshFormData();
        loadSections();
    }



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
        
        courseCombo.removeAllItems();
        for (Course c : courseService.getAllCourses()) courseCombo.addItem(new CourseItem(c));

        instructorCombo.removeAllItems();
        for (Instructor i : instructorService.getAllInstructors()) instructorCombo.addItem(new InstructorItem(i));
    }

   private void populateFormFromSelectedRow(int rowIndex) {
        try {
            selectedSectionId = Integer.parseInt(tableModel.getValueAt(rowIndex, 0).toString());
        } catch (Exception ex) {
            selectedSectionId = -1;
        }
        
       
        Section sectionToPopulate = courseService.getAllSections()
                .stream()
                .filter(x -> x.getSectionId() == selectedSectionId)
                .findFirst()
                .orElse(null);

        if (sectionToPopulate == null) {
             clearFormSelection();
             return;
        }
        
        
        int instructorId = sectionToPopulate.getInstructorId(); 

        String courseText = tableModel.getValueAt(rowIndex, 1).toString();
       
        String room = tableModel.getValueAt(rowIndex, 4).toString();
        String capacity = tableModel.getValueAt(rowIndex, 6).toString();
        String semester = tableModel.getValueAt(rowIndex, 7).toString();
        String year = tableModel.getValueAt(rowIndex, 8).toString();

        
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

        
        slotsTableModel.setRowCount(0);
        
       
        
        if (sectionToPopulate.getTimes() != null && !sectionToPopulate.getTimes().isEmpty()) {
            for (SectionTime st : sectionToPopulate.getTimes()) {
                slotsTableModel.addRow(new Object[]{st.getDayOfWeek(), st.getStartTime(), st.getEndTime()});
            }
        } else if (sectionToPopulate.getDayTime() != null && !sectionToPopulate.getDayTime().isEmpty()) {
           
             slotsTableModel.addRow(new Object[]{"N/A", sectionToPopulate.getDayTime(), ""});
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
         
            if (!start.isEmpty() && !end.isEmpty()) {
                times.add(new SectionTime(day, start, end));
            }
        }
        return times;
    }

    private void clearFormFields() {

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
                    s.getInstructorName(),
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


    private static class CourseItem {
        Course course;
        CourseItem(Course course) { this.course = course; }
        @Override public String toString() { return course.getCode() + " - " + course.getTitle(); }
    }

    private static class InstructorItem {
        Instructor instructor;
        InstructorItem(Instructor instructor) { this.instructor = instructor; }
        @Override public String toString() {
            String fullName = instructor.getSalutation() + " " + instructor.getFirstName() + " " + instructor.getLastName();
            return fullName.trim();
        }
    }
}
