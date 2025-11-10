// package edu.univ.erp.data;

// import edu.univ.erp.domain.Instructor;
// import edu.univ.erp.util.DatabaseConfig;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class InstructorStore {

//     public Instructor findById(int userId) {
//         String sql = "SELECT i.user_id, i.department, u.username " +
//                      "FROM instructors i " +
//                      "JOIN users_auth u ON i.user_id = u.user_id " +
//                      "WHERE i.user_id = ?";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, userId);
//             ResultSet rs = pstmt.executeQuery();
            
//             if (rs.next()) {
//                 Instructor instructor = new Instructor();
//                 instructor.setUserId(rs.getInt("user_id"));
//                 instructor.setDepartment(rs.getString("department"));
//                 instructor.setUsername(rs.getString("username"));
//                 return instructor;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }

//     public boolean create(Instructor instructor) {
//         String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, instructor.getUserId());
//             pstmt.setString(2, instructor.getDepartment());
            
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     public List<Instructor> findAll() {
//         List<Instructor> instructors = new ArrayList<>();
//         String sql = "SELECT user_id, department FROM instructors";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              Statement stmt = conn.createStatement();
//              ResultSet rs = stmt.executeQuery(sql)) {
            
//             while (rs.next()) {
//                 Instructor instructor = new Instructor();
//                 instructor.setUserId(rs.getInt("user_id"));
//                 instructor.setDepartment(rs.getString("department"));
//                 instructors.add(instructor);
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return instructors;
//     }
// }
package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorStore {

    public Instructor findById(int userId) {
        String sql = "SELECT user_id, department FROM instructors WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getInt("user_id"));
                instructor.setDepartment(rs.getString("department"));
                // username will be fetched separately from AuthStore
                return instructor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Instructor instructor) {
        String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, instructor.getUserId());
            pstmt.setString(2, instructor.getDepartment());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Instructor> findAll() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT user_id, department FROM instructors";

        try (Connection conn = DatabaseConfig.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getInt("user_id"));
                instructor.setDepartment(rs.getString("department"));
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }
}
