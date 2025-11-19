package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
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
    private List<Section> instructorSections;

    // --- Constructor ---
    public ManageGradingPanel(int instructorId) {
        this.currentInstructorId = instructorId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadInstructorSections();
        setupListeners();
    }

    // --- Component Setup ---
    private void initializeComponents() {
        // --- TOP PANEL: Section Selection ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Section:"));
        sectionComboBox = new JComboBox<>();
        topPanel.add(sectionComboBox);
        
        this.add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Criteria Table ---
        String[] columnNames = {"Component Name", "Weight (%)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Ensure the Weight column accepts numbers (Double)
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }
        };
        criteriaTable = new JTable(tableModel);
        
        // Add button to add new row
        JButton addRowButton = new JButton("Add New Component");
        addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"New Component", 0.0}));

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(new JScrollPane(criteriaTable), BorderLayout.CENTER);
        editorPanel.add(addRowButton, BorderLayout.SOUTH);
        
        this.add(editorPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL: Save and Status ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalWeightLabel = new JLabel("Total Weight: 0.0%");
        totalWeightLabel.setFont(totalWeightLabel.getFont().deriveFont(Font.BOLD));
        
        saveButton = new JButton("Save Grading Criteria");
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.add(totalWeightLabel);
        
        bottomPanel.add(statusPanel, BorderLayout.WEST);
        bottomPanel.add(saveButton, BorderLayout.EAST);
        
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Data Loading ---
    private void loadInstructorSections() {
        instructorSections = instructorService.getSectionsByInstructorId(currentInstructorId);
        sectionComboBox.removeAllItems();
        if (instructorSections.isEmpty()) {
            sectionComboBox.addItem("No sections assigned.");
            saveButton.setEnabled(false);
            return;
        }

        for (Section section : instructorSections) {
            String item = section.getCourseCode() + " - " + section.getCourseTitle() + " (Sec: " + section.getSectionId() + ")";
            sectionComboBox.addItem(item);
        }
    }

    // --- Listeners and Logic ---
    private void setupListeners() {
        sectionComboBox.addActionListener(e -> {
            int selectedIndex = sectionComboBox.getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < instructorSections.size()) {
                Section selectedSection = instructorSections.get(selectedIndex);
                loadCriteriaForSection(selectedSection.getSectionId());
            }
        });

        // Add a listener to update the total weight when the table data changes
        tableModel.addTableModelListener(e -> updateTotalWeight());

        saveButton.addActionListener(e -> saveCriteria());
        
        // Load criteria for the first section on initial load
        if (!instructorSections.isEmpty()) {
            loadCriteriaForSection(instructorSections.get(0).getSectionId());
        }
    }

    private void loadCriteriaForSection(int sectionId) {
        // Clear existing rows
        tableModel.setRowCount(0); 
        
        List<GradingCriteria> criteriaList = instructorService.getGradingCriteria(sectionId);
        
        if (criteriaList.isEmpty()) {
            // Add a default row if no criteria exists
            tableModel.addRow(new Object[]{"Homework", 40.0});
            tableModel.addRow(new Object[]{"Final Exam", 60.0});
        } else {
            for (GradingCriteria criteria : criteriaList) {
                tableModel.addRow(new Object[]{
                    criteria.getComponentName(),
                    criteria.getWeightPercentage()
                });
            }
        }
        updateTotalWeight();
    }
    
    private void updateTotalWeight() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object value = tableModel.getValueAt(i, 1);
            if (value instanceof Double) {
                total += (Double) value;
            } else if (value instanceof String) {
                 try {
                     total += Double.parseDouble((String) value);
                 } catch (NumberFormatException ignored) {
                     // Ignore non-numeric input for now, but in a real app, display error
                 }
            }
        }

        totalWeightLabel.setText(String.format("Total Weight: %.2f%%", total));
        
        // Highlight in red if total is not 100%
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
                
                // Simple validation check
                if (name.isEmpty() || weight <= 0) {
                    JOptionPane.showMessageDialog(this, "Component Name cannot be empty and weight must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                criteriaList.add(new GradingCriteria(selectedSectionId, name, weight, i + 1));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Weight must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if (instructorService.updateGradingCriteria(selectedSectionId, criteriaList)) {
            JOptionPane.showMessageDialog(this, "Grading criteria saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // This captures the 100% error from the service layer, but also database errors.
            JOptionPane.showMessageDialog(this, "Failed to save criteria. Please check total weight (must be 100%) and input fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}