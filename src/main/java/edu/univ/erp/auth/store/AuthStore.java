package edu.univ.erp.auth.store;

import edu.univ.erp.domain.User;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

public class AuthStore {

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, role, status, last_login FROM users_auth WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                Timestamp lastLogin = rs.getTimestamp("last_login");
                if (lastLogin != null) {
                    user.setLastLogin(lastLogin.toLocalDateTime());
                }
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPasswordHash(int userId) {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(String username, String role, String passwordHash) {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status) VALUES (?, ?, ?, 'ACTIVE')";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, role);
            pstmt.setString(3, passwordHash);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users_auth WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean changePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String findUsernameByUserId(int userId) {
    String sql = "SELECT username FROM users_auth WHERE user_id = ?";
    try (Connection conn = DatabaseConfig.getAuthConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("username");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

}
