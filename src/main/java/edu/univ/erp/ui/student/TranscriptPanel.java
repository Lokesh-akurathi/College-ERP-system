package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.util.CSVExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class TranscriptPanel extends JPanel {
    private final EnrollmentService enrollmentService;
    private final CSVExporter csvExporter;

    public TranscriptPanel() {
        this.enrollmentService = new EnrollmentService();
        this.csvExporter = new CSVExporter();
        
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Download Transcript");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel infoLabel = new JLabel("<html><p>Click the button below to download your academic transcript.</p>" +
                "<p>The transcript will be saved as a CSV file containing your course grades.</p></html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(infoLabel, gbc);

        JButton downloadButton = new JButton("Download Transcript (CSV)");
        downloadButton.setBackground(new Color(33, 150, 243));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setPreferredSize(new Dimension(200, 40));
        downloadButton.addActionListener(e -> downloadTranscript());
        gbc.gridy = 1;
        contentPanel.add(downloadButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void downloadTranscript() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("transcript.csv"));
        
        int userSelection = fileChooser.showSaveDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            List<Enrollment> enrollments = enrollmentService.getMyEnrollments();
            
            if (csvExporter.exportTranscript(enrollments, fileToSave.getAbsolutePath())) {
                MessageDialog.showSuccess((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Transcript downloaded successfully to: " + fileToSave.getAbsolutePath());
            } else {
                MessageDialog.showError((JFrame) SwingUtilities.getWindowAncestor(this), 
                        "Failed to download transcript.");
            }
        }
    }
}
