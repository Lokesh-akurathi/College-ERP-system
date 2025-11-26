package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentStore {

    private static final String BASE_QUERY =
            "SELECT e.enrollment_id, " +
            "stu.roll_no AS roll_number, " +
            "stu.first_name || ' ' || stu.last_name AS student_name, " +
            "e.student_id, e.section_id, e.status, e.enrolled_at, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            "COALESCE(STRING_AGG(st.day_of_week || ' ' || " +
            "TO_CHAR(st.start_time, 'HH24:MI') || '-' || TO_CHAR(st.end_time, 'HH24:MI'), ', '), '') AS section_day_time, " +
            "s.room AS section_room, " +
            "(i.salutation || ' ' || i.first_name || ' ' || i.last_name) AS instructor_name " +
            "FROM enrollments e " +
            "JOIN students stu ON e.student_id = stu.user_id " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "JOIN instructors i ON s.instructor_id = i.user_id " +
            "LEFT JOIN section_times st ON s.section_id = st.section_id ";

    private static final String GROUP_BY =
            " GROUP BY e.enrollment_id, stu.roll_no, stu.first_name, stu.last_name, " +
            "e.student_id, e.section_id, e.status, e.enrolled_at, " +
            "c.code, c.title, c.credits, s.room, i.salutation, i.first_name, i.last_name ";

    
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();

        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setSectionId(rs.getInt("section_id"));
        enrollment.setStatus(rs.getString("status"));
        enrollment.setRollNumber(rs.getString("roll_number"));
        enrollment.setStudentName(rs.getString("student_name"));
        enrollment.setInstructorName(rs.getString("instructor_name"));

        Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
        if (enrolledAt != null) {
            enrollment.setEnrolledAt(enrolledAt.toLocalDateTime());
        }

        enrollment.setCourseCode(rs.getString("course_code"));
        enrollment.setCourseTitle(rs.getString("course_title"));
        enrollment.setCourseCredits(rs.getInt("course_credits"));
        enrollment.setSectionDayTime(rs.getString("section_day_time"));
        enrollment.setSectionRoom(rs.getString("section_room"));

        return enrollment;
    }

   
    public Enrollment findById(int enrollmentId) {
        String sql = BASE_QUERY + " WHERE e.enrollment_id = ? " + GROUP_BY;

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

        String sql = BASE_QUERY +
                " WHERE e.student_id = ? AND e.status = 'ACTIVE' " +
                GROUP_BY +
                " ORDER BY c.code";

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
        List<Enrollment> list = new ArrayList<>();

        String sql = BASE_QUERY +
                " WHERE e.section_id = ? AND e.status = 'ACTIVE' " +
                GROUP_BY +
                " ORDER BY stu.roll_no";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToEnrollment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    
    public boolean exists(int studentId, int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, sectionId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public boolean create(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ACTIVE')";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getSectionId());

            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    enrollment.setEnrollmentId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    
    public boolean drop(int enrollmentId) {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, enrollmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public String findRollNumberByEnrollmentId(int enrollmentId) {
        String sql = 
            "SELECT s.roll_no " +
            "FROM enrollments e " +
            "INNER JOIN students s ON e.student_id = s.user_id " +
            "WHERE e.enrollment_id = ?";
            
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
       
                return rs.getString("roll_no").trim(); 
            }
            
        } catch (SQLException e) {
            System.err.println("Database error in findRollNumberByEnrollmentId: " + e.getMessage());
            e.printStackTrace();
        }
        return null; 
    }

}
