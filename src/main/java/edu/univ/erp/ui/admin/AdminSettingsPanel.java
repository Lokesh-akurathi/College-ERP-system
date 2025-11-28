package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminSettingsPanel extends JPanel {
    public AdminSettingsPanel(Runnable refreshCallback) {
        setLayout(new BorderLayout());
        MaintenanceModePanel maintenancePanel = new MaintenanceModePanel(refreshCallback);
        AddDropControlPanel addDropPanel = new AddDropControlPanel(refreshCallback);
        add(maintenancePanel,BorderLayout.WEST);
        add(addDropPanel,BorderLayout.EAST);

    }
}
