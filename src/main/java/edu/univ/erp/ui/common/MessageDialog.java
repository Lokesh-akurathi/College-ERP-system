package edu.univ.erp.ui.common;

import javax.swing.*;

public class MessageDialog {
    
    public static void showSuccess(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showError(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showWarning(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    public static void showInfo(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirm(JFrame parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
