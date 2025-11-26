package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.SectionTime;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionStore {

   
    private String formatSchedule(List<SectionTime> times) {
        if (times == null || times.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (SectionTime t : times) {
            sb.append(t.getDayOfWeek())
              .append(" ")
              .append(t.getStartTime())
              .append("-")
              .append(t.getEndTime())
              .append(", ");
        }
    
        return sb.substring(0, sb.length() - 2);
    }

    public Section findById(int sectionId) {
        String sql =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.room, s.capacity, " +
            "s.semester, s.year, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            
           
            "COALESCE(i.salutation || ' ', '') || i.first_name || ' ' || i.last_name AS instructor_name, " +
            
            "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status='ACTIVE') AS enrolled " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            
         
            "JOIN instructors i ON s.instructor_id = i.user_id " +
            
            "WHERE s.section_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Section section = map(rs);

                List<SectionTime> times = fetchSectionTimes(sectionId);
                section.setTimes(times);
                section.setDayTime(formatSchedule(times));

                return section;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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

   
    public List<Section> findAll() {
        List<Section> sections = new ArrayList<>();

        String sql =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.room, s.capacity, " +
            "s.semester, s.year, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            
            
            "COALESCE(i.salutation || ' ', '') || i.first_name || ' ' || i.last_name AS instructor_name, " +

            "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status='ACTIVE') AS enrolled " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            
            
            "JOIN instructors i ON s.instructor_id = i.user_id " +
            
            "ORDER BY c.code, s.section_id";

        try (Connection conn = DatabaseConfig.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Section section = map(rs);

                List<SectionTime> times = fetchSectionTimes(section.getSectionId());
                section.setTimes(times);
                section.setDayTime(formatSchedule(times));

               
                sections.add(section);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;
    }

   
    public List<Section> findByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();

        String sql =
            "SELECT s.section_id, s.course_id, s.instructor_id, s.room, s.capacity, " +
            "s.semester, s.year, " +
            "c.code AS course_code, c.title AS course_title, c.credits AS course_credits, " +
            
            
            "COALESCE(i.salutation || ' ', '') || i.first_name || ' ' || i.last_name AS instructor_name, " +
            
            "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status='ACTIVE') AS enrolled " +
            "FROM sections s " +
            "JOIN courses c ON s.course_id = c.course_id " +
            
            
            "JOIN instructors i ON s.instructor_id = i.user_id " +
            
            "WHERE s.instructor_id = ? " +
            "ORDER BY c.code";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, instructorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Section section = map(rs);

                List<SectionTime> times = fetchSectionTimes(section.getSectionId());
                section.setTimes(times);
                section.setDayTime(formatSchedule(times));

                
                sections.add(section);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;
    }

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

                for (SectionTime t : section.getTimes()) {
                    insertSectionTime(sectionId, t);
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

   
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

            boolean ok = pstmt.executeUpdate() > 0;
            if (!ok) return false;

            
            deleteSectionTimes(section.getSectionId());
            for (SectionTime t : section.getTimes()) {
                insertSectionTime(section.getSectionId(), t);
            }

            return true;

        } catch (SQLException e) {
            System.out.println("ERROR in update(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
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
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<SectionTime> fetchSectionTimes(int sectionId) {
        List<SectionTime> times = new ArrayList<>();

        String sql =
            "SELECT day_of_week, start_time, end_time " +
            "FROM section_times WHERE section_id = ? ORDER BY id";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                times.add(new SectionTime(
                    rs.getString("day_of_week"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return times;
    }

    private void insertSectionTime(int sectionId, SectionTime time) throws SQLException {
        String sql =
            "INSERT INTO section_times (section_id, day_of_week, start_time, end_time) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sectionId);
            pstmt.setString(2, time.getDayOfWeek());

           
            String startTimeStr = time.getStartTime();
            String endTimeStr = time.getEndTime();

          
            java.time.LocalTime localStartTime = java.time.LocalTime.parse(startTimeStr);
            java.time.LocalTime localEndTime = java.time.LocalTime.parse(endTimeStr);

            pstmt.setTime(3, Time.valueOf(localStartTime));
            pstmt.setTime(4, Time.valueOf(localEndTime));

            pstmt.executeUpdate();
        } 
    }

   
    private Section map(ResultSet rs) throws SQLException {
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
        
      
        section.setInstructorName(rs.getString("instructor_name")); 
        
        return section;
    }
    
    
}