package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeStore {

    public List<Grade> findByEnrollment(int enrollmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT grade_id, enrollment_id, component, score, final_grade " +
                     "FROM grades WHERE enrollment_id = ? ORDER BY component";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    public Grade findByEnrollmentAndComponent(int enrollmentId, String component) {
        String sql = "SELECT grade_id, enrollment_id, component, score, final_grade " +
                     "FROM grades WHERE enrollment_id = ? AND component = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            pstmt.setString(2, component);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGrade(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Grade grade) {
        String sql = "INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, grade.getEnrollmentId());
            pstmt.setString(2, grade.getComponent());
            if (grade.getScore() != null) {
                pstmt.setDouble(3, grade.getScore());
            } else {
                pstmt.setNull(3, Types.DOUBLE);
            }
            pstmt.setString(4, grade.getFinalGrade());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    grade.setGradeId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Grade grade) {
        String sql = "UPDATE grades SET score = ?, final_grade = ? WHERE grade_id = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (grade.getScore() != null) {
                pstmt.setDouble(1, grade.getScore());
            } else {
                pstmt.setNull(1, Types.DOUBLE);
            }
            pstmt.setString(2, grade.getFinalGrade());
            pstmt.setInt(3, grade.getGradeId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrCreate(int enrollmentId, String component, Double score) {
        Grade existing = findByEnrollmentAndComponent(enrollmentId, component);
        
        if (existing != null) {
            existing.setScore(score);
            return update(existing);
        } else {
            Grade newGrade = new Grade();
            newGrade.setEnrollmentId(enrollmentId);
            newGrade.setComponent(component);
            newGrade.setScore(score);
            return create(newGrade);
        }
    }

    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setComponent(rs.getString("component"));
        double score = rs.getDouble("score");
        if (!rs.wasNull()) {
            grade.setScore(score);
        }
        grade.setFinalGrade(rs.getString("final_grade"));
        return grade;
    }
}
