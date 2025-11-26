
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


    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.UNDERLINE);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font SUMMARY_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

    public PDFExporter() {
        this.gradeService = new GradeService();
    }

   
    public boolean exportTranscript(List<Enrollment> enrollments, String filePath) {
        
        if (enrollments == null || enrollments.isEmpty()) {
            return false;
        }

        
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        
        String rollNumber = enrollments.get(0).getRollNumber(); 
        String studentName = enrollments.get(0).getStudentName();
        
        
        double totalGradePoints = 0.0;
        int totalCreditsAttempted = 0;

        Document document = new Document();

        try {
            
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
           
            document.add(new Paragraph("Official Student Transcript", TITLE_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Roll Number: " + rollNumber, DATA_FONT));
            document.add(new Paragraph("Student Name: " + studentName, DATA_FONT));
            document.add(new Paragraph(" ")); // Blank line


            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
  
            table.setWidths(new float[]{1.5f, 4f, 1f, 1f, 1.5f});
            

            String[] header = {"Course Code", "Course Title", "Credits", "Grade", "Grade Point"};
            for (String col : header) {
                PdfPCell cell = new PdfPCell(new Phrase(col, HEADER_FONT));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);
            }


            for (Enrollment enrollment : enrollments) {
                List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
                
                String finalGrade = "-";
                double gradePoint = 0.0;
                int credits = enrollment.getCourseCredits();
                

                for (Grade grade : grades) {
                    if ("FINAL".equals(grade.getComponent()) && grade.getScore() != null) {
                        double score = grade.getScore();
                        finalGrade = gradeService.getLetterGrade(score);
                        gradePoint = calculateGradePoint(finalGrade);
                        

                        totalGradePoints += (gradePoint * credits);
                        totalCreditsAttempted += credits;
                        
                        break;
                    }
                }


                table.addCell(new Phrase(enrollment.getCourseCode(), DATA_FONT));
                table.addCell(new Phrase(enrollment.getCourseTitle(), DATA_FONT));
                table.addCell(new Phrase(String.valueOf(credits), DATA_FONT));
                table.addCell(new Phrase(finalGrade, DATA_FONT));
                table.addCell(new Phrase(String.format("%.2f", gradePoint), DATA_FONT));
            }

            document.add(table);
            document.add(new Paragraph(" "));


            double cgpa = 0.0;
            if (totalCreditsAttempted > 0) {
                cgpa = totalGradePoints / totalCreditsAttempted;
            }

            document.add(new Paragraph("-- CGPA Summary --", SUMMARY_FONT));
            document.add(new Paragraph("Total Credits Attempted: " + totalCreditsAttempted, DATA_FONT));
            document.add(new Paragraph("Cumulative GPA (CGPA): " + String.format("%.2f", cgpa), SUMMARY_FONT));
            document.add(new Paragraph("Grading Scale: A=10.0, A-=9.0, B=8.0, B-=7.0, C=6.0, C-=5.0, D=4.0, F=0.0", DATA_FONT));



            document.close();
            return true;

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


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