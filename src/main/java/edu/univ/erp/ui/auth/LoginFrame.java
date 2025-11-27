package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.auth.LoginStatus;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.ui.common.BackgroundPanel;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthService authService;
    private final JLabel statusLabel;
    private final JButton loginButton;


    private String lockedUsername = null;

    public LoginFrame() {
        this.authService = new AuthService();

        
        BackgroundPanel bg = new BackgroundPanel("/images/iiitdrndblock.jpeg");
        bg.setLayout(new BorderLayout());

        setContentPane(bg);                    
        setTitle("University ERP - Login");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(true);
        headerPanel.setBackground(new Color(33, 150, 243, 200)); 
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("University ERP System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        bg.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
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

        this.loginButton = new JButton("Login");
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);


        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(statusLabel, gbc);



        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(loginButton, gbc);

        bg.add(formPanel, BorderLayout.CENTER);

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


        checkInitialLockout();

        setVisible(true);
    }

    private void checkInitialLockout() {

        if (usernameField.getText().trim().isEmpty()) return;

        String username = usernameField.getText().trim();
        Duration remaining = authService.getRemainingLockoutDuration(username);


        if (!remaining.isZero()) {
             setLockedState(username);
        }
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }


        if (username.equals(lockedUsername)) {
            Duration remaining = authService.getRemainingLockoutDuration(username);

            if (remaining.isZero()) {

                clearLockedState();
            } else {

                setLockedState(username);
                return;
            }
        }


        statusLabel.setText("");

        LoginStatus status = authService.login(username, password);

        if (status == LoginStatus.SUCCESS) {
            clearLockedState();

            User currentUser = authService.getCurrentUser();
            dispose();
            openDashboard(currentUser.getRole());

        } else if (status == LoginStatus.ACCOUNT_LOCKED_TIME) {

            setLockedState(username);

        } else {

            int attempts = authService.getFailedLoginAttempts(username);

            if (attempts >= 3) {

                setLockedState(username);
            } else {

                int attemptsLeft = 3 - attempts;
                String message = String.format("Incorrect credentials. You have %d attempts remaining.", attemptsLeft);
                statusLabel.setText(message);
            }
        }


        passwordField.setText("");

        usernameField.requestFocusInWindow();
    }


    private void setLockedState(String username) {
        lockedUsername = username;



        loginButton.setText("Login");
        statusLabel.setText("You are blocked for 5 minutes. Try again later.");


        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }


    private void clearLockedState() {
        lockedUsername = null;


        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        loginButton.setEnabled(true);

        loginButton.setText("Login");
        statusLabel.setText("");
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
