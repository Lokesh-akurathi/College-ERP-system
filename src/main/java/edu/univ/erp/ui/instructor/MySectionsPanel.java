package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Section;

import edu.univ.erp.service.GradeStatsService;
import edu.univ.erp.domain.GradeStats;
import edu.univ.erp.ui.common.GradeDistributionChartPanel; 
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MySectionsPanel extends JPanel {
    private final SectionStore sectionStore;
  
    private final GradeStatsService gradeStatsService; 
    
    private final JTable table;
    private final JPanel statsPanel; 

    public MySectionsPanel() {
        this.sectionStore = new SectionStore();
        this.gradeStatsService = new GradeStatsService(); 
        
        
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        JLabel titleLabel = new JLabel("My Sections");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        titleLabel.setForeground(ThemeConstants.TEXT_DARK);
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

        table = new JTable(tableModel);
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

        if (isSelected) {
            c.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            c.setForeground(ThemeConstants.TEXT_LIGHT);
        } else {
          
            if (row % 2 == 0) {
                c.setBackground(new Color(240, 245, 250)); // Light bluish/gray stripe
            } else {
                c.setBackground(Color.WHITE); // White stripe
            }
            c.setForeground(ThemeConstants.TEXT_DARK);
        }
        return c;
    }
};
table.setDefaultRenderer(Object.class, rowStripeRenderer);
table.setDefaultRenderer(String.class, rowStripeRenderer);
table.setDefaultRenderer(Integer.class, rowStripeRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);

        
       
        statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);

        statsPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(ThemeConstants.PRIMARY_NAVY, 1), 
    "Section Grade Statistics (Select a row)",
    javax.swing.border.TitledBorder.LEFT,
    javax.swing.border.TitledBorder.TOP, 
    new Font("Arial", Font.BOLD, 14), 
    ThemeConstants.TEXT_DARK 
));
statsPanel.add(new JLabel("Select a section row above to view final grade statistics.", SwingConstants.CENTER), BorderLayout.CENTER);

       
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, statsPanel);
        splitPane.setResizeWeight(0.7); 
        add(splitPane, BorderLayout.CENTER);
        
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    viewSectionStats();
                }
            }
        });
    }

    private void viewSectionStats() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        int sectionId = (int) model.getValueAt(selectedRow, 0); 
        
       
        GradeStats stats = gradeStatsService.calculateStats(sectionId);

        
        statsPanel.removeAll();
        statsPanel.setBorder(BorderFactory.createTitledBorder("Section Grade Statistics for Section ID " + sectionId));
        
        if (stats.getGradeCounts().isEmpty()) {
            statsPanel.add(new JLabel("No final grades computed for this section.", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
           
            JPanel textStats = new JPanel(new GridLayout(3, 2));
            textStats.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
            textStats.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            textStats.add(new JLabel("Number of Students:"));
            textStats.add(new JLabel(String.valueOf(stats.getGradeCounts().values().stream().mapToInt(i -> i).sum())));
            textStats.add(new JLabel("Average Score:"));
            textStats.add(new JLabel(String.format("%.2f", stats.getAverageScore())));
            textStats.add(new JLabel("Median Score:"));
            textStats.add(new JLabel(String.format("%.2f", stats.getMedianScore())));
            statsPanel.add(textStats, BorderLayout.NORTH);

         
            GradeDistributionChartPanel chartPanel = new GradeDistributionChartPanel(stats.getGradeCounts());
            statsPanel.add(chartPanel, BorderLayout.CENTER);
        }

       
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }
}