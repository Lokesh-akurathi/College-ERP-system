package edu.univ.erp.ui.admin;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.common.MaintenanceBanner;

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
            // update top maintenance banner
            refreshMaintenanceBanner();

            // if the currently displayed main panel implements Refreshable, call its refresh()
//            if (mainPanel.getComponentCount() > 0) {
//                Component c = mainPanel.getComponent(0);
//                if (c instanceof Refreshable) {
//                    ((Refreshable) c).refresh();
//                }
//            }
        });

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(255, 87, 34));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcomeLabel = new JLabel("Admin Panel");
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

        addMenuButton(sidebarPanel, "Manage Users", () -> showManageUsers());
        addMenuButton(sidebarPanel, "Manage Courses", () -> showManageCourses());
        addMenuButton(sidebarPanel, "Manage Sections", () -> showManageSections());
        addMenuButton(sidebarPanel, "Settings panel", () -> showSettings());
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(150, 30));
        logoutButton.addActionListener(e -> logout());
        sidebarPanel.add(logoutButton);

        add(sidebarPanel, BorderLayout.WEST);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeMessage = new JLabel("<html><h1>Welcome to Admin Portal</h1>" +
                "<p>Use the menu on the left to manage the system.</p></html>");
        mainPanel.add(welcomeMessage, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addMenuButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(230, 74, 25));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            refreshMaintenanceBanner();
            action.run();
        });
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
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
