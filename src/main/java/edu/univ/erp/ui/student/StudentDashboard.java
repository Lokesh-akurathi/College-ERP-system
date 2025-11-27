package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.auth.LoginFrame;
import edu.univ.erp.ui.common.ChangePasswordDialog;
import edu.univ.erp.ui.common.MaintenanceBanner;
import edu.univ.erp.ui.ThemeConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StudentDashboard extends JFrame {

    private final AuthService authService;
    private final JPanel mainPanel;
    private final JPanel bannerPanel;
    private JButton activeButton = null;

    public StudentDashboard() {
        this.authService = new AuthService();
        User currentUser = authService.getCurrentUser();

        setTitle("Student Dashboard - " + currentUser.getUsername());
        setSize(1000, 700);
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
        sidebarPanel.setBackground(ThemeConstants.PRIMARY_NAVY);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setForeground(ThemeConstants.TEXT_LIGHT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(welcomeLabel);

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setForeground(ThemeConstants.TEXT_LIGHT);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(userLabel);

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        addMenuButton(sidebarPanel, "Browse Courses", this::showCourseCatalog);
        addMenuButton(sidebarPanel, "My Registrations", this::showMyRegistrations);
        addMenuButton(sidebarPanel, "My Timetable", this::showMyTimetable);
        addMenuButton(sidebarPanel, "My Grades", this::showMyGrades);
        addMenuButton(sidebarPanel, "Download Transcript", this::showTranscript);

        sidebarPanel.add(Box.createVerticalGlue());

        JButton changePasswordButton = new JButton("Change Password");
        styleSecondaryButton(changePasswordButton);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        sidebarPanel.add(changePasswordButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton logoutButton = new JButton("Logout");
        styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        sidebarPanel.add(logoutButton);

        add(sidebarPanel, BorderLayout.WEST);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeConstants.SECONDARY_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        showWelcomePanel();
        setVisible(true);
    }


    private void showCourseCatalog() {
        mainPanel.removeAll();
        mainPanel.add(new CourseCatalogPanel(), BorderLayout.CENTER);
        refresh();
    }

    private void showMyRegistrations() {
        mainPanel.removeAll();
        mainPanel.add(new MyRegistrationsPanel(), BorderLayout.CENTER);
        refresh();
    }

    private void showMyTimetable() {
        mainPanel.removeAll();
        mainPanel.add(new MyTimetablePanel(), BorderLayout.CENTER);
        refresh();
    }

    private void showMyGrades() {
        mainPanel.removeAll();
        mainPanel.add(new MyGradesPanel(), BorderLayout.CENTER);
        refresh();
    }

    private void showTranscript() {
        mainPanel.removeAll();
        mainPanel.add(new TranscriptPanel(), BorderLayout.CENTER);
        refresh();
    }

    private void showWelcomePanel() {
        mainPanel.removeAll();
        JLabel label = new JLabel(
            "<html><h1>Welcome to Student Portal</h1><p>Use the menu to navigate.</p></html>",
            SwingConstants.CENTER
        );
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(ThemeConstants.TEXT_DARK);
        mainPanel.add(label, BorderLayout.CENTER);
        refresh();
    }


    private void refresh() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void addMenuButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBackground(ThemeConstants.PRIMARY_NAVY);
        button.setForeground(ThemeConstants.TEXT_LIGHT);
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            }
            public void mouseExited(MouseEvent e) {
                if (button != activeButton)
                    button.setBackground(ThemeConstants.PRIMARY_NAVY);
            }
        });

        button.addActionListener(e -> {
            if (activeButton != null)
                activeButton.setBackground(ThemeConstants.PRIMARY_NAVY);

            activeButton = button;
            activeButton.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
            action.run();
        });

        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void styleSecondaryButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 35));
        button.setBackground(ThemeConstants.BUTTON_HOVER_ACTIVE);
        button.setForeground(ThemeConstants.TEXT_LIGHT);
        button.setFocusPainted(false);
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, authService.getCurrentUser().getUserId());
        dialog.setVisible(true);
    }

    private void logout() {
        authService.logout();
        dispose();
        new LoginFrame();
    }
}
