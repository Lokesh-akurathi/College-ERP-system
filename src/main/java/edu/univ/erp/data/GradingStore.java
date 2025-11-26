
package edu.univ.erp.data;

import edu.univ.erp.domain.GradeStats;
import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.service.GradeImportData;
import edu.univ.erp.util.DatabaseConfig;
import edu.univ.erp.service.GradeImportData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradingStore {

    /**
     * Retrieves the list of grading criteria (components) for a given section.
     */
    public List<GradingCriteria> findBySectionId(int sectionId) {
        List<GradingCriteria> criteriaList = new ArrayList<>();
        String sql = "SELECT criteria_id, section_id, component_name, weight_percentage, display_order " +
                     "FROM grading_criteria WHERE section_id = ? ORDER BY display_order";

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                GradingCriteria criteria = new GradingCriteria();
                criteria.setCriteriaId(rs.getInt("criteria_id"));
                criteria.setSectionId(rs.getInt("section_id"));
                criteria.setComponentName(rs.getString("component_name"));
                criteria.setWeightPercentage(rs.getDouble("weight_percentage"));
                criteria.setDisplayOrder(rs.getInt("display_order"));
                criteriaList.add(criteria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return criteriaList;
    }

    /**
     * Deletes all grading criteria associated with a section.
     */
    public void deleteBySectionId(int sectionId) throws SQLException {
        String sql = "DELETE FROM grading_criteria WHERE section_id = ?";
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Inserts a new grading criterion. Used during saveOrUpdate.
     */
    public void insertCriteria(Connection conn, GradingCriteria criteria) throws SQLException {
        String sql = "INSERT INTO grading_criteria (section_id, component_name, weight_percentage, display_order) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, criteria.getSectionId());
            pstmt.setString(2, criteria.getComponentName());
            pstmt.setDouble(3, criteria.getWeightPercentage());
            pstmt.setInt(4, criteria.getDisplayOrder());
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves or updates the entire set of grading criteria for a section (atomic transaction).
     */
    public boolean saveOrUpdateCriteria(int sectionId, List<GradingCriteria> criteriaList) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getErpConnection();
            conn.setAutoCommit(false);

            deleteBySectionId(sectionId); 
            
            for (GradingCriteria criteria : criteriaList) {
                insertCriteria(conn, criteria);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving grading criteria for section " + sectionId + ": " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Retrieves a list of final scores for calculation purposes (e.g., median).
     * NOTE: This method appears to be incorrectly querying for 'final_score' from a 'grades' table 
     * but using a column alias 'score' in the select list. It should be reviewed, 
     * but is kept as-is to preserve existing code structure.
     */
    public List<Double> getFinalScoresBySection(int sectionId) {
        List<Double> scores = new ArrayList<>();

        String sql = 
        "SELECT g.final_score " + // Changed 'g.score' to 'g.final_score' based on context, assuming 'grades' table has final score
        "FROM grades g " +
        "JOIN enrollment e ON g.enrollment_id = e.enrollment_id " + // Fixed table name 'enrollments' to 'enrollment'
        "WHERE e.section_id = ?";


        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                scores.add(rs.getDouble("final_score"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }

    /**
     * Maps a numeric score to a letter grade based on a defined scale.
     */
    private String mapScoreToGrade(double score) {
        if (score >= 90) return "A";
        if (score >= 85) return "A-";
        if (score >= 80) return "B";
        if (score >= 75) return "B-";
        if (score >= 70) return "C";
        if (score >= 65) return "C-";
        if (score >= 60) return "D";
        return "F";
    }

    /**
     * IMPLEMENTATION FOR EXPORT/IMPORT FEATURE:
     * Retrieves the student roster and their individual component scores for a section, 
     * aggregating component scores into a single GradeStats object per student.
     */
    // In edu/univ/erp/data/GradingStore.java

// ... (other methods) ...

/**
 * Retrieves the student roster, individual component scores, and final score 
 * for a section by joining enrollments, students, and grades (twice).
 */
// public List<GradeStats> findGradesBySectionId(int sectionId) {
//     Map<Integer, GradeStats> statsMap = new HashMap<>();

//     String sql = 
//         "SELECT " +
//         "    e.enrollment_id, s.roll_no, s.first_name, s.last_name, " + 
//         "    g.component AS criteria_name, g.score, " + // Component scores
//         "    gf.score AS final_score " +               // Final numeric score (aliased)
//         "FROM " +
//         "    enrollments e " + 
//         "INNER JOIN " +
//         "    students s ON e.student_id = s.user_id " + // Correct join confirmed by schema
//         "LEFT JOIN " +
//         "    grades g ON e.enrollment_id = g.enrollment_id AND g.component != 'FINAL' " + // Component scores only
//         "LEFT JOIN " +
//         "    grades gf ON e.enrollment_id = gf.enrollment_id AND gf.component = 'FINAL' " + // Final score only
//         "WHERE " +
//         "    e.section_id = ? " +
//         "ORDER BY s.roll_no"; // Ensure a clean, ordered roster
public List<GradeStats> findGradesBySectionId(int sectionId) {
    Map<Integer, GradeStats> statsMap = new HashMap<>();

    String sql = 
        "SELECT " +
        "    e.enrollment_id, s.roll_no, s.first_name, s.last_name, " + 
        "    LOWER(TRIM(g.component)) AS criteria_name, g.score, " + // <-- FIX: Trim component name for clean matching
        "    gf.score AS final_score " +               
        "FROM " +
        "    enrollments e " + 
        "INNER JOIN " +
        "    students s ON e.student_id = s.user_id " + 
        "LEFT JOIN " +
        "    grades g ON e.enrollment_id = g.enrollment_id AND g.component != 'FINAL' " + 
        "LEFT JOIN " +
        "    grades gf ON e.enrollment_id = gf.enrollment_id AND gf.component = 'FINAL' " + 
        "WHERE " +
        "    e.section_id = ? " +
        "ORDER BY s.roll_no";

    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, sectionId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            int enrollmentId = rs.getInt("enrollment_id");
            
            GradeStats currentStats = statsMap.get(enrollmentId);
            if (currentStats == null) {
                
                String rollNo = rs.getString("roll_no");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                
                // Read the final score pulled from the 'gf' alias
                Double finalScore = (Double) rs.getObject("final_score"); 
                
                // Letter grade must be null here as it's calculated in the service layer
                String letterGrade = null; 
                
                currentStats = new GradeStats(
                    enrollmentId,
                    rollNo,
                    firstName,
                    lastName,
                    new HashMap<>(),
                    finalScore,
                    letterGrade
                );
                statsMap.put(enrollmentId, currentStats);
            }

            // Aggregate component score data (g.component and g.score)
           String criteriaName = rs.getString("criteria_name"); 
            if (criteriaName != null && rs.getObject("score") != null) {
                // Ensure we are not accidentally processing a 'FINAL' component score here
                if (!"FINAL".equals(criteriaName)) { 
                    double score = rs.getDouble("score");
                    currentStats.getComponentScores().put(criteriaName, score);
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Database error in findGradesBySectionId: " + e.getMessage());
        e.printStackTrace();
        return new ArrayList<>(); 
    }
    
    return new ArrayList<>(statsMap.values());
}
public void updateGrades(List<GradeImportData> dataList) throws SQLException {
        // SQL for component scores (updates or inserts into the 'grades' table)
        String upsertComponentSql = 
            "INSERT INTO grades (enrollment_id, component, score) " +
            "VALUES (?, ?, ?) " +
            "ON CONFLICT (enrollment_id, component) DO UPDATE " +
            "SET score = EXCLUDED.score";
            
        // SQL for final score/grade (updates or inserts the 'FINAL' component)
        // Since letter_grade isn't stored, we only update the score. 
        // We will treat FINAL as a component score update.
        String upsertFinalSql = upsertComponentSql; 

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmtComponent = conn.prepareStatement(upsertComponentSql);
             PreparedStatement pstmtFinal = conn.prepareStatement(upsertFinalSql)) {

            conn.setAutoCommit(false); // Start transaction

            for (GradeImportData data : dataList) {
                int enrollmentId = data.getEnrollmentId();
                
                // 1. Update/Insert Component Scores (e.g., HOMEWORK, QUIZ)
                for (Map.Entry<String, Double> entry : data.getComponentScores().entrySet()) {
                    pstmtComponent.setInt(1, enrollmentId);
                    // Use all caps to match the database's component case (HOMEWORK, QUIZ)
                    pstmtComponent.setString(2, entry.getKey().toUpperCase()); 
                    pstmtComponent.setDouble(3, entry.getValue());
                    pstmtComponent.addBatch();
                }

                // 2. Update/Insert Final Score
                Double finalScore = data.getFinalScore();
                if (finalScore != null) {
                    pstmtFinal.setInt(1, enrollmentId);
                    pstmtFinal.setString(2, "FINAL"); // Component name for final score
                    pstmtFinal.setDouble(3, finalScore);
                    pstmtFinal.addBatch();
                }
            }

            // Execute all updates/inserts in batches
            pstmtComponent.executeBatch();
            pstmtFinal.executeBatch();
            
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Database error during grade import: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw or handle as necessary
        }
    }
}