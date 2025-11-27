package edu.univ.erp.auth.store;

import edu.univ.erp.domain.User;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

public class AuthStore {

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        Timestamp lockoutTs = rs.getTimestamp("lockout_until");
        if (lockoutTs != null) {
            user.setLockoutUntil(lockoutTs.toLocalDateTime());
        }
        
        return user;
    }

    public User findByUsername(String username) {
        
        String sql = "SELECT user_id, username, role, status, last_login, failed_login_attempts, lockout_until FROM users_auth WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapUserFromResultSet(rs); 
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

    
    public void incrementLoginAttempts(int userId) {
        String sql = "UPDATE users_auth SET failed_login_attempts = failed_login_attempts + 1 WHERE user_id = ?";
        executeSimpleUpdate(sql, userId);
    }
    
    public void lockUser(int userId, LocalDateTime lockoutTime) {
        String sql = "UPDATE users_auth SET status = 'LOCKED', failed_login_attempts = failed_login_attempts + 1, lockout_until = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(lockoutTime));
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error locking user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean resetLockout(int userId) {
        String sql = "UPDATE users_auth SET status = 'ACTIVE', failed_login_attempts = 0, lockout_until = NULL WHERE user_id = ?";
        return executeSimpleUpdate(sql, userId);
    }
    
    public void resetLoginAttempts(int userId) {
        String sql = "UPDATE users_auth SET failed_login_attempts = 0 WHERE user_id = ?";
        executeSimpleUpdate(sql, userId);
    }
    
    public boolean unlockUser(int userId) {
        return resetLockout(userId);
    }
    


    private boolean executeSimpleUpdate(String sql, int userId) {
         try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error during simple update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}