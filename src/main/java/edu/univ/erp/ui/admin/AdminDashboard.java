package edu.univ.erp.ui.admin;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.common.MaintenanceBanner;
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private final AuthService authService;
    private final JPanel mainPanel;
    private final JPanel bannerPanel;
    private final Runnable refreshCallback;

    public AdminDashboard() {
        this.authService = new AuthService();
        User currentUser = authService.getCurrentUser();
        
        setTitle("Admin Dashboard - " + currentUser.getUsername());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        bannerPanel = new JPanel(new BorderLayout());
        if (AccessControl.isMaintenanceMode()) {
            bannerPanel.add(new MaintenanceBanner(), BorderLayout.NORTH);
        }
        add(bannerPanel, BorderLayout.NORTH);

        refreshCallback = () -> SwingUtilities.invokeLater(() -> {
            refreshMaintenanceBanner();

        });

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(ThemeConstants.PRIMARY_NAVY);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcomeLabel = new JLabel("Admin Panel");
        welcomeLabel.setForeground(ThemeConstants.TEXT_LIGHT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(welcomeLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setForeground(ThemeConstants.TEXT_LIGHT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(userLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        addMenuButton(sidebarPanel, "Manage Users", () -> showManageUsers());
        addMenuButton(sidebarPanel, "Manage Courses", () -> showManageCourses());
        addMenuButton(sidebarPanel, "Manage Sections", () -> showManageSections());
        addMenuButton(sidebarPanel, "Settings panel", () -> showSettings());
        
        sidebarPanel.add(Box.createVerticalGlue());
        
       JButton logoutButton = new JButton("Logout");
        styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        sidebarPanel.add(logoutButton);


        add(sidebarPanel, BorderLayout.WEST);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeMessage = new JLabel("<html><h1>Welcome to Admin Portal</h1>" +
                "<p>Use the menu on the left to manage the system.</p></html>");
        welcomeMessage.setForeground(ThemeConstants.TEXT_DARK);
        welcomeMessage.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeMessage, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

   private JButton activeButton = null;

private void addMenuButton(JPanel panel, String text, Runnable action) {
    JButton button = new JButton(text);

    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(200, 40));
    button.setBackground(ThemeConstants.PRIMARY_NAVY);
    button.setForeground(ThemeConstants.TEXT_LIGHT);
    button.setFocusPainted(false);
    button.setBorderPainted(true);

    button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        }
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            if (button != activeButton) {
                button.setBackground(ThemeConstants.PRIMARY_NAVY);
            }
        }
    });

    button.addActionListener(e -> {
        refreshMaintenanceBanner();

        if (activeButton != null) {
            activeButton.setBackground(ThemeConstants.PRIMARY_NAVY);
        }

        activeButton = button;
        activeButton.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        action.run();
    });

    panel.add(button);
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
}

    
    private void styleSecondaryButton(JButton button) {
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setMaximumSize(new Dimension(200, 35));
    button.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
    button.setForeground(ThemeConstants.TEXT_LIGHT);
    button.setFocusPainted(false);
}

    private void showManageUsers() {
        mainPanel.removeAll();
        mainPanel.add(new ManageUsersPanel(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showManageCourses() {
        mainPanel.removeAll();
        mainPanel.add(new ManageCoursesPanel(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showManageSections() {
        mainPanel.removeAll();
        mainPanel.add(new ManageSectionsPanel(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showSettings() {
        mainPanel.removeAll();
        mainPanel.add(new AdminSettingsPanel(refreshCallback), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showMaintenanceMode() {
        mainPanel.removeAll();
        mainPanel.add(new MaintenanceModePanel(() -> refreshMaintenanceBanner()), BorderLayout.CENTER);
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
