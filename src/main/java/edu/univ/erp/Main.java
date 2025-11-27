

// package edu.univ.erp;

// import edu.univ.erp.ui.auth.LoginFrame;

// import javax.swing.*;
// import java.awt.Color; // <-- NEW: Fixes the 'Color' error
// //import java.awt.Font; // If you used the font modification

// public class Main {
//     public static void main(String[] args) {
//         try {
//             // 1. Set the L&F to Nimbus
//             for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                 if ("Nimbus".equals(info.getName())) {
//                     UIManager.setLookAndFeel(info.getClassName());
//                     break;
//                 }
//             }
//             Color veryLightSkyBlue = new Color(224, 244, 255);
//             UIManager.put("control", veryLightSkyBlue);
//             // 2. Customize Colors (Requires the import java.awt.Color)
//             // Color customOrange = new Color(230, 74, 25);
//             // Color sidebarColor = new Color(255, 87, 34);

//             // UIManager.put("nimbusBase", customOrange);
//             // UIManager.put("control", customOrange);

//             // UIManager.put("nimbusSelectionBackground", sidebarColor);
//             UIManager.put("nimbusFocus", veryLightSkyBlue);

//             // UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 14)); // Requires import java.awt.Font
//                 UIManager.put("nimbusBase", veryLightSkyBlue); // Base color for many nimbus gradients
//                 UIManager.put("textBackground", veryLightSkyBlue); // Background for text fields
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         SwingUtilities.invokeLater(() -> {
//             new LoginFrame();
//         });
//     }
// }
package edu.univ.erp;

import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.ThemeConstants; // NEW: Import the custom colors

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;

// NEW: Required FlatLaf imports (Ensure the FlatLaf library is in your classpath)
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Setup FlatLaf Theme (Use Light as the base for a clean look)
            FlatLaf.setup(new FlatLightLaf());
            
            // 2. Set GLOBAL UIManager Defaults for the Custom Palette

            // --- Backgrounds ---
            // Set the main application background color (Muted Sky Blue)
            UIManager.put("Panel.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("Viewport.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("textBackground", Color.WHITE); // Keep text fields white for contrast
            
            // --- Standard Buttons (Used for Login and other actions) ---
            UIManager.put("Button.background", ThemeConstants.ACTION_BUTTON_VIBRANT); // Vibrant Blue
            UIManager.put("Button.foreground", ThemeConstants.TEXT_LIGHT);
            UIManager.put("Button.hoverBackground", ThemeConstants.BUTTON_HOVER_ACTIVE);
            UIManager.put("Button.focusedBackground", ThemeConstants.BUTTON_HOVER_ACTIVE);
            UIManager.put("Button.arc", 8); // Slightly rounded corners
            
            // --- Text/Label Defaults ---
            UIManager.put("Label.foreground", ThemeConstants.TEXT_DARK); 
            UIManager.put("textForeground", ThemeConstants.TEXT_DARK);
            UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 14));
            
            // --- Table Defaults (for Course Catalog, etc.) ---
            UIManager.put("Table.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("TableHeader.background", ThemeConstants.SECONDARY_BACKGROUND);
            UIManager.put("Table.gridColor", new Color(0xC0C0C0)); // Subtle grey grid lines
            UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 14));

            // The Sidebar and Navigation Buttons must be styled directly in the Dashboard code 
            // (as shown in the previous response) to use PRIMARY_NAVY and a transparent background.

        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf LookAndFeel.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}