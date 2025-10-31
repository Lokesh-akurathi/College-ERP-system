package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Section;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MySectionsPanel extends JPanel {
    private final SectionStore sectionStore;

    public MySectionsPanel() {
        this.sectionStore = new SectionStore();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Section ID", "Course Code", "Course Title", "Day/Time", "Room", 
                           "Enrolled", "Capacity", "Semester", "Year"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int instructorId = SessionManager.getInstance().getCurrentUserId();
        List<Section> sections = sectionStore.findByInstructor(instructorId);
        
        for (Section section : sections) {
            Object[] row = {
                section.getSectionId(),
                section.getCourseCode(),
                section.getCourseTitle(),
                section.getDayTime(),
                section.getRoom(),
                section.getEnrolled(),
                section.getCapacity(),
                section.getSemester(),
                section.getYear()
            };
            tableModel.addRow(row);
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}
