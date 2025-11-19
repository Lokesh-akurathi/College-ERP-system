// package edu.univ.erp.data;

// import edu.univ.erp.domain.Grade;
// import edu.univ.erp.util.DatabaseConfig;

// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class GradeStore {

//     public List<Grade> findByEnrollment(int enrollmentId) {
//         List<Grade> grades = new ArrayList<>();
//         String sql = "SELECT grade_id, enrollment_id, component, score, final_grade " +
//                      "FROM grades WHERE enrollment_id = ? ORDER BY component";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, enrollmentId);
//             ResultSet rs = pstmt.executeQuery();
            
//             while (rs.next()) {
//                 grades.add(mapResultSetToGrade(rs));
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return grades;
//     }

//     public Grade findByEnrollmentAndComponent(int enrollmentId, String component) {
//         String sql = "SELECT grade_id, enrollment_id, component, score, final_grade " +
//                      "FROM grades WHERE enrollment_id = ? AND component = ?";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             pstmt.setInt(1, enrollmentId);
//             pstmt.setString(2, component);
//             ResultSet rs = pstmt.executeQuery();
            
//             if (rs.next()) {
//                 return mapResultSetToGrade(rs);
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }

//     public boolean create(Grade grade) {
//         String sql = "INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES (?, ?, ?, ?)";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
//             pstmt.setInt(1, grade.getEnrollmentId());
//             pstmt.setString(2, grade.getComponent());
//             if (grade.getScore() != null) {
//                 pstmt.setDouble(3, grade.getScore());
//             } else {
//                 pstmt.setNull(3, Types.DOUBLE);
//             }
//             pstmt.setString(4, grade.getFinalGrade());
            
//             int affected = pstmt.executeUpdate();
//             if (affected > 0) {
//                 ResultSet rs = pstmt.getGeneratedKeys();
//                 if (rs.next()) {
//                     grade.setGradeId(rs.getInt(1));
//                 }
//                 return true;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }

//     public boolean update(Grade grade) {
//         String sql = "UPDATE grades SET score = ?, final_grade = ? WHERE grade_id = ?";
        
//         try (Connection conn = DatabaseConfig.getErpConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
//             if (grade.getScore() != null) {
//                 pstmt.setDouble(1, grade.getScore());
//             } else {
//                 pstmt.setNull(1, Types.DOUBLE);
//             }
//             pstmt.setString(2, grade.getFinalGrade());
//             pstmt.setInt(3, grade.getGradeId());
            
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     public boolean updateOrCreate(int enrollmentId, String component, Double score) {
//         Grade existing = findByEnrollmentAndComponent(enrollmentId, component);
        
//         if (existing != null) {
//             existing.setScore(score);
//             return update(existing);
//         } else {
//             Grade newGrade = new Grade();
//             newGrade.setEnrollmentId(enrollmentId);
//             newGrade.setComponent(component);
//             newGrade.setScore(score);
//             return create(newGrade);
//         }
//     }

//     private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
//         Grade grade = new Grade();
//         grade.setGradeId(rs.getInt("grade_id"));
//         grade.setEnrollmentId(rs.getInt("enrollment_id"));
//         grade.setComponent(rs.getString("component"));
//         double score = rs.getDouble("score");
//         if (!rs.wasNull()) {
//             grade.setScore(score);
//         }
//         grade.setFinalGrade(rs.getString("final_grade"));
//         return grade;
//     }
// }
package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeStore {

    // --- 1. findByEnrollment FIX ---
    public List<Grade> findByEnrollment(int enrollmentId) {
        List<Grade> grades = new ArrayList<>();
        // Removed 'final_grade' from SELECT list
        String sql = "SELECT grade_id, enrollment_id, component, score " +
                     "FROM grades WHERE enrollment_id = ? ORDER BY component";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    // --- 2. findByEnrollmentAndComponent FIX ---
    public Grade findByEnrollmentAndComponent(int enrollmentId, String component) {
        // Removed 'final_grade' from SELECT list
        String sql = "SELECT grade_id, enrollment_id, component, score " +
                     "FROM grades WHERE enrollment_id = ? AND component = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            pstmt.setString(2, component);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGrade(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- 3. create FIX ---
    // public boolean create(Grade grade) {
    //     // Removed 'final_grade' from INSERT column list and '?' from VALUES list
    //     String sql = "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?)";
        
    //     try (Connection conn = DatabaseConfig.getErpConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
    //         pstmt.setInt(1, grade.getEnrollmentId());
    //         pstmt.setString(2, grade.getComponent());
    //         if (grade.getScore() != null) {
    //             pstmt.setDouble(3, grade.getScore());
    //         } else {
    //             pstmt.setNull(3, Types.DOUBLE);
    //         }
    //         // REMOVED: pstmt.setString(4, grade.getFinalGrade());
            
    //         int affected = pstmt.executeUpdate();
    //         if (affected > 0) {
    //             ResultSet rs = pstmt.getGeneratedKeys();
    //             if (rs.next()) {
    //                 grade.setGradeId(rs.getInt(1));
    //             }
    //             return true;
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return false;
    // }
   // Inside GradeStore.java

public boolean create(Grade grade) {
    String sql = "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?)";
    int gradeId = 0; // Initialize grade ID

    try (Connection conn = DatabaseConfig.getErpConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        // 1. Set Parameters
        pstmt.setInt(1, grade.getEnrollmentId());
        pstmt.setString(2, grade.getComponent());
        
        if (grade.getScore() != null) {
            pstmt.setDouble(3, grade.getScore());
        } else {
            pstmt.setNull(3, Types.DOUBLE);
        }
        
        // 2. Execute
        int affected = pstmt.executeUpdate();
        
        // 3. Process Keys (Only if rows were affected)
        if (affected > 0) {
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                gradeId = rs.getInt(1);
            }
        } else {
            // Log that zero rows were affected on insert (the runtime issue we were debugging)
            System.err.println("DEBUG: Grade CREATE Failed. Rows affected was 0 for Enrollment: " + grade.getEnrollmentId());
            return false; // Explicit return on logical failure
        }
        
        // 4. Set ID and Return Success
        grade.setGradeId(gradeId);
        return true; // Explicit return on success

    } catch (SQLException e) {
        // Log the exception and return failure
        e.printStackTrace();
        return false; // Explicit return on SQL exception failure
    }
    
    // NOTE: If the method is structured this way, this line IS UNREACHABLE and should be deleted.
    // The IDE is likely highlighting this line because the paths inside the try/catch already return a value.
    // If you remove the previous 'return false;' lines and ONLY rely on the final one, it creates confusion.
    // Solution: DELETE the final 'return false;' or ensure it's outside a nested scope.
}

    // --- 4. update FIX (This only works for updating score. If you need to update letter grade, you must update the Grade object in GradeService.) ---
    public boolean update(Grade grade) {
        // Removed 'final_grade = ?' from SET clause. The 'FINAL' component score row
        // should now handle the letter grade within the GradeService logic.
        String sql = "UPDATE grades SET score = ? WHERE grade_id = ?";
        
        try (Connection conn = DatabaseConfig.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (grade.getScore() != null) {
                pstmt.setDouble(1, grade.getScore());
            } else {
                pstmt.setNull(1, Types.DOUBLE);
            }
            // REMOVED: pstmt.setString(2, grade.getFinalGrade());
            pstmt.setInt(2, grade.getGradeId()); // Shifted index from 3 to 2
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- 5. updateOrCreate (No SQL changes needed here, as it calls create/update) ---
    public boolean updateOrCreate(int enrollmentId, String component, Double score) {
        Grade existing = findByEnrollmentAndComponent(enrollmentId, component);
        
        if (existing != null) {
            existing.setScore(score);
            return update(existing);
        } else {
            Grade newGrade = new Grade();
            newGrade.setEnrollmentId(enrollmentId);
            newGrade.setComponent(component);
            newGrade.setScore(score);
            // Note: When creating a new score component (like 'HOMEWORK'), finalGrade will be null, 
            // which is fine since the 'create' method no longer uses it.
            return create(newGrade);
        }
    }

    // --- 6. mapResultSetToGrade FIX ---
    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setComponent(rs.getString("component"));
        double score = rs.getDouble("score");
        if (!rs.wasNull()) {
            grade.setScore(score);
        }
        // REMOVED: grade.setFinalGrade(rs.getString("final_grade"));
        
        // --- CRITICAL TEMPORARY FIX ---
        // Since GradeService.computeFinalGrade still sets the letter grade via setFinalGrade()
        // and GradeEntryPanel expects to read it, we must ensure GradeService is updated.
        // For now, we rely on the GradeService fix for the 'FINAL' component.
        
        return grade;
    }
}