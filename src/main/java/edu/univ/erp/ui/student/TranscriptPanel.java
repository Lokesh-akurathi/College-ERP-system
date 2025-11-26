package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.common.MessageDialog;

import edu.univ.erp.util.PDFExporter; 

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter; 
import java.awt.*;
import java.io.File;
import java.util.List;

public class TranscriptPanel extends JPanel {
    private final EnrollmentService enrollmentService;
    
    private final PDFExporter pdfExporter; 

    public TranscriptPanel() {
        this.enrollmentService = new EnrollmentService();
       
        this.pdfExporter = new PDFExporter(); 
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Download Academic Transcript");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

     
        JLabel infoLabel = new JLabel("<html><p>Click the button below to download your official academic transcript.</p>" +
                "<p>The transcript will be saved as a **PDF file** containing your final course grades and CGPA.</p></html>"); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(infoLabel, gbc);

    
        JButton downloadButton = new JButton("Download Transcript (PDF)"); 
        downloadButton.setBackground(new Color(33, 150, 243));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setPreferredSize(new Dimension(220, 40));
        downloadButton.addActionListener(e -> downloadTranscript());
        gbc.gridy = 1;
        contentPanel.add(downloadButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void downloadTranscript() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript as PDF");

      
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);
        
        
        fileChooser.setSelectedFile(new File("transcript.pdf")); 
        
        int userSelection = fileChooser.showSaveDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
           
            String path = fileToSave.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(path + ".pdf");
            }
            
            List<Enrollment> enrollments = enrollmentService.getMyEnrollments();
            
           
            if (pdfExporter.exportTranscript(enrollments, fileToSave.getAbsolutePath())) { 
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Transcript downloaded successfully to: " + fileToSave.getAbsolutePath());
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Failed to download transcript. Check if the iText library is correctly added.");
            }
        }
    }
}