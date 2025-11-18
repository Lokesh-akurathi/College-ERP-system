// package edu.univ.erp.data;

// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.util.DatabaseConfig;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class EnrollmentStore {

//     public Enrollment findById(int enrollmentId) {
//         String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
//                      "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
//                      "s.day_time as section_day_time, s.room as section_room, " +
//                      "u.username as instructor_name " +
//                      "FROM enrollments e " +
//                      "JOIN sections sec ON e.section_id = sec.section_id " +
//                      "JOIN courses c ON sec.course_id = c.course_id " +
//                      "JOIN sections s ON e.section_id = s.section_id " +
//                      "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
//                      "WHERE e.enrollment_id = ?";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, enrollmentId);
//             ResultSet rs = pstmt.executeQuery();
            
//             if (rs.next()) {
//                 return mapResultSetToEnrollment(rs);
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }

//     public List<Enrollment> findByStudent(int studentId) {
//         List<Enrollment> enrollments = new ArrayList<>();
//         String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
//                      "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
//                      "s.day_time as section_day_time, s.room as section_room, " +
//                      "u.username as instructor_name " +
//                      "FROM enrollments e " +
//                      "JOIN sections s ON e.section_id = s.section_id " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
//                      "WHERE e.student_id = ? AND e.status = 'ACTIVE' " +
//                      "ORDER BY c.code";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, studentId);
//             ResultSet rs = pstmt.executeQuery();
            
//             while (rs.next()) {
//                 enrollments.add(mapResultSetToEnrollment(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return enrollments;
//     }

//     public List<Enrollment> findBySection(int sectionId) {
//         List<Enrollment> enrollments = new ArrayList<>();
//         String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
//                      "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
//                      "s.day_time as section_day_time, s.room as section_room, " +
//                      "u.username as instructor_name " +
//                      "FROM enrollments e " +
//                      "JOIN sections s ON e.section_id = s.section_id " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
//                      "WHERE e.section_id = ? AND e.status = 'ACTIVE' " +
//                      "ORDER BY e.student_id";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, sectionId);
//             ResultSet rs = pstmt.executeQuery();
            
//             while (rs.next()) {
//                 enrollments.add(mapResultSetToEnrollment(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return enrollments;
//     }

//     public boolean exists(int studentId, int sectionId) {
//         String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ACTIVE'";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, studentId);
//             pstmt.setInt(2, sectionId);
//             ResultSet rs = pstmt.executeQuery();
            
//             if (rs.next()) {
//                 return rs.getInt(1) > 0;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public boolean create(Enrollment enrollment) {
//         String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ACTIVE')";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
//             pstmt.setInt(1, enrollment.getStudentId());
//             pstmt.setInt(2, enrollment.getSectionId());
            
//             int affected = pstmt.executeUpdate();
//             if (affected > 0) {
//                 ResultSet rs = pstmt.getGeneratedKeys();
//                 if (rs.next()) {
//                     enrollment.setEnrollmentId(rs.getInt(1));
//                 }
//                 return true;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public boolean drop(int enrollmentId) {
//         String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ?";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, enrollmentId);
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
//         Enrollment enrollment = new Enrollment();
//         enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
//         enrollment.setStudentId(rs.getInt("student_id"));
//         enrollment.setSectionId(rs.getInt("section_id"));
//         enrollment.setStatus(rs.getString("status"));
//         Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
//         if (enrolledAt != null) {
//             enrollment.setEnrolledAt(enrolledAt.toLocalDateTime());
//         }
//         enrollment.setCourseCode(rs.getString("course_code"));
//         enrollment.setCourseTitle(rs.getString("course_title"));
//         enrollment.setCourseCredits(rs.getInt("course_credits"));
//         enrollment.setSectionDayTime(rs.getString("section_day_time"));
//         enrollment.setSectionRoom(rs.getString("section_room"));
//         enrollment.setInstructorName(rs.getString("instructor_name"));
//         return enrollment;
//     }
// }
// package edu.univ.erp.data;

// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.util.DatabaseConfig;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class EnrollmentStore {

//     public Enrollment findById(int enrollmentId) {
//         String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
//                      "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
//                      "s.day_time AS section_day_time, s.room AS section_room, s.instructor_id AS instructor_id " +
//                      "FROM enrollments e " +
//                      "JOIN sections s ON e.section_id = s.section_id " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "WHERE e.enrollment_id = ?";

//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             pstmt.setInt(1, enrollmentId);
//             ResultSet rs = pstmt.executeQuery();

//             if (rs.next()) {
//                 return mapResultSetToEnrollment(rs);
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }

//     public List<Enrollment> findByStudent(int studentId) {
//         List<Enrollment> enrollments = new ArrayList<>();
//         String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
//                      "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
//                      "s.day_time AS section_day_time, s.room AS section_room, s.instructor_id AS instructor_id " +
//                      "FROM enrollments e " +
//                      "JOIN sections s ON e.section_id = s.section_id " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "WHERE e.student_id = ? AND e.status = 'ACTIVE' " +
//                      "ORDER BY c.code";

//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             pstmt.setInt(1, studentId);
//             ResultSet rs = pstmt.executeQuery();

//             while (rs.next()) {
//                 enrollments.add(mapResultSetToEnrollment(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return enrollments;
//     }

//     public List<Enrollment> findBySection(int sectionId) {
//         List<Enrollment> enrollments = new ArrayList<>();
//         String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
//                      "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
//                      "s.day_time AS section_day_time, s.room AS section_room, s.instructor_id AS instructor_id " +
//                      "FROM enrollments e " +
//                      "JOIN sections s ON e.section_id = s.section_id " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "WHERE e.section_id = ? AND e.status = 'ACTIVE' " +
//                      "ORDER BY e.student_id";

//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             pstmt.setInt(1, sectionId);
//             ResultSet rs = pstmt.executeQuery();

//             while (rs.next()) {
//                 enrollments.add(mapResultSetToEnrollment(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return enrollments;
//     }

//     public boolean exists(int studentId, int sectionId) {
//         String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ACTIVE'";

//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             pstmt.setInt(1, studentId);
//             pstmt.setInt(2, sectionId);
//             ResultSet rs = pstmt.executeQuery();

//             if (rs.next()) {
//                 return rs.getInt(1) > 0;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public boolean create(Enrollment enrollment) {
//         String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ACTIVE')";

//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

//             pstmt.setInt(1, enrollment.getStudentId());
//             pstmt.setInt(2, enrollment.getSectionId());

//             int affected = pstmt.executeUpdate();
//             if (affected > 0) {
//                 ResultSet rs = pstmt.getGeneratedKeys();
//                 if (rs.next()) {
//                     enrollment.setEnrollmentId(rs.getInt(1));
//                 }
//                 return true;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public boolean drop(int enrollmentId) {
//         String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ?";

//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {

//             pstmt.setInt(1, enrollmentId);
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
//         Enrollment enrollment = new Enrollment();
//         enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
//         enrollment.setStudentId(rs.getInt("student_id"));
//         enrollment.setSectionId(rs.getInt("section_id"));
//         enrollment.setStatus(rs.getString("status"));
//         Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
//         if (enrolledAt != null) {
//             enrollment.setEnrolledAt(enrolledAt.toLocalDateTime());
//         }
//         enrollment.setCourseCode(rs.getString("course_code"));
//         enrollment.setCourseTitle(rs.getString("course_title"));
//         enrollment.setCourseCredits(rs.getInt("course_credits"));
//         enrollment.setSectionDayTime(rs.getString("section_day_time"));
//         enrollment.setSectionRoom(rs.getString("section_room"));
//         enrollment.setInstructorId(rs.getInt("instructor_id"));  // store instructor ID only
//         return enrollment;
//     }
// }

package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentStore {

    private static final String BASE_QUERY =
            "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            "COALESCE(STRING_AGG(st.day_of_week || ' ' || " +
            "       TO_CHAR(st.start_time, 'HH24:MI') || '-' || TO_CHAR(st.end_time, 'HH24:MI'), ', '), '') AS section_day_time, " +
            "s.room AS section_room, s.instructor_id AS instructor_id " +
            "FROM enrollments e " +
            "JOIN sections s ON e.section_id = s.section_id " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "LEFT JOIN section_times st ON s.section_id = st.section_id ";

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
        enrollment.setInstructorId(rs.getInt("instructor_id"));

        return enrollment;
    }


    // -------------------------------------------------------------
    // Find by enrollment ID
    // -------------------------------------------------------------
    public Enrollment findById(int enrollmentId) {
        String sql = BASE_QUERY +
                "WHERE e.enrollment_id = ? " +
                "GROUP BY e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
                "c.code, c.title, c.credits, s.room, s.instructor_id";

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


    // -------------------------------------------------------------
    // Get all active enrollments for a student
    // -------------------------------------------------------------
    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();

        String sql = BASE_QUERY +
                "WHERE e.student_id = ? AND e.status = 'ACTIVE' " +
                "GROUP BY e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
                "c.code, c.title, c.credits, s.room, s.instructor_id " +
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


    // -------------------------------------------------------------
    // Find enrollments for a section
    // -------------------------------------------------------------
    public List<Enrollment> findBySection(int sectionId) {
        List<Enrollment> list = new ArrayList<>();

        String sql = BASE_QUERY +
                "WHERE e.section_id = ? AND e.status = 'ACTIVE' " +
                "GROUP BY e.enrollment_id, e.student_id, e.section_id, e.status, e.enrolled_at, " +
                "c.code, c.title, c.credits, s.room, s.instructor_id " +
                "ORDER BY e.student_id";

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


    // -------------------------------------------------------------
    // Check if student is enrolled
    // -------------------------------------------------------------
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


    // -------------------------------------------------------------
    // Create enrollment
    // -------------------------------------------------------------
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


    // -------------------------------------------------------------
    // Drop enrollment
    // -------------------------------------------------------------
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
}
