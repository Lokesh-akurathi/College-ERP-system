package edu.univ.erp.ui.admin;
import edu.univ.erp.service.SettingsService;
import edu.univ.erp.ui.common.MessageDialog;
import javax.swing.*;
import java.awt.*;

public class AddDropControlPanel extends JPanel {
    private final SettingsService settingsService;
    private final Runnable refreshCallback;
    private final JLabel statusLabel;

    public AddDropControlPanel(Runnable refreshCallback) {
        this.settingsService = new SettingsService();
        this.refreshCallback = refreshCallback;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Course Add/Drop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel infoLabel = new JLabel("<html><p><b>Course Add/Drop</b> controls whether students " +
                "may add or drop course registrations from the system.</p>" +
                "<p>When disabled, students can still view registrations but cannot make changes.</p></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(infoLabel, gbc);

        statusLabel = new JLabel();
        updateStatusLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 1;
        contentPanel.add(statusLabel, gbc);

        JButton enableButton = new JButton("Enable Add/Drop");
        enableButton.setBackground(new Color(33, 150, 243)); // blue-ish
        enableButton.setForeground(Color.WHITE);
        enableButton.setPreferredSize(new Dimension(200, 40));
        enableButton.addActionListener(e -> toggleAddDrop(true));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        contentPanel.add(enableButton, gbc);

        JButton disableButton = new JButton("Disable Add/Drop");
        disableButton.setBackground(new Color(244, 67, 54)); // red-ish
        disableButton.setForeground(Color.WHITE);
        disableButton.setPreferredSize(new Dimension(200, 40));
        disableButton.addActionListener(e -> toggleAddDrop(false));
        gbc.gridx = 1;
        contentPanel.add(disableButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void toggleAddDrop(boolean enable) {
        String error = settingsService.toggleAddDrop(enable);

        if (error == null) {
            String message = enable ? "Course Add/Drop ENABLED. Students may register/drop courses." :
                    "Course Add/Drop DISABLED. Students cannot change registrations.";
            MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), message);
            updateStatusLabel();
            refreshCallback.run();
        } else {
            MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), error);
        }
    }

    private void updateStatusLabel() {
        boolean addDropAllowed = settingsService.isAddDropEnabled();
        if (addDropAllowed) {
            statusLabel.setText("Current Status: Add/Drop ENABLED");
            statusLabel.setForeground(new Color(76, 175, 80));
        } else {
            statusLabel.setText("Current Status: Add/Drop DISABLED");
            statusLabel.setForeground(new Color(255, 87, 34));
        }
    }
}
