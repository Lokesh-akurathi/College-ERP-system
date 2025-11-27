package edu.univ.erp.ui.common;

import edu.univ.erp.service.UserService;
import edu.univ.erp.util.ValidationHelper;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    private final UserService userService;
    private final int userId;
    private final JPasswordField oldPasswordField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;

    public ChangePasswordDialog(JFrame parent, int userId) {
        super(parent, "Change Password", true); 
        this.userId = userId;
        this.userService = new UserService();

    
        oldPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        int row = 0;
        addFormField(formPanel, gbc, row++, "Old Password:", oldPasswordField);
        addFormField(formPanel, gbc, row++, "New Password:", newPasswordField);
        addFormField(formPanel, gbc, row++, "Confirm New Password:", confirmPasswordField);

        
        JButton changeButton = new JButton("Change Password");
        changeButton.setBackground(new Color(30, 144, 255));
        changeButton.setForeground(Color.WHITE);
        changeButton.addActionListener(e -> handleChangePassword());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(changeButton);

        
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(field, gbc);
    }

    private void handleChangePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

     
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            MessageDialog.showError(parentFrame, "All fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            MessageDialog.showError(parentFrame, "New passwords do not match.");
            return;
        }

        if (!ValidationHelper.isValidPassword(newPassword)) {
            MessageDialog.showError(parentFrame, "New password must be at least 6 characters long.");
            return;
        }

       
        String error = userService.changePassword(userId, oldPassword, newPassword);

        
        if (error == null) {
            MessageDialog.showSuccess(parentFrame, "Password changed successfully!");
            this.dispose(); 
        } else {
            MessageDialog.showError(parentFrame, error);
        }
    }
}