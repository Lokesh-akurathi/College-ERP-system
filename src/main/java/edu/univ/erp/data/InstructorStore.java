package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorStore {


    public Instructor findById(int userId) {
        String sql = "SELECT user_id, salutation, first_name, last_name, department " +
                     "FROM instructors WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getInt("user_id"));
                instructor.setSalutation(rs.getString("salutation"));
                instructor.setFirstName(rs.getString("first_name"));
                instructor.setLastName(rs.getString("last_name"));
                instructor.setDepartment(rs.getString("department"));
                return instructor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

   
    public boolean create(Instructor instructor) {
        String sql = "INSERT INTO instructors (user_id, salutation, first_name, last_name, department) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, instructor.getUserId());
            pstmt.setString(2, instructor.getSalutation());
            pstmt.setString(3, instructor.getFirstName());
            pstmt.setString(4, instructor.getLastName());
            pstmt.setString(5, instructor.getDepartment());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Instructor> findAll() {
        List<Instructor> instructors = new ArrayList<>();

        String sql = "SELECT user_id, salutation, first_name, last_name, department FROM instructors";

        try (Connection conn = DatabaseConfig.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setUserId(rs.getInt("user_id"));
                instructor.setSalutation(rs.getString("salutation"));
                instructor.setFirstName(rs.getString("first_name"));
                instructor.setLastName(rs.getString("last_name"));
                instructor.setDepartment(rs.getString("department"));
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }
}
