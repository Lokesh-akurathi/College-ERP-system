package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminSettingsPanel extends JPanel {
    public AdminSettingsPanel(Runnable refreshCallback) {

        setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
        MaintenanceModePanel maintenancePanel = new MaintenanceModePanel(refreshCallback);
        AddDropControlPanel addDropPanel = new AddDropControlPanel(refreshCallback);

        add(maintenancePanel);
        add(addDropPanel);
    }
}

