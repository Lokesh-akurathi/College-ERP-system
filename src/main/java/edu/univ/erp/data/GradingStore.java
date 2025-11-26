package edu.univ.erp.data;


import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;

import java.util.List;

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
    public List<Double> getFinalScoresBySection(int sectionId) {
    List<Double> scores = new ArrayList<>();

    String sql = 
    "SELECT g.score " +
    "FROM grades g " +
    "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
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
    

}