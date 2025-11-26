package edu.univ.erp.access;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.data.SettingsStore;

public class AccessControl {
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final SettingsStore settingsStore = new SettingsStore();

    public static boolean isMaintenanceMode() {
        String value = settingsStore.getSetting("maintenance_on");
        return "true".equalsIgnoreCase(value);
    }

    public static boolean isAddDropPeriod() {
        String value = settingsStore.getSetting("add_drop_period_on");
        return "true".equalsIgnoreCase(value);
    }

    public static boolean canModify() {
        if (isMaintenanceMode()) {
            String role = sessionManager.getCurrentRole();
            return "ADMIN".equals(role);
        }
        return true;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(sessionManager.getCurrentRole());
    }

    public static boolean isInstructor() {
        return "INSTRUCTOR".equals(sessionManager.getCurrentRole());
    }

    public static boolean isStudent() {
        return "STUDENT".equals(sessionManager.getCurrentRole());
    }

    public static boolean canAccessAdminFeatures() {
        return isAdmin();
    }

    public static boolean canAccessInstructorFeatures() {
        return isInstructor() || isAdmin();
    }

    public static boolean canAccessStudentFeatures() {
        return isStudent() || isAdmin();
    }

    public static String getMaintenanceDenialMessage() {
        return "System is in Maintenance Mode. Only viewing is allowed.";
    }

    public static String getAccessDeniedMessage() {
        return "Access Denied. You do not have permission for this action.";
    }

    public static String getAddDropDeniedMessage() {
        return "Add/Drop Period is over. Only viewing is allowed.";
    }
}
