// package edu.univ.erp;

// import edu.univ.erp.ui.auth.LoginFrame;

// import javax.swing.*;

// public class Main {
//     public static void main(String[] args) {
//         try {
//             // ⭐ CHANGE: Set the Look and Feel to Nimbus
//             for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                 if ("Nimbus".equals(info.getName())) {
//                     UIManager.setLookAndFeel(info.getClassName());
//                     break;
//                 }
//             }
//         } catch (Exception e) {
//             // Fallback if Nimbus isn't found
//             System.err.println("Could not set Nimbus L&F. Falling back to default.");
//             try {
//                 // Fallback to Metal (Cross Platform)
//                 UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//             } catch (Exception fallbackE) {
//                 fallbackE.printStackTrace();
//             }
//         }

//         SwingUtilities.invokeLater(() -> {
//             new LoginFrame();
//         });
//     }
// }

package edu.univ.erp;

import edu.univ.erp.ui.auth.LoginFrame;

import javax.swing.*;
import java.awt.Color; // <-- NEW: Fixes the 'Color' error
//import java.awt.Font; // If you used the font modification

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Set the L&F to Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Color veryLightSkyBlue = new Color(224, 244, 255);
            UIManager.put("control", veryLightSkyBlue);
            // 2. Customize Colors (Requires the import java.awt.Color)
            // Color customOrange = new Color(230, 74, 25);
            // Color sidebarColor = new Color(255, 87, 34);

            // UIManager.put("nimbusBase", customOrange);
            // UIManager.put("control", customOrange);

            // UIManager.put("nimbusSelectionBackground", sidebarColor);
            UIManager.put("nimbusFocus", veryLightSkyBlue);

            // UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 14)); // Requires import java.awt.Font
                UIManager.put("nimbusBase", veryLightSkyBlue); // Base color for many nimbus gradients
                UIManager.put("textBackground", veryLightSkyBlue); // Background for text fields
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}


// package edu.univ.erp;
//
// import edu.univ.erp.ui.auth.LoginFrame;
//
// import javax.swing.*;
// // 💡 New Import for FlatLaf
// import com.formdev.flatlaf.intellijthemes.*;;
//
// public class Main {
//     public static void main(String[] args) {
//         try {
//             UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
////              ⭐ CHANGE: Use FlatLaf setup method instead of UIManager.getSystemLookAndFeelClassName()
////             FlatLightLaf.setup();
////             FlatGradiantoNatureGreenIJTheme()
////              If you prefer a Dark Mode theme, use:
////              FlatDarkLaf.setup();
//
////              Or the IntelliJ theme:
////              com.formdev.flatlaf.intellijthemes.FlatIntelliJLaf.setup();
//
//         } catch (Exception e) {
//             // FlatLaf throws an exception if it fails to load
//             System.err.println("Failed to initialize FlatLaf");
//             e.printStackTrace();
//         }
//
//         SwingUtilities.invokeLater(() -> {
//             new LoginFrame();
//         });
//     }
// }