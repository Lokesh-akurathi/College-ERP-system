// package edu.univ.erp.ui.instructor;

// import edu.univ.erp.access.AccessControl;
// import edu.univ.erp.auth.AuthService;
// import edu.univ.erp.domain.User;
// import edu.univ.erp.ui.auth.LoginFrame;
// import edu.univ.erp.ui.common.MaintenanceBanner;

// import javax.swing.*;
// import java.awt.*;

// public class InstructorDashboard extends JFrame {
//     private final AuthService authService;
//     private final JPanel mainPanel;
//     private final JPanel bannerPanel;

//     public InstructorDashboard() {
//         this.authService = new AuthService();
//         User currentUser = authService.getCurrentUser();
        
//         setTitle("Instructor Dashboard - " + currentUser.getUsername());
//         setSize(900, 600);
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setLocationRelativeTo(null);
//         setLayout(new BorderLayout());

//         bannerPanel = new JPanel(new BorderLayout());
//         if (AccessControl.isMaintenanceMode()) {
//             bannerPanel.add(new MaintenanceBanner(), BorderLayout.NORTH);
//         }
//         add(bannerPanel, BorderLayout.NORTH);

//         JPanel sidebarPanel = new JPanel();
//         sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
//         sidebarPanel.setBackground(new Color(156, 39, 176));
//         sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
//         sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

//         JLabel welcomeLabel = new JLabel("Welcome!");
//         welcomeLabel.setForeground(Color.WHITE);
//         welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
//         welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//         sidebarPanel.add(welcomeLabel);
//         sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

//         JLabel userLabel = new JLabel(currentUser.getUsername());
//         userLabel.setForeground(Color.WHITE);
//         userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//         sidebarPanel.add(userLabel);
//         sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

//         addMenuButton(sidebarPanel, "My Sections", () -> showMySections());
//         addMenuButton(sidebarPanel, "Grade Entry", () -> showGradeEntry());
        
//         sidebarPanel.add(Box.createVerticalGlue());
        
//         JButton logoutButton = new JButton("Logout");
//         logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
//         logoutButton.setMaximumSize(new Dimension(150, 30));
//         logoutButton.addActionListener(e -> logout());
//         sidebarPanel.add(logoutButton);

//         add(sidebarPanel, BorderLayout.WEST);

//         mainPanel = new JPanel(new BorderLayout());
//         mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
//         JLabel welcomeMessage = new JLabel("<html><h1>Welcome to Instructor Portal</h1>" +
//                 "<p>Use the menu on the left to navigate through different sections.</p></html>");
//         mainPanel.add(welcomeMessage, BorderLayout.CENTER);
        
//         add(mainPanel, BorderLayout.CENTER);

//         setVisible(true);
//     }

//     private void addMenuButton(JPanel panel, String text, Runnable action) {
//         JButton button = new JButton(text);
//         button.setAlignmentX(Component.CENTER_ALIGNMENT);
//         button.setMaximumSize(new Dimension(180, 40));
//         button.setBackground(new Color(106, 27, 154));
//         button.setForeground(Color.WHITE);
//         button.setFocusPainted(false);
//         button.addActionListener(e -> {
//             refreshMaintenanceBanner();
//             action.run();
//         });
//         panel.add(button);
//         panel.add(Box.createRigidArea(new Dimension(0, 10)));
//     }

//     private void showMySections() {
//         mainPanel.removeAll();
//         mainPanel.add(new MySectionsPanel(), BorderLayout.CENTER);
//         mainPanel.revalidate();
//         mainPanel.repaint();
//     }

//     private void showGradeEntry() {
//         mainPanel.removeAll();
//         mainPanel.add(new GradeEntryPanel(), BorderLayout.CENTER);
//         mainPanel.revalidate();
//         mainPanel.repaint();
//     }

//     private void refreshMaintenanceBanner() {
//         bannerPanel.removeAll();
//         if (AccessControl.isMaintenanceMode()) {
//             bannerPanel.add(new MaintenanceBanner(), BorderLayout.NORTH);
//         }
//         bannerPanel.revalidate();
//         bannerPanel.repaint();
//     }

//     private void logout() {
//         authService.logout();
//         dispose();
//         new LoginFrame();
//     }
// }
package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.common.MaintenanceBanner;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    private final AuthService authService;
    private final JPanel mainPanel;
    private final JPanel bannerPanel;
    // Store current user to easily retrieve their ID for service calls
    private final User currentUser; 

    public InstructorDashboard() {
        this.authService = new AuthService();
        this.currentUser = authService.getCurrentUser(); // Store the user object
        
        setTitle("Instructor Dashboard - " + currentUser.getUsername());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        bannerPanel = new JPanel(new BorderLayout());
        if (AccessControl.isMaintenanceMode()) {
            bannerPanel.add(new MaintenanceBanner(), BorderLayout.NORTH);
        }
        add(bannerPanel, BorderLayout.NORTH);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(156, 39, 176));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(welcomeLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(userLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- NEW BUTTON ADDED HERE ---
        addMenuButton(sidebarPanel, "My Sections", () -> showMySections());
        addMenuButton(sidebarPanel, "Grade Entry", () -> showGradeEntry());
        addMenuButton(sidebarPanel, "Manage Grading Criteria", () -> showManageGrading());
        // -----------------------------
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(150, 30));
        logoutButton.addActionListener(e -> logout());
        sidebarPanel.add(logoutButton);

        add(sidebarPanel, BorderLayout.WEST);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeMessage = new JLabel("<html><h1>Welcome to Instructor Portal</h1>" +
                "<p>Use the menu on the left to navigate through different sections.</p></html>");
        mainPanel.add(welcomeMessage, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addMenuButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(106, 27, 154));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            refreshMaintenanceBanner();
            action.run();
        });
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showMySections() {
        mainPanel.removeAll();
        // NOTE: MySectionsPanel needs a constructor that accepts the instructorId if it uses it.
        mainPanel.add(new MySectionsPanel(), BorderLayout.CENTER); 
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showGradeEntry() {
        mainPanel.removeAll();
        // NOTE: GradeEntryPanel needs a constructor that accepts the instructorId if it uses it.
        mainPanel.add(new GradeEntryPanel(), BorderLayout.CENTER); 
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * NEW METHOD: Displays the ManageGradingPanel
     */
    private void showManageGrading() {
        mainPanel.removeAll();
        // Pass the current user's ID to the panel so it can fetch the correct sections.
        mainPanel.add(new ManageGradingPanel(currentUser.getUserId()), BorderLayout.CENTER); 
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void refreshMaintenanceBanner() {
        bannerPanel.removeAll();
        if (AccessControl.isMaintenanceMode()) {
            bannerPanel.add(new MaintenanceBanner(), BorderLayout.NORTH);
        }
        bannerPanel.revalidate();
        bannerPanel.repaint();
    }

    private void logout() {
        authService.logout();
        dispose();
        new LoginFrame();
    }
}