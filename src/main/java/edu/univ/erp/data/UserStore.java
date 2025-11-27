
package edu.univ.erp.data;

import edu.univ.erp.domain.User; 
import edu.univ.erp.util.DatabaseConfig; 
import java.sql.*;
import java.time.LocalDateTime; 
import java.util.ArrayList;
import java.util.List;

public class UserStore {

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id")); 
        user.setRole(rs.getString("role").trim());
        user.setUsername(rs.getString("username").trim());
        
        String status = rs.getString("status");
        user.setStatus(status == null ? "" : status.trim());
        
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        Timestamp lockoutTs = rs.getTimestamp("lockout_until");
        if (lockoutTs != null) {
            user.setLockoutUntil(lockoutTs.toLocalDateTime());
        }
        
        return user;
    }


    public List<User> findAllByType(String role) { 
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT user_id, username, role, status, failed_login_attempts, lockout_until FROM users_auth WHERE role = ?"; 

        try (Connection authConn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = authConn.prepareStatement(sql)) {

            pstmt.setString(1, role.toUpperCase()); 
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = mapUserFromResultSet(rs);
                
                if ("STUDENT".equalsIgnoreCase(role) || "INSTRUCTOR".equalsIgnoreCase(role)) {
                    fetchProfileDetails(user);
                }
                
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving users by type (Two-step fetch failed): " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    private void fetchProfileDetails(User user) {
        String role = user.getRole();
        String tableName = role.equalsIgnoreCase("STUDENT") ? "students" : "instructors";
        
        String sql = "SELECT first_name, last_name FROM " + tableName + " WHERE user_id = ?";

        try (Connection erpConn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = erpConn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching profile details for " + user.getUserId() + ": " + e.getMessage());
            e.printStackTrace();
        }
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
            System.err.println("Database error fetching hash for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?"; 
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newHashedPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database error updating password for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public User findUserByUsername(String username) {
        String sql = "SELECT user_id, username, role, status, failed_login_attempts, lockout_until FROM users_auth WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error retrieving user by username: " + e.getMessage());
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

    public void resetLoginAttempts(int userId) {
        String sql = "UPDATE users_auth SET failed_login_attempts = 0 WHERE user_id = ?";
        executeSimpleUpdate(sql, userId);
    }

    public boolean resetLockout(int userId) {
        String sql = "UPDATE users_auth SET status = 'ACTIVE', failed_login_attempts = 0, lockout_until = NULL WHERE user_id = ?";
        return executeSimpleUpdate(sql, userId);
    }
    
    public boolean unlockUser(int userId) {
        return resetLockout(userId);
    }

    public void updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        executeSimpleUpdate(sql, userId);
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