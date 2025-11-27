package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.ui.common.BackgroundPanel;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthService authService;

    public LoginFrame() {
        this.authService = new AuthService();

        // Use classpath resource (place the jpeg under src/main/resources/images/)
        BackgroundPanel bg = new BackgroundPanel("/images/iiitdrndblock.jpeg");
        bg.setLayout(new BorderLayout());

        setContentPane(bg);                     // only call once
        setTitle("University ERP - Login");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header (semi-transparent so background is visible)
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(true);
        headerPanel.setBackground(new Color(33, 150, 243, 200)); // alpha < 255 -> semi transparent
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        bg.add(headerPanel, BorderLayout.NORTH);

        // Form (make non-opaque so background shows through)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // <-- important
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(loginButton, gbc);

        bg.add(formPanel, BorderLayout.CENTER);

        // Footer (non-opaque or semi-transparent)
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel infoLabel = new JLabel("<html><center>Default Credentials:<br>" +
                "admin1/admin123, inst1/inst123, stu1/stu123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(Color.WHITE);
        footerPanel.add(infoLabel);
        bg.add(footerPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        setVisible(true);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            MessageDialog.showError(this, "Please enter both username and password.");
            return;
        }

        if (authService.login(username, password)) {
            User currentUser = authService.getCurrentUser();
            dispose();
            openDashboard(currentUser.getRole());
        } else {
            MessageDialog.showError(this, "Incorrect username or password.");
            passwordField.setText("");
        }
    }

    private void openDashboard(String role) {
        SwingUtilities.invokeLater(() -> {
            switch (role) {
                case "ADMIN":
                    new AdminDashboard();
                    break;
                case "INSTRUCTOR":
                    new InstructorDashboard();
                    break;
                case "STUDENT":
                    new StudentDashboard();
                    break;
                default:
                    MessageDialog.showError(null, "Unknown role: " + role);
            }
        });
    }
}

