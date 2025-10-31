package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

public class MaintenanceBanner extends JPanel {
    private static final Color BANNER_COLOR = new Color(255, 152, 0);
    
    public MaintenanceBanner() {
        setBackground(BANNER_COLOR);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel("⚠ MAINTENANCE MODE - Read-Only Access");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        add(label);
    }
}
