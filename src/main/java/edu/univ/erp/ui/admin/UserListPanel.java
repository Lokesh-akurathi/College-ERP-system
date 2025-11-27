package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.User;
import edu.univ.erp.service.UserService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.ValidationHelper;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UserListPanel extends JPanel {
    
    private final UserService userService;
    private final String userRole;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserListPanel(String role) {
        this.userService = new UserService();
        this.userRole = role;
        
        setLayout(new BorderLayout());
        
        setupTableModel();
        userTable = new JTable(tableModel);
    
        loadUserData();
        
        addUserActionListener();
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> loadUserData());
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(refreshButton);
        add(controlPanel, BorderLayout.NORTH);
    }
    private void setupTableModel() {
       
        String[] columnNames = {"User ID", "Name", "Username", "Role", "Status", "Action"}; 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
    }

    private void loadUserData() {
        tableModel.setRowCount(0); 

        List<User> users = userService.getUsersByType(userRole); 
        
        for (User user : users) {
            
            String fullName = user.getFullName(); 
            
         
            String actionLabel = user.getStatus().equalsIgnoreCase("LOCKED") ? 
                                 "Unlock Account" : 
                                 "Reset Password";
            
            tableModel.addRow(new Object[]{
                user.getUserId(), 
                fullName,
                user.getUsername(), 
                user.getRole(), 
                user.getStatus(),
                actionLabel 
            }); 
        }
    }
    
    private void addUserActionListener() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = userTable.rowAtPoint(e.getPoint());
                int col = userTable.columnAtPoint(e.getPoint());
                

                if (col == 5 && row >= 0) {
                    int userId = (int) tableModel.getValueAt(row, 0);
                    String displayName = (String) tableModel.getValueAt(row, 1);
                    String actionLabel = (String) tableModel.getValueAt(row, 5); 
                    
                   
                    if (actionLabel.equals("Unlock Account")) {
                        handleUnlockAccount(userId, displayName);
                    } else if (actionLabel.equals("Reset Password")) {
                        handlePasswordReset(userId, displayName); 
                    }
                }
            }
        });
    }


   
    private void handleUnlockAccount(int userId, String displayName) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        int confirm = JOptionPane.showConfirmDialog(parentFrame, 
            "Are you sure you want to manually UNLOCK the account for " + displayName + "?", 
            "Confirm Account Unlock", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
           
            if (userService.unlockUser(userId)) {
                MessageDialog.showSuccess(parentFrame, 
                    displayName + "'s account has been successfully unlocked.");
                loadUserData();
            } else {
                MessageDialog.showError(parentFrame, 
                    "Failed to unlock account for " + displayName + " due to a service error.");
            }
        }
    }


    private void handlePasswordReset(int userId, String displayName) {
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        String newPassword = JOptionPane.showInputDialog(parentFrame,
            "Enter NEW password for user: " + displayName + " (ID: " + userId + ")", 
            "Password Reset", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String trimmedPassword = newPassword.trim();

            
            if (!ValidationHelper.isValidPassword(trimmedPassword)) { 
                MessageDialog.showError(parentFrame, "Password must be at least 6 characters long.");
                return;
            }
            
            if (userService.resetUserPassword(userId, trimmedPassword)) {
                
                MessageDialog.showSuccess(parentFrame, 
                    "Password successfully reset for " + displayName + "!");
            } else {
                MessageDialog.showError(parentFrame, 
                    "Failed to update password due to a service or database error.");
            }
        }
    }
}