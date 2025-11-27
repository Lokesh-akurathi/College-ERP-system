package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.domain.Section;

import edu.univ.erp.service.GradeStatsService;
import edu.univ.erp.domain.GradeStats;
import edu.univ.erp.ui.common.GradeDistributionChartPanel; 

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

        table = new JTable(tableModel);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
       
        statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Section Grade Statistics (Select a row)"));
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