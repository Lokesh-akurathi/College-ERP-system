package edu.univ.erp.ui.admin;

import edu.univ.erp.service.UserService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;

import javax.swing.*;
import java.awt.*;

public class ManageUsersPanel extends JPanel {
    private final UserService userService;

    public ManageUsersPanel() {
        this.userService = new UserService();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Create Student", createStudentPanel());
        tabbedPane.addTab("Create Instructor", createInstructorPanel());
        tabbedPane.addTab("Create Admin", createAdminPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField rollNoField = new JTextField(20);
        JTextField programField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));

        addFormField(panel, gbc, 0, "Username:", usernameField);
        addFormField(panel, gbc, 1, "Password:", passwordField);
        addFormField(panel, gbc, 2, "Roll Number:", rollNoField);
        addFormField(panel, gbc, 3, "First Name:", firstNameField);
        addFormField(panel, gbc, 4, "Last Name:", lastNameField);
        addFormField(panel, gbc, 5, "Program:", programField);
        addFormField(panel, gbc, 6, "Year:", yearSpinner);

        JButton createButton = new JButton("Create Student");
        createButton.setBackground(new Color(76, 175, 80));
        createButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String rollNo = rollNoField.getText().trim();
            String program = programField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            int year = (int) yearSpinner.getValue();

            String validationError = ValidationHelper.validateUserData(username, password);
            if (validationError != null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
                return;
            }

            if (rollNo.isEmpty() || program.isEmpty()) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "All fields are required.");
                return;
            }

            String error = userService.createStudent(username, password, rollNo, program, year, firstName, lastName);
            if (error == null) {
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Student created successfully!");
                usernameField.setText("");
                passwordField.setText("");
                rollNoField.setText("");
                programField.setText("");
                firstNameField.setText("");
                lastNameField.setText("");
                yearSpinner.setValue(1);
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
            }
        });

        return panel;
    }

    private JPanel createInstructorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField departmentField = new JTextField(20);
        JTextField salutationField = new JTextField(10);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);


        addFormField(panel, gbc, 0, "Username:", usernameField);
        addFormField(panel, gbc, 1, "Password:", passwordField);
        addFormField(panel, gbc, 2, "Salutation:", salutationField);
        addFormField(panel, gbc, 3, "First Name:", firstNameField);
        addFormField(panel, gbc, 4, "Last Name:", lastNameField);
        addFormField(panel, gbc, 5, "Department:", departmentField);


        JButton createButton = new JButton("Create Instructor");
        createButton.setBackground(new Color(76, 175, 80));
        createButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String department = departmentField.getText().trim();
            String salutation = salutationField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String validationError = ValidationHelper.validateUserData(username, password);
            if (validationError != null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
                return;
            }

            if (department.isEmpty()) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Department is required.");
                return;
            }

            String error = userService.createInstructor(username, password, department,firstName,lastName,salutation);
            if (error == null) {
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Instructor created successfully!");
                usernameField.setText("");
                passwordField.setText("");
                departmentField.setText("");
                salutationField.setText("");
                firstNameField.setText("");
                lastNameField.setText("");
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
            }
        });

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        addFormField(panel, gbc, 0, "Username:", usernameField);
        addFormField(panel, gbc, 1, "Password:", passwordField);

        JButton createButton = new JButton("Create Admin");
        createButton.setBackground(new Color(76, 175, 80));
        createButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(createButton, gbc);

        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            String validationError = ValidationHelper.validateUserData(username, password);
            if (validationError != null) {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), validationError);
                return;
            }

            String error = userService.createAdmin(username, password);
            if (error == null) {
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Admin created successfully!");
                usernameField.setText("");
                passwordField.setText("");
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
            }
        });

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}
