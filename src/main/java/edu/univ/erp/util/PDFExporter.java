// package edu.univ.erp.util;

// import edu.univ.erp.domain.Enrollment;
// import edu.univ.erp.domain.Grade;
// import edu.univ.erp.service.GradeService;

// import com.itextpdf.text.Document;
// import com.itextpdf.text.Paragraph;
// import com.itextpdf.text.Phrase;
// import com.itextpdf.text.pdf.PdfPCell;
// import com.itextpdf.text.pdf.PdfPTable;
// import com.itextpdf.text.pdf.PdfWriter;
// import com.itextpdf.text.DocumentException;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.FontFactory;

// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.util.List;

// public class PDFExporter {
    
//     private final GradeService gradeService;

//     // Define standard fonts for the PDF
//     private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.UNDERLINE);
//     private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
//     private static final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
//     private static final Font SUMMARY_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

//     public PDFExporter() {
//         this.gradeService = new GradeService();
//     }

//     /**
//      * Exports a simplified academic transcript to a PDF file, including final grades and CGPA.
//      * @param enrollments The list of all enrollments for the student.
//      * @param filePath The path where the PDF should be saved (e.g., "transcript.pdf").
//      * @return true on success, false on failure.
//      */
//     public boolean exportTranscript(List<Enrollment> enrollments, String filePath) {
        
//         // Ensure the file path ends with .pdf
//         if (!filePath.toLowerCase().endsWith(".pdf")) {
//             filePath += ".pdf";
//         }

//         // Variables for CGPA Calculation
//         double totalGradePoints = 0.0;
//         int totalCreditsAttempted = 0;

//         Document document = new Document();

//         try {
//             // 1. Initialize the PDF Writer
//             PdfWriter.getInstance(document, new FileOutputStream(filePath));
//             document.open();
            
//             // 2. Add Title and Student Info (Placeholder)
//             document.add(new Paragraph("Official Student Transcript", TITLE_FONT));
//             document.add(new Paragraph("Student ID: [STUDENT_ID_PLACEHOLDER]", DATA_FONT));
//             document.add(new Paragraph(" ")); // Blank line

//             // 3. Create the table structure (6 columns)
//             PdfPTable table = new PdfPTable(6);
//             table.setWidthPercentage(100);
            
//             // Define column widths for better layout
//             table.setWidths(new float[]{1.5f, 3f, 1f, 1.5f, 1f, 1.5f});
            
//             // Add Header Cells
//             String[] header = {"Course Code", "Course Title", "Credits", "Final Score", "Grade", "Grade Point"};
//             for (String col : header) {
//                 PdfPCell cell = new PdfPCell(new Phrase(col, HEADER_FONT));
//                 cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//                 table.addCell(cell);
//             }

//             // 4. Iterate and populate the table
//             for (Enrollment enrollment : enrollments) {
//                 List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
                
//                 String finalScoreStr = "-";
//                 String finalGrade = "-";
//                 double gradePoint = 0.0;
//                 int credits = enrollment.getCourseCredits();
                
//                 // Find the FINAL score component
//                 for (Grade grade : grades) {
//                     if ("FINAL".equals(grade.getComponent()) && grade.getScore() != null) {
//                         double score = grade.getScore();
//                         finalScoreStr = String.format("%.2f", score);
//                         finalGrade = gradeService.getLetterGrade(score);
//                         gradePoint = calculateGradePoint(finalGrade);
                        
//                         // Accumulate for CGPA only if the course has a recorded final grade
//                         totalGradePoints += (gradePoint * credits);
//                         totalCreditsAttempted += credits;
                        
//                         break;
//                     }
//                 }

//                 // Add data rows to the PDF table
//                 table.addCell(new Phrase(enrollment.getCourseCode(), DATA_FONT));
//                 table.addCell(new Phrase(enrollment.getCourseTitle(), DATA_FONT));
//                 table.addCell(new Phrase(String.valueOf(credits), DATA_FONT));
//                 table.addCell(new Phrase(finalScoreStr, DATA_FONT));
//                 table.addCell(new Phrase(finalGrade, DATA_FONT));
//                 table.addCell(new Phrase(String.format("%.2f", gradePoint), DATA_FONT));
//             }

//             document.add(table);
//             document.add(new Paragraph(" ")); // Blank line

//             // 5. Add CGPA Summary
//             double cgpa = 0.0;
//             if (totalCreditsAttempted > 0) {
//                 cgpa = totalGradePoints / totalCreditsAttempted;
//             }

//             document.add(new Paragraph("-- CGPA Summary --", SUMMARY_FONT));
//             document.add(new Paragraph("Total Credits Attempted: " + totalCreditsAttempted, DATA_FONT));
//             document.add(new Paragraph("Cumulative GPA (CGPA): " + String.format("%.2f", cgpa), SUMMARY_FONT));
//             document.add(new Paragraph("Grading Scale: A=10.0, A-=9.0, B+=8.0, B=7.0, B-=6.0, C+=5.0, C=4.5, C-=4.0, D=4.0, F=0.0", DATA_FONT));


//             // 6. Close the document to save the file
//             document.close();
//             return true;

//         } catch (DocumentException | IOException e) {
//             System.err.println("Error generating PDF: " + e.getMessage());
//             e.printStackTrace();
//             return false;
//         }
//     }

//     /**
//      * Helper method to convert letter grade to the custom 10-point scale.
//      * Scale: A=10, A-=9, B+=8, B=7, B-=6, C+=5, C=4.5, C-=4, D=4, F=0
//      */
//     private double calculateGradePoint(String grade) {
//         switch (grade) {
//             case "A": return 10.0;
//             case "A-": return 9.0;
//             case "B": return 8.0;
//             case "B-": return 7.0;
//             case "C": return 6.0;
//             case "C-": return 5.0;
//             case "D": return 4.0; 
//             case "F": 
//             default: return 0.0;
//         }
//     }
// }

package edu.univ.erp.util;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.GradeService;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFExporter {
    
    private final GradeService gradeService;

    // Define standard fonts for the PDF
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.UNDERLINE);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font SUMMARY_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

    public PDFExporter() {
        this.gradeService = new GradeService();
    }

    /**
     * Exports a simplified academic transcript to a PDF file, including final grades and CGPA.
     * @param enrollments The list of all enrollments for the student.
     * @param filePath The path where the PDF should be saved (e.g., "transcript.pdf").
     * @return true on success, false on failure.
     */
    public boolean exportTranscript(List<Enrollment> enrollments, String filePath) {
        
        if (enrollments == null || enrollments.isEmpty()) {
            return false;
        }

        // Ensure the file path ends with .pdf
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        // Use the Student ID from the first enrollment for the header (assuming all enrollments belong to the same student)
        String rollNumber = enrollments.get(0).getRollNumber(); 
        
        // Variables for CGPA Calculation
        double totalGradePoints = 0.0;
        int totalCreditsAttempted = 0;

        Document document = new Document();

        try {
            // 1. Initialize the PDF Writer
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // 2. Add Title and Student Info
            document.add(new Paragraph("Official Student Transcript", TITLE_FONT));
            // UPDATED: Use Student ID from the enrollment list
            document.add(new Paragraph("Roll Number: " + rollNumber, DATA_FONT)); 
            document.add(new Paragraph(" ")); // Blank line

            // 3. Create the table structure (5 columns, "Final Score" removed)
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Define column widths for better layout (5 columns now)
            table.setWidths(new float[]{1.5f, 4f, 1f, 1f, 1.5f});
            
            // Add Header Cells (Removed "Final Score")
            String[] header = {"Course Code", "Course Title", "Credits", "Grade", "Grade Point"};
            for (String col : header) {
                PdfPCell cell = new PdfPCell(new Phrase(col, HEADER_FONT));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);
            }

            // 4. Iterate and populate the table
            for (Enrollment enrollment : enrollments) {
                List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
                
                String finalGrade = "-";
                double gradePoint = 0.0;
                int credits = enrollment.getCourseCredits();
                
                // Find the FINAL score component
                for (Grade grade : grades) {
                    if ("FINAL".equals(grade.getComponent()) && grade.getScore() != null) {
                        double score = grade.getScore();
                        finalGrade = gradeService.getLetterGrade(score);
                        gradePoint = calculateGradePoint(finalGrade);
                        
                        // Accumulate for CGPA only if the course has a recorded final grade
                        totalGradePoints += (gradePoint * credits);
                        totalCreditsAttempted += credits;
                        
                        break;
                    }
                }

                // Add data rows to the PDF table (Removed finalScoreStr)
                table.addCell(new Phrase(enrollment.getCourseCode(), DATA_FONT));
                table.addCell(new Phrase(enrollment.getCourseTitle(), DATA_FONT));
                table.addCell(new Phrase(String.valueOf(credits), DATA_FONT));
                table.addCell(new Phrase(finalGrade, DATA_FONT));
                table.addCell(new Phrase(String.format("%.2f", gradePoint), DATA_FONT));
            }

            document.add(table);
            document.add(new Paragraph(" ")); // Blank line

            // 5. Add CGPA Summary
            double cgpa = 0.0;
            if (totalCreditsAttempted > 0) {
                cgpa = totalGradePoints / totalCreditsAttempted;
            }

            document.add(new Paragraph("-- CGPA Summary --", SUMMARY_FONT));
            document.add(new Paragraph("Total Credits Attempted: " + totalCreditsAttempted, DATA_FONT));
            document.add(new Paragraph("Cumulative GPA (CGPA): " + String.format("%.2f", cgpa), SUMMARY_FONT));
            document.add(new Paragraph("Grading Scale: A=10.0, A-=9.0, B=8.0, B-=7.0, C=6.0, C-=5.0, D=4.0, F=0.0", DATA_FONT));


            // 6. Close the document to save the file
            document.close();
            return true;

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to convert letter grade to the custom 10-point scale based on the provided image.
     * Scale: A=10, A-=9, B=8, B-=7, C=6, C-=5, D=4, F=0
     */
    private double calculateGradePoint(String grade) {
        switch (grade) {
            case "A": return 10.0;
            case "A-": return 9.0;
            case "B": return 8.0;
            case "B-": return 7.0;
            case "C": return 6.0;
            case "C-": return 5.0;
            case "D": return 4.0;
            case "F": 
            default: return 0.0;
        }
    }
}