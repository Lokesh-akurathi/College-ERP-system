package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentStore {

    public Enrollment findById(int enrollmentId) {
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
                     "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                     "s.day_time as section_day_time, s.room as section_room, " +
                     "u.username as instructor_name " +
                     "FROM enrollments e " +
                     "JOIN sections sec ON e.section_id = sec.section_id " +
                     "JOIN courses c ON sec.course_id = c.course_id " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
                     "WHERE e.enrollment_id = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEnrollment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
                     "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                     "s.day_time as section_day_time, s.room as section_room, " +
                     "u.username as instructor_name " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
                     "WHERE e.student_id = ? AND e.status = 'ACTIVE' " +
                     "ORDER BY c.code";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public List<Enrollment> findBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
                     "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                     "s.day_time as section_day_time, s.room as section_room, " +
                     "u.username as instructor_name " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
                     "WHERE e.section_id = ? AND e.status = 'ACTIVE' " +
                     "ORDER BY e.student_id";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public boolean exists(int studentId, int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, sectionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean create(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ACTIVE')";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getSectionId());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    enrollment.setEnrollmentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean drop(int enrollmentId) {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setSectionId(rs.getInt("section_id"));
        enrollment.setStatus(rs.getString("status"));
        Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
        if (enrolledAt != null) {
            enrollment.setEnrolledAt(enrolledAt.toLocalDateTime());
        }
        enrollment.setCourseCode(rs.getString("course_code"));
        enrollment.setCourseTitle(rs.getString("course_title"));
        enrollment.setCourseCredits(rs.getInt("course_credits"));
        enrollment.setSectionDayTime(rs.getString("section_day_time"));
        enrollment.setSectionRoom(rs.getString("section_room"));
        enrollment.setInstructorName(rs.getString("instructor_name"));
        return enrollment;
    }
}
