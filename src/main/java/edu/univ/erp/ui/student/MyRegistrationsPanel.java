
package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JLabel titleLabel = new JLabel("My Registrations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ThemeConstants.TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

       
        String[] columns = {
                "Roll No",
                "Code",
                "Title",
                "Credits",
                "Day/Time",
                "Room",
                "Instructor"
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

       
        if (column == 3) { 
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (isSelected) {
            c.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            c.setForeground(ThemeConstants.TEXT_LIGHT);
        } else {
            // Zebra Striping Logic
            if (row % 2 == 0) {
                c.setBackground(new Color(240, 245, 250)); // Light stripe
            } else {
                c.setBackground(Color.WHITE); // White stripe
            }
            c.setForeground(ThemeConstants.TEXT_DARK);
        }
        return c;
    }
};table.setDefaultRenderer(Object.class, rowStripeRenderer);
table.setDefaultRenderer(String.class, rowStripeRenderer);
table.setDefaultRenderer(Integer.class, rowStripeRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JButton dropButton = new JButton("Drop Selected Section");
        dropButton.setBackground(new Color(244, 67, 54));
        dropButton.setForeground(ThemeConstants.TEXT_LIGHT);
        dropButton.addActionListener(e -> dropSection());
        buttonPanel.add(dropButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        refreshButton.setForeground(ThemeConstants.TEXT_LIGHT);
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
                    enrollment.getInstructorName()
            };

         
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


