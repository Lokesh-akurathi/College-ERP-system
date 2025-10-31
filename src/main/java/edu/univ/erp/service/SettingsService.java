package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.data.SettingsStore;

public class SettingsService {
    private final SettingsStore settingsStore;

    public SettingsService() {
        this.settingsStore = new SettingsStore();
    }

    public String toggleMaintenanceMode(boolean enable) {
        if (!AccessControl.canAccessAdminFeatures()) {
            return AccessControl.getAccessDeniedMessage();
        }

        String value = enable ? "true" : "false";
        if (settingsStore.setSetting("maintenance_on", value)) {
            return null;
        } else {
            return "Failed to toggle maintenance mode.";
        }
    }

    public boolean isMaintenanceMode() {
        return AccessControl.isMaintenanceMode();
    }
}
