// package edu.univ.erp.ui.student;

// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.service.EnrollmentService;
// import edu.univ.erp.ui.common.MessageDialog;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.util.List;

// public class MyRegistrationsPanel extends JPanel {
//     private final EnrollmentService enrollmentService;
//     private final JTable table;
//     private final DefaultTableModel tableModel;

//     public MyRegistrationsPanel() {
//         this.enrollmentService = new EnrollmentService();
        
//         setLayout(new BorderLayout());

//         JLabel titleLabel = new JLabel("My Registrations");
//         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
//         add(titleLabel, BorderLayout.NORTH);

//         String[] columns = {"Enrollment ID", "Code", "Title", "Credits", "Day/Time", "Room", "Instructor"};
//         tableModel = new DefaultTableModel(columns, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };
//         table = new JTable(tableModel);
//         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//         table.setRowHeight(25);
        
//         JScrollPane scrollPane = new JScrollPane(table);
//         add(scrollPane, BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
//         JButton dropButton = new JButton("Drop Selected Section");
//         dropButton.setBackground(new Color(244, 67, 54));
//         dropButton.setForeground(Color.WHITE);
//         dropButton.addActionListener(e -> dropSection());
//         buttonPanel.add(dropButton);

//         JButton refreshButton = new JButton("Refresh");
//         refreshButton.addActionListener(e -> loadEnrollments());
//         buttonPanel.add(refreshButton);

//         add(buttonPanel, BorderLayout.SOUTH);

//         loadEnrollments();
//     }

//     private void loadEnrollments() {
//         tableModel.setRowCount(0);
//         List<Enrollment> enrollments = enrollmentService.getMyEnrollments();
        
//         for (Enrollment enrollment : enrollments) {
//             Object[] row = {
//                 enrollment.getEnrollmentId(),
//                 enrollment.getCourseCode(),
//                 enrollment.getCourseTitle(),
//                 enrollment.getCourseCredits(),
//                 enrollment.getSectionDayTime(),
//                 enrollment.getSectionRoom(),
//                 enrollment.getInstructorName()
//             };
//             tableModel.addRow(row);
//         }
//     }

//     private void dropSection() {
//         int selectedRow = table.getSelectedRow();
//         if (selectedRow == -1) {
//             MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this), 
//                     "Please select a section to drop.");
//             return;
//         }

//         boolean confirmed = MessageDialog.showConfirm((JFrame) SwingUtilities.getWindowAncestor(this), 
//                 "Are you sure you want to drop this section?");
        
//         if (!confirmed) {
//             return;
//         }

//         int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);
//         String error = enrollmentService.dropSection(enrollmentId);
        
//         if (error == null) {
//             MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
//                     "Section dropped successfully!");
//             loadEnrollments();
//         } else {
//             MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
//         }
//     }
// }
package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyRegistrationsPanel extends JPanel {

    private final EnrollmentService enrollmentService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public MyRegistrationsPanel() {
        this.enrollmentService = new EnrollmentService();
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("My Registrations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Visible columns (NO enrollmentId here)
        String[] columns = {
                "Roll No",
                "Code",
                "Title",
                "Credits",
                "Day/Time",
                "Room"
        };

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

        JButton dropButton = new JButton("Drop Selected Section");
        dropButton.setBackground(new Color(244, 67, 54));
        dropButton.setForeground(Color.WHITE);
        dropButton.addActionListener(e -> dropSection());
        buttonPanel.add(dropButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadEnrollments());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadEnrollments();
    }

    private void loadEnrollments() {
        tableModel.setRowCount(0);
        
        List<Enrollment> enrollments = enrollmentService.getMyEnrollments();

        for (Enrollment enrollment : enrollments) {
            Object[] row = {
                    enrollment.getRollNumber(),
                    enrollment.getCourseCode(),
                    enrollment.getCourseTitle(),
                    enrollment.getCourseCredits(),
                    enrollment.getSectionDayTime(),
                    enrollment.getSectionRoom(),
            };

            // Store hidden enrollmentId safely
            table.putClientProperty("ENROLL_ID_" + tableModel.getRowCount(), enrollment.getEnrollmentId());

            tableModel.addRow(row);
        }
    }

    private void dropSection() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showWarning((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Please select a section to drop.");
            return;
        }

        boolean confirmed = MessageDialog.showConfirm((JFrame) SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to drop this section?");

        if (!confirmed) return;

        Integer enrollmentId = (Integer) table.getClientProperty("ENROLL_ID_" + selectedRow);

        if (enrollmentId == null) {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Internal error: Enrollment ID not found.");
            return;
        }

        String error = enrollmentService.dropSection(enrollmentId);

        if (error == null) {
            MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Section dropped successfully!");
            loadEnrollments();
        } else {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
        }
    }
}


