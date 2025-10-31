package edu.univ.erp.ui.admin;

import edu.univ.erp.service.SettingsService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;

public class MaintenanceModePanel extends JPanel {
    private final SettingsService settingsService;
    private final Runnable refreshCallback;
    private final JLabel statusLabel;

    public MaintenanceModePanel(Runnable refreshCallback) {
        this.settingsService = new SettingsService();
        this.refreshCallback = refreshCallback;
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Maintenance Mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel infoLabel = new JLabel("<html><p><b>Maintenance Mode</b> prevents students and instructors " +
                "from making changes to the system.</p>" +
                "<p>They can still login and view data, but all modification operations will be blocked.</p>" +
                "<p>Use this feature when performing system maintenance or data updates.</p></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(infoLabel, gbc);

        statusLabel = new JLabel();
        updateStatusLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 1;
        contentPanel.add(statusLabel, gbc);

        JButton enableButton = new JButton("Enable Maintenance Mode");
        enableButton.setBackground(new Color(255, 152, 0));
        enableButton.setForeground(Color.WHITE);
        enableButton.setPreferredSize(new Dimension(200, 40));
        enableButton.addActionListener(e -> toggleMaintenanceMode(true));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        contentPanel.add(enableButton, gbc);

        JButton disableButton = new JButton("Disable Maintenance Mode");
        disableButton.setBackground(new Color(76, 175, 80));
        disableButton.setForeground(Color.WHITE);
        disableButton.setPreferredSize(new Dimension(200, 40));
        disableButton.addActionListener(e -> toggleMaintenanceMode(false));
        gbc.gridx = 1;
        contentPanel.add(disableButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void toggleMaintenanceMode(boolean enable) {
        String error = settingsService.toggleMaintenanceMode(enable);
        
        if (error == null) {
            String message = enable ? "Maintenance Mode ENABLED. Students and instructors can now only view data." :
                                    "Maintenance Mode DISABLED. Normal operations resumed.";
            MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), message);
            updateStatusLabel();
            refreshCallback.run();
        } else {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
        }
    }

    private void updateStatusLabel() {
        boolean isMaintenanceOn = settingsService.isMaintenanceMode();
        if (isMaintenanceOn) {
            statusLabel.setText("Current Status: MAINTENANCE MODE ON");
            statusLabel.setForeground(new Color(255, 87, 34));
        } else {
            statusLabel.setText("Current Status: Normal Operation");
            statusLabel.setForeground(new Color(76, 175, 80));
        }
    }
}
