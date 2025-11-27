
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

   
    public void deleteBySectionId(int sectionId) throws SQLException {
        String sql = "DELETE FROM grading_criteria WHERE section_id = ?";
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            pstmt.executeUpdate();
        }
    }
    
   
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
    
   
    public List<Double> getFinalScoresBySection(int sectionId) {
        List<Double> scores = new ArrayList<>();

        String sql = 
        "SELECT g.final_score " + 
        "FROM grades g " +
        "JOIN enrollment e ON g.enrollment_id = e.enrollment_id " + 
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

  
public List<GradeStats> findGradesBySectionId(int sectionId) {
    Map<Integer, GradeStats> statsMap = new HashMap<>();

    String sql = 
        "SELECT " +
        "    e.enrollment_id, s.roll_no, s.first_name, s.last_name, " + 
        "    LOWER(TRIM(g.component)) AS criteria_name, g.score, " + 
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
                
              
                Double finalScore = (Double) rs.getObject("final_score"); 
                
             
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

           String criteriaName = rs.getString("criteria_name"); 
            if (criteriaName != null && rs.getObject("score") != null) {
               
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
        String upsertComponentSql = 
            "INSERT INTO grades (enrollment_id, component, score) " +
            "VALUES (?, ?, ?) " +
            "ON CONFLICT (enrollment_id, component) DO UPDATE " +
            "SET score = EXCLUDED.score";
            
       
        String upsertFinalSql = upsertComponentSql; 

        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmtComponent = conn.prepareStatement(upsertComponentSql);
             PreparedStatement pstmtFinal = conn.prepareStatement(upsertFinalSql)) {

            conn.setAutoCommit(false); 

            for (GradeImportData data : dataList) {
                int enrollmentId = data.getEnrollmentId();
                
               
                for (Map.Entry<String, Double> entry : data.getComponentScores().entrySet()) {
                    pstmtComponent.setInt(1, enrollmentId);
                    
                    pstmtComponent.setString(2, entry.getKey().toUpperCase()); 
                    pstmtComponent.setDouble(3, entry.getValue());
                    pstmtComponent.addBatch();
                }

              
                Double finalScore = data.getFinalScore();
                if (finalScore != null) {
                    pstmtFinal.setInt(1, enrollmentId);
                    pstmtFinal.setString(2, "FINAL");
                    pstmtFinal.setDouble(3, finalScore);
                    pstmtFinal.addBatch();
                }
            }

           
            pstmtComponent.executeBatch();
            pstmtFinal.executeBatch();
            
            conn.commit(); 
        } catch (SQLException e) {
            System.err.println("Database error during grade import: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        }
    }
}