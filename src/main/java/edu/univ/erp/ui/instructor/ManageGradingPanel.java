package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageGradingPanel extends JPanel {

    private final InstructorService instructorService = new InstructorService();
    private int currentInstructorId;
    private JComboBox<String> sectionComboBox;
    private JTable criteriaTable;
    private DefaultTableModel tableModel;
    private JLabel totalWeightLabel;
    private JButton saveButton;
    private JButton deleteRowButton;
    private List<Section> instructorSections;

    public ManageGradingPanel(int instructorId) {
        this.currentInstructorId = instructorId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        
        initializeComponents();
        loadInstructorSections();
        setupListeners();
    }

    private void initializeComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
       JLabel selectSectionLabel = new JLabel("Select Section:"); 
    selectSectionLabel.setForeground(ThemeConstants.TEXT_DARK);
    topPanel.add(selectSectionLabel);

        sectionComboBox = new JComboBox<>();
        topPanel.add(sectionComboBox);
        this.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Component Name", "Weight (%)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }
        };
        criteriaTable = new JTable(tableModel);
        criteriaTable.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
    criteriaTable.setForeground(ThemeConstants.TEXT_DARK);
    criteriaTable.setGridColor(ThemeConstants.PRIMARY_NAVY);
    criteriaTable.setSelectionBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
    criteriaTable.setSelectionForeground(ThemeConstants.TEXT_LIGHT);
    criteriaTable.getTableHeader().setBackground(ThemeConstants.PRIMARY_NAVY);
    criteriaTable.getTableHeader().setForeground(ThemeConstants.TEXT_LIGHT);
    criteriaTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
   
DefaultTableCellRenderer customRowStripeRenderer = new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        ((JComponent)c).setOpaque(true);

        
        if (column == 1) {
             setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
             setHorizontalAlignment(SwingConstants.LEFT);
        }

        if (isSelected) {
            c.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            c.setForeground(ThemeConstants.TEXT_LIGHT);
        } else {
           
            if (row % 2 == 0) {
                c.setBackground(new Color(240, 245, 250));
            } else {
                c.setBackground(Color.WHITE);
            }
            c.setForeground(ThemeConstants.TEXT_DARK);
        }
        return c;
    }
};

criteriaTable.setDefaultRenderer(Object.class, customRowStripeRenderer);

criteriaTable.setDefaultRenderer(String.class, customRowStripeRenderer);

criteriaTable.setDefaultRenderer(Double.class, customRowStripeRenderer);

        JButton addRowButton = new JButton("Add New Component");
        addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"New Component", 0.0}));

        deleteRowButton = new JButton("Delete Selected Component");
        deleteRowButton.addActionListener(e -> deleteSelectedRow());

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        actionPanel.add(addRowButton);
        actionPanel.add(deleteRowButton);

        editorPanel.add(new JScrollPane(criteriaTable), BorderLayout.CENTER);
        editorPanel.add(actionPanel, BorderLayout.SOUTH);
        this.add(editorPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        totalWeightLabel = new JLabel("Total Weight: 0.0%");
        totalWeightLabel.setFont(totalWeightLabel.getFont().deriveFont(Font.BOLD));
        saveButton = new JButton("Save Grading Criteria");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(ThemeConstants.TEXT_LIGHT);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        statusPanel.add(totalWeightLabel);
        bottomPanel.add(statusPanel, BorderLayout.WEST);
        bottomPanel.add(saveButton, BorderLayout.EAST);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadInstructorSections() {
        instructorSections = instructorService.getSectionsByInstructorId(currentInstructorId);
        sectionComboBox.removeAllItems();

        if (instructorSections.isEmpty()) {
            sectionComboBox.addItem("No sections assigned");
            saveButton.setEnabled(false);
            return;
        }

        for (Section section : instructorSections) {
            String display = section.getCourseCode() + " | " + section.getDayTime();
            sectionComboBox.addItem(display);
        }
    }

    private void setupListeners() {
        sectionComboBox.addActionListener(e -> {
            int selectedIndex = sectionComboBox.getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < instructorSections.size()) {
                loadCriteriaForSection(instructorSections.get(selectedIndex).getSectionId());
            }
        });

        tableModel.addTableModelListener(e -> updateTotalWeight());
        saveButton.addActionListener(e -> saveCriteria());

        if (!instructorSections.isEmpty()) {
            loadCriteriaForSection(instructorSections.get(0).getSectionId());
        }
    }

    private void loadCriteriaForSection(int sectionId) {
        tableModel.setRowCount(0);
        List<GradingCriteria> criteriaList = instructorService.getGradingCriteria(sectionId);

        if (criteriaList.isEmpty()) {
            tableModel.addRow(new Object[]{"Homework", 40.0});
            tableModel.addRow(new Object[]{"Endsem", 60.0});
        } else {
            for (GradingCriteria criteria : criteriaList) {
                tableModel.addRow(new Object[]{criteria.getComponentName(), criteria.getWeightPercentage()});
            }
        }
        updateTotalWeight();
    }

    private void deleteSelectedRow() {
        int row = criteriaTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a component to delete");
            return;
        }
        tableModel.removeRow(row);
        updateTotalWeight();
    }

    private void updateTotalWeight() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                total += Double.parseDouble(tableModel.getValueAt(i, 1).toString());
            } catch (Exception ignored) {}
        }

        totalWeightLabel.setText(String.format("Total Weight: %.2f%%", total));

        if (Math.abs(total - 100.0) > 0.001) {
            totalWeightLabel.setForeground(Color.RED);
            saveButton.setEnabled(false);
        } else {
            totalWeightLabel.setForeground(Color.GREEN.darker());
            saveButton.setEnabled(true);
        }
    }

    private void saveCriteria() {
        int selectedIndex = sectionComboBox.getSelectedIndex();
        if (selectedIndex == -1 || selectedIndex >= instructorSections.size()) return;

        int selectedSectionId = instructorSections.get(selectedIndex).getSectionId();
        List<GradingCriteria> criteriaList = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String name = tableModel.getValueAt(i, 0).toString().trim();
                double weight = Double.parseDouble(tableModel.getValueAt(i, 1).toString());

                if (name.isEmpty() || weight <= 0) {
                    JOptionPane.showMessageDialog(this, "Invalid component values", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                criteriaList.add(new GradingCriteria(selectedSectionId, name, weight, i + 1));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Weight must be numeric", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (instructorService.updateGradingCriteria(selectedSectionId, criteriaList)) {
            JOptionPane.showMessageDialog(this, "Grading criteria saved successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Save failed. Ensure total weight = 100%.");
        }
    }
}
