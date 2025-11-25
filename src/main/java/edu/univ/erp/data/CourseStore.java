
package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseStore {

    

    public Course findById(int courseId) {
        String sql = "SELECT course_id, code, title, credits FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    

    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, code, title, credits FROM courses ORDER BY code";

        try (Connection conn = DatabaseConfig.getErpConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }


    

    public boolean create(Course course) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getCode());
            pstmt.setString(2, course.getTitle());
            pstmt.setInt(3, course.getCredits());

            int affected = pstmt.executeUpdate();
            if (affected == 0)
                return false;

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                course.setCourseId(rs.getInt(1));
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    

    public boolean updateCourse(int courseId, String newCode, String newTitle, int newCredits) {
        String sql = "UPDATE courses SET code = ?, title = ?, credits = ? WHERE course_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newCode);
            pstmt.setString(2, newTitle);
            pstmt.setInt(3, newCredits);
            pstmt.setInt(4, courseId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


   

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCode(rs.getString("code"));
        course.setTitle(rs.getString("title"));
        course.setCredits(rs.getInt("credits"));
        return course;
    }
}
