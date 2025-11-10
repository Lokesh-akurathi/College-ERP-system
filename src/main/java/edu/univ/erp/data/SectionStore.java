// package edu.univ.erp.data;

// import edu.univ.erp.domain.Section;
// import edu.univ.erp.util.DatabaseConfig;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class SectionStore {

//     public Section findById(int sectionId) {
//         String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
//                      "s.capacity, s.semester, s.year, " +
//                      "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
//                      "u.username as instructor_name, " +
//                      "COUNT(e.enrollment_id) as enrolled " +
//                      "FROM sections s " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
//                      "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status = 'ACTIVE' " +
//                      "WHERE s.section_id = ? " +
//                      "GROUP BY s.section_id, c.course_id, u.username";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, sectionId);
//             ResultSet rs = pstmt.executeQuery();
            
//             if (rs.next()) {
//                 return mapResultSetToSection(rs);
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }

//     public List<Section> findAll() {
//         List<Section> sections = new ArrayList<>();
//         String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
//                      "s.capacity, s.semester, s.year, " +
//                      "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
//                      "u.username as instructor_name, " +
//                      "COUNT(e.enrollment_id) as enrolled " +
//                      "FROM sections s " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
//                      "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status = 'ACTIVE' " +
//                      "GROUP BY s.section_id, c.course_id, u.username " +
//                      "ORDER BY c.code, s.day_time";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              Connection authConn = DatabaseConfig.getAuthConnection();
//              Statement stmt = conn.createStatement();
//              ResultSet rs = stmt.executeQuery(sql)) {
            
//             while (rs.next()) {
//                 sections.add(mapResultSetToSection(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return sections;
//     }

//     public List<Section> findByInstructor(int instructorId) {
//         List<Section> sections = new ArrayList<>();
//         String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
//                      "s.capacity, s.semester, s.year, " +
//                      "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
//                      "u.username as instructor_name, " +
//                      "COUNT(e.enrollment_id) as enrolled " +
//                      "FROM sections s " +
//                      "JOIN courses c ON s.course_id = c.course_id " +
//                      "LEFT JOIN users_auth u ON s.instructor_id = u.user_id " +
//                      "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status = 'ACTIVE' " +
//                      "WHERE s.instructor_id = ? " +
//                      "GROUP BY s.section_id, c.course_id, u.username " +
//                      "ORDER BY c.code";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, instructorId);
//             ResultSet rs = pstmt.executeQuery();
            
//             while (rs.next()) {
//                 sections.add(mapResultSetToSection(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return sections;
//     }

//     public boolean create(Section section) {
//         String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
//                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
//             pstmt.setInt(1, section.getCourseId());
//             pstmt.setInt(2, section.getInstructorId());
//             pstmt.setString(3, section.getDayTime());
//             pstmt.setString(4, section.getRoom());
//             pstmt.setInt(5, section.getCapacity());
//             pstmt.setString(6, section.getSemester());
//             pstmt.setInt(7, section.getYear());
            
//             int affected = pstmt.executeUpdate();
//             if (affected > 0) {
//                 ResultSet rs = pstmt.getGeneratedKeys();
//                 if (rs.next()) {
//                     section.setSectionId(rs.getInt(1));
//                 }
//                 return true;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public boolean update(Section section) {
//         String sql = "UPDATE sections SET instructor_id = ?, day_time = ?, room = ?, " +
//                      "capacity = ?, semester = ?, year = ? WHERE section_id = ?";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, section.getInstructorId());
//             pstmt.setString(2, section.getDayTime());
//             pstmt.setString(3, section.getRoom());
//             pstmt.setInt(4, section.getCapacity());
//             pstmt.setString(5, section.getSemester());
//             pstmt.setInt(6, section.getYear());
//             pstmt.setInt(7, section.getSectionId());
            
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     private Section mapResultSetToSection(ResultSet rs) throws SQLException {
//         Section section = new Section();
//         section.setSectionId(rs.getInt("section_id"));
//         section.setCourseId(rs.getInt("course_id"));
//         section.setInstructorId(rs.getInt("instructor_id"));
//         section.setDayTime(rs.getString("day_time"));
//         section.setRoom(rs.getString("room"));
//         section.setCapacity(rs.getInt("capacity"));
//         section.setSemester(rs.getString("semester"));
//         section.setYear(rs.getInt("year"));
//         section.setCourseCode(rs.getString("course_code"));
//         section.setCourseTitle(rs.getString("course_title"));
//         section.setCourseCredits(rs.getInt("course_credits"));
//         section.setInstructorName(rs.getString("instructor_name"));
//         section.setEnrolled(rs.getInt("enrolled"));
//         return section;
//     }
// }
package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionStore {

    /** Find a single section by its ID (ERP DB only) */
    public Section findById(int sectionId) {
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
                     "s.capacity, s.semester, s.year, " +
                     "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
                     "COUNT(e.enrollment_id) AS enrolled " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status = 'ACTIVE' " +
                     "WHERE s.section_id = ? " +
                     "GROUP BY s.section_id, c.course_id";

        try (Connection erpConn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = erpConn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Section section = mapResultSetToSection(rs);
                // Fetch instructor name from Auth DB
                section.setInstructorName(fetchInstructorName(section.getInstructorId()));
                return section;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Retrieve all sections (ERP DB) */
    public List<Section> findAll() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
                     "s.capacity, s.semester, s.year, " +
                     "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
                     "COUNT(e.enrollment_id) AS enrolled " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status = 'ACTIVE' " +
                     "GROUP BY s.section_id, c.course_id " +
                     "ORDER BY c.code, s.day_time";

        try (Connection erpConn = DatabaseConfig.getErpConnection();
             Statement stmt = erpConn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Section section = mapResultSetToSection(rs);
                section.setInstructorName(fetchInstructorName(section.getInstructorId()));
                sections.add(section);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    /** Retrieve all sections assigned to a particular instructor */
    public List<Section> findByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day_time, s.room, " +
                     "s.capacity, s.semester, s.year, " +
                     "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
                     "COUNT(e.enrollment_id) AS enrolled " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status = 'ACTIVE' " +
                     "WHERE s.instructor_id = ? " +
                     "GROUP BY s.section_id, c.course_id " +
                     "ORDER BY c.code";

        try (Connection erpConn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = erpConn.prepareStatement(sql)) {

            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Section section = mapResultSetToSection(rs);
                section.setInstructorName(fetchInstructorName(section.getInstructorId()));
                sections.add(section);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    /** Create a new section (ERP DB) */
    public boolean create(Section section) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection erpConn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = erpConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, section.getCourseId());
            pstmt.setInt(2, section.getInstructorId());
            pstmt.setString(3, section.getDayTime());
            pstmt.setString(4, section.getRoom());
            pstmt.setInt(5, section.getCapacity());
            pstmt.setString(6, section.getSemester());
            pstmt.setInt(7, section.getYear());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    section.setSectionId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Update section details (ERP DB) */
    public boolean update(Section section) {
        String sql = "UPDATE sections SET instructor_id = ?, day_time = ?, room = ?, " +
                     "capacity = ?, semester = ?, year = ? WHERE section_id = ?";

        try (Connection erpConn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = erpConn.prepareStatement(sql)) {

            pstmt.setInt(1, section.getInstructorId());
            pstmt.setString(2, section.getDayTime());
            pstmt.setString(3, section.getRoom());
            pstmt.setInt(4, section.getCapacity());
            pstmt.setString(5, section.getSemester());
            pstmt.setInt(6, section.getYear());
            pstmt.setInt(7, section.getSectionId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Helper: map ERP DB row → Section object (no instructor name yet) */
    private Section mapResultSetToSection(ResultSet rs) throws SQLException {
        Section section = new Section();
        section.setSectionId(rs.getInt("section_id"));
        section.setCourseId(rs.getInt("course_id"));
        section.setInstructorId(rs.getInt("instructor_id"));
        section.setDayTime(rs.getString("day_time"));
        section.setRoom(rs.getString("room"));
        section.setCapacity(rs.getInt("capacity"));
        section.setSemester(rs.getString("semester"));
        section.setYear(rs.getInt("year"));
        section.setCourseCode(rs.getString("course_code"));
        section.setCourseTitle(rs.getString("course_title"));
        section.setCourseCredits(rs.getInt("course_credits"));
        section.setEnrolled(rs.getInt("enrolled"));
        return section;
    }

    /** Helper: fetch instructor name from Auth DB using instructor_id */
    private String fetchInstructorName(int instructorId) {
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";
        try (Connection authConn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = authConn.prepareStatement(sql)) {

            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    // inside SectionStore.java
public boolean deleteSection(int sectionId) {
    String sql = "DELETE FROM sections WHERE section_id = ?";
    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, sectionId);
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
public boolean canDeleteSection(int sectionId) {
    String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, sectionId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

}
