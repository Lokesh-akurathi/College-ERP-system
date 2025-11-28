
package edu.univ.erp;

import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;


import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        try {
           
            FlatLaf.setup(new FlatLightLaf());
            
            
            UIManager.put("Panel.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("Viewport.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("textBackground", Color.WHITE); 
            
            
            UIManager.put("Button.background", ThemeConstants.ACTION_BUTTON_VIBRANT); 
            UIManager.put("Button.foreground", ThemeConstants.TEXT_LIGHT);
            UIManager.put("Button.hoverBackground", ThemeConstants.BUTTON_HOVER_ACTIVE);
            UIManager.put("Button.focusedBackground", ThemeConstants.BUTTON_HOVER_ACTIVE);
            UIManager.put("Button.arc", 8); 
            
            UIManager.put("Label.foreground", ThemeConstants.TEXT_DARK); 
            UIManager.put("textForeground", ThemeConstants.TEXT_DARK);
            UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 14));
            
           
            UIManager.put("Table.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("TableHeader.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("Table.gridColor", new Color(0xC0C0C0)); 
            UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 14));

           

        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf LookAndFeel.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}