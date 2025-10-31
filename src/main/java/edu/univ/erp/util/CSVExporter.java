package edu.univ.erp.util;

import com.opencsv.CSVWriter;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.GradeService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    private final GradeService gradeService;

    public CSVExporter() {
        this.gradeService = new GradeService();
    }

    public boolean exportTranscript(List<Enrollment> enrollments, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"Course Code", "Course Title", "Credits", "Quiz", "Midterm", "End-Sem", "Final Score", "Grade"};
            writer.writeNext(header);

            for (Enrollment enrollment : enrollments) {
                List<Grade> grades = gradeService.getGradesForEnrollment(enrollment.getEnrollmentId());
                
                String quizScore = "";
                String midtermScore = "";
                String endsemScore = "";
                String finalScore = "";
                String finalGrade = "";

                for (Grade grade : grades) {
                    if ("QUIZ".equals(grade.getComponent())) {
                        quizScore = grade.getScore() != null ? String.format("%.2f", grade.getScore()) : "";
                    } else if ("MIDTERM".equals(grade.getComponent())) {
                        midtermScore = grade.getScore() != null ? String.format("%.2f", grade.getScore()) : "";
                    } else if ("ENDSEM".equals(grade.getComponent())) {
                        endsemScore = grade.getScore() != null ? String.format("%.2f", grade.getScore()) : "";
                    } else if ("FINAL".equals(grade.getComponent())) {
                        finalScore = grade.getScore() != null ? String.format("%.2f", grade.getScore()) : "";
                        finalGrade = grade.getFinalGrade() != null ? grade.getFinalGrade() : "";
                    }
                }

                String[] row = {
                    enrollment.getCourseCode(),
                    enrollment.getCourseTitle(),
                    String.valueOf(enrollment.getCourseCredits()),
                    quizScore,
                    midtermScore,
                    endsemScore,
                    finalScore,
                    finalGrade
                };
                writer.writeNext(row);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
