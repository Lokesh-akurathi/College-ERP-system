package edu.univ.erp.data;

import edu.univ.erp.domain.Settings;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;

public class SettingsStore {

    public String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setSetting(String key, String value) {
        String checkSql = "SELECT COUNT(*) FROM settings WHERE key = ?";
        String insertSql = "INSERT INTO settings (key, value) VALUES (?, ?)";
        String updateSql = "UPDATE settings SET value = ? WHERE key = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, key);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;

                if (exists) {
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setString(1, value);
                        pstmt.setString(2, key);
                        return pstmt.executeUpdate() > 0;
                    }
                } else {
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setString(1, key);
                        pstmt.setString(2, value);
                        return pstmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
