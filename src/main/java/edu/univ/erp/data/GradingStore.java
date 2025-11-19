package edu.univ.erp.data;

import edu.univ.erp.domain.GradingCriteria;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradingStore {

    /** Fetches all grading criteria for a given section. */
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

    /** Deletes all criteria for a section, preparing for update. */
    public void deleteBySectionId(int sectionId) throws SQLException {
        String sql = "DELETE FROM grading_criteria WHERE section_id = ?";
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            pstmt.executeUpdate();
        }
    }

    /** Inserts a single grading component. */
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

    /** Core method to replace all criteria for a section in a transaction. */
    public boolean saveOrUpdateCriteria(int sectionId, List<GradingCriteria> criteriaList) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getErpConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete existing criteria
            deleteBySectionId(sectionId); 
            
            // 2. Insert new criteria
            for (GradingCriteria criteria : criteriaList) {
                insertCriteria(conn, criteria);
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving grading criteria for section " + sectionId + ": " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
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