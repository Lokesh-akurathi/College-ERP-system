package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentStore {

    public Student findById(int userId) {
        String sql = "SELECT s.user_id, s.roll_no, s.program, s.year, u.username " +
                     "FROM students s " +
                     "JOIN users_auth u ON s.user_id = u.user_id " +
                     "WHERE s.user_id = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setUserId(rs.getInt("user_id"));
                student.setRollNo(rs.getString("roll_no"));
                student.setProgram(rs.getString("program"));
                student.setYear(rs.getInt("year"));
                student.setUsername(rs.getString("username"));
                return student;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Student student) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getRollNo());
            pstmt.setString(3, student.getProgram());
            pstmt.setInt(4, student.getYear());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT user_id, roll_no, program, year FROM students";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student();
                student.setUserId(rs.getInt("user_id"));
                student.setRollNo(rs.getString("roll_no"));
                student.setProgram(rs.getString("program"));
                student.setYear(rs.getInt("year"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}
