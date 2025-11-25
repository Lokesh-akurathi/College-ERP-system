

package edu.univ.erp.ui.common;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

public class GradeDistributionChartPanel extends JPanel {
    private final Map<String, Integer> gradeCounts;
    private final Map<String, Color> colorMap = new HashMap<>();

    public GradeDistributionChartPanel(Map<String, Integer> gradeCounts) {
        this.gradeCounts = gradeCounts;
        
        // Define colors for consistency
        colorMap.put("A", new Color(0, 150, 0));      // Dark Green
        colorMap.put("A-", new Color(50, 200, 50));   // Light Green
        colorMap.put("B", new Color(0, 150, 255));    // Blue
        colorMap.put("B-", new Color(50, 200, 255));  // Light Blue
        colorMap.put("C", new Color(255, 150, 0));    // Orange
        colorMap.put("C-", new Color(255, 200, 50));  // Light Orange
        colorMap.put("D", new Color(255, 100, 100));  // Light Red
        colorMap.put("F", new Color(200, 0, 0));      // Red
        
        setPreferredSize(new Dimension(300, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int totalStudents = gradeCounts.values().stream().mapToInt(i -> i).sum();
        if (totalStudents == 0) {
            g2.drawString("No data available to generate chart.", 50, getHeight() / 2);
            return;
        }

        // --- Chart Parameters ---
        // Size and position for the pie chart
        int chartSize = Math.min(getWidth(), getHeight()) - 100; // Leave space for the legend
        int chartX = 10;
        int chartY = 10;
        
        float startAngle = 0;
        int legendX = chartX + chartSize + 30; // Start legend to the right of the chart
        int legendY = 30;

        // --- Draw Pie Chart and Legend ---
        for (Map.Entry<String, Integer> entry : gradeCounts.entrySet()) {
            String grade = entry.getKey();
            int count = entry.getValue();
            float angle = (count * 360.0f) / totalStudents;
            Color color = colorMap.getOrDefault(grade, Color.GRAY);

            // 1. Draw Pie Slice
            g2.setColor(color);
            g2.fill(new Arc2D.Float(chartX, chartY, chartSize, chartSize, startAngle, angle, Arc2D.PIE));
            
            // 2. Draw Legend Entry
            g2.setColor(Color.BLACK); // Use black text for contrast
            
            // Color box for the grade
            g2.setColor(color);
            g2.fillRect(legendX, legendY - 8, 16, 16); 
            
            // Legend Label (Grade and Count)
            String label = String.format("%s (%d students)", grade, count);
            g2.drawString(label, legendX + 25, legendY + 5);
            
            // Move down for the next legend entry
            legendY += 25;
            
            startAngle += angle;
        }
    }
}


