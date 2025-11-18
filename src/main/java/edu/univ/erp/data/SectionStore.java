//package edu.univ.erp.data;

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
import edu.univ.erp.domain.SectionTime;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionStore {

    /** ------------------------ FETCH SINGLE SECTION ------------------------- */

    public Section findById(int sectionId) {
        String sql =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.room, s.capacity, " +
            "s.semester, s.year, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status='ACTIVE') AS enrolled " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "WHERE s.section_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Section section = mapResultSetToSection(rs);

                // New: fetch structured schedule
                section.setTimes(fetchSectionTimes(sectionId));

                // Fetch instructor name from Auth DB
                section.setInstructorName(fetchInstructorName(section.getInstructorId()));
                return section;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /** ------------------------ FETCH ALL SECTIONS ------------------------- */

    public List<Section> findAll() {
        List<Section> sections = new ArrayList<>();

        String sql =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.room, s.capacity, " +
            "s.semester, s.year, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status='ACTIVE') AS enrolled " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "ORDER BY c.code, s.section_id";

        try (Connection conn = DatabaseConfig.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Section section = mapResultSetToSection(rs);

                section.setTimes(fetchSectionTimes(section.getSectionId()));
                section.setInstructorName(fetchInstructorName(section.getInstructorId()));

                sections.add(section);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;
    }


    /** ------------------------ FETCH SECTIONS BY INSTRUCTOR ------------------------- */

    public List<Section> findByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();

        String sql =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.room, s.capacity, " +
            "s.semester, s.year, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status='ACTIVE') AS enrolled " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            "WHERE s.instructor_id = ? " +
            "ORDER BY c.code";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Section section = mapResultSetToSection(rs);

                section.setTimes(fetchSectionTimes(section.getSectionId()));
                section.setInstructorName(fetchInstructorName(section.getInstructorId()));

                sections.add(section);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;
    }


    /** ----------------------------- CREATE SECTION ----------------------------- */

    public boolean create(Section section) {
        String sql =
            "INSERT INTO sections (course_id, instructor_id, room, capacity, semester, year) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, section.getCourseId());
            pstmt.setInt(2, section.getInstructorId());
            pstmt.setString(3, section.getRoom());
            pstmt.setInt(4, section.getCapacity());
            pstmt.setString(5, section.getSemester());
            pstmt.setInt(6, section.getYear());

            int ok = pstmt.executeUpdate();
            if (ok == 0) return false;

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int sectionId = rs.getInt(1);
                section.setSectionId(sectionId);

                // Insert structured times
                for (SectionTime time : section.getTimes()) {
                    insertSectionTime(sectionId, time);
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    /** ----------------------------- UPDATE SECTION ----------------------------- */

    // public boolean update(Section section) {
    //     String sql =
    //         "UPDATE sections SET instructor_id = ?, room = ?, capacity = ?, semester = ?, year = ? " +
    //         "WHERE section_id = ?";

    //     try (Connection conn = DatabaseConfig.getErpConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {

    //         pstmt.setInt(1, section.getInstructorId());
    //         pstmt.setString(2, section.getRoom());
    //         pstmt.setInt(3, section.getCapacity());
    //         pstmt.setString(4, section.getSemester());
    //         pstmt.setInt(5, section.getYear());
    //         pstmt.setInt(6, section.getSectionId());

    //         boolean updated = pstmt.executeUpdate() > 0;

    //         if (!updated) return false;

    //         // Replace schedule: delete old + add new
    //         deleteSectionTimes(section.getSectionId());

    //         for (SectionTime time : section.getTimes()) {
    //             insertSectionTime(section.getSectionId(), time);
    //         }

    //         return true;

    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
public boolean update(Section section) {
    String sql =
        "UPDATE sections SET course_id = ?, instructor_id = ?, room = ?, capacity = ?, semester = ?, year = ? " +
        "WHERE section_id = ?";

    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, section.getCourseId());
        pstmt.setInt(2, section.getInstructorId());
        pstmt.setString(3, section.getRoom());
        pstmt.setInt(4, section.getCapacity());
        pstmt.setString(5, section.getSemester());
        pstmt.setInt(6, section.getYear());
        pstmt.setInt(7, section.getSectionId());

        boolean updated = pstmt.executeUpdate() > 0;

        if (!updated) return false;

        // Replace schedule: delete old + add new
        deleteSectionTimes(section.getSectionId());

        for (SectionTime time : section.getTimes()) {
            insertSectionTime(section.getSectionId(), time);
        }

        return true;

    // } catch (SQLException e) {
    //     e.printStackTrace();
    //     return false;
    // }
    } catch (SQLException e) {
    System.out.println("ERROR in update(): " + e.getMessage());
    e.printStackTrace();
    return false;
}
}


    /** ----------------------------- DELETE SECTION ----------------------------- */

    public boolean deleteSection(int sectionId) {
    // First delete schedule times
    deleteSectionTimes(sectionId);

    String sql = "DELETE FROM sections WHERE section_id = ?";

    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, sectionId);
        return pstmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}



    public boolean deleteSectionTimes(int sectionId) {
    String sql = "DELETE FROM section_times WHERE section_id = ?";

    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, sectionId);
        return pstmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public boolean canDeleteSection(int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1) == 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    /** ----------------------------- SCHEDULE HELPERS ----------------------------- */

    private List<SectionTime> fetchSectionTimes(int sectionId) {
        List<SectionTime> times = new ArrayList<>();

        String sql = "SELECT day_of_week, start_time, end_time FROM section_times WHERE section_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SectionTime t = new SectionTime(
                    rs.getString("day_of_week"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                );
                times.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return times;
    }

    private void insertSectionTime(int sectionId, SectionTime time) throws SQLException {
        String sql = "INSERT INTO section_times (section_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            pstmt.setString(2, time.getDayOfWeek());
             pstmt.setTime(3, java.sql.Time.valueOf(time.getStartTime() + ":00"));
        pstmt.setTime(4, java.sql.Time.valueOf(time.getEndTime() + ":00"));
            pstmt.executeUpdate();
        }
    }

    // private void deleteSectionTimes(int sectionId) throws SQLException {
    //     String sql = "DELETE FROM section_times WHERE section_id = ?";

    //     try (Connection conn = DatabaseConfig.getErpConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {

    //         pstmt.setInt(1, sectionId);
    //         pstmt.executeUpdate();
    //     }
    // }


    /** ----------------------------- MAPPING HELPERS ----------------------------- */

    private Section mapResultSetToSection(ResultSet rs) throws SQLException {
        Section section = new Section();
        section.setSectionId(rs.getInt("section_id"));
        section.setCourseId(rs.getInt("course_id"));
        section.setInstructorId(rs.getInt("instructor_id"));
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

    /** Fetch instructor name from AUTH DB */
    private String fetchInstructorName(int instructorId) {
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getString("username");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }


}
