package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminSettingsPanel extends JPanel {
    public AdminSettingsPanel(Runnable refreshCallback) {
        setLayout(new BorderLayout());
        MaintenanceModePanel maintenancePanel = new MaintenanceModePanel(refreshCallback);
        AddDropControlPanel addDropPanel = new AddDropControlPanel(refreshCallback);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        splitPanel.add(maintenancePanel);
        splitPanel.add(addDropPanel);

        add(splitPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
