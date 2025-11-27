package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BackgroundPanel extends JPanel {

    private Image bgImage;
    public BackgroundPanel(String imagePath) {
        URL res = getClass().getResource(imagePath);
        if (res != null) {
            bgImage = new ImageIcon(res).getImage();
        } else {
            ImageIcon icon = new ImageIcon(imagePath);
            bgImage = icon.getImage();
        }

        if (bgImage == null) {
            System.err.println("BackgroundPanel: failed to load image: " + imagePath);
        }
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
