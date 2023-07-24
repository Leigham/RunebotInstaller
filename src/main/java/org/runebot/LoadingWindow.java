package org.runebot;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LoadingWindow extends JFrame {
    private final JProgressBar progressBar;
    private final JLabel logoLabel;


    public LoadingWindow() {
        setUndecorated(true);
        setTitle("Loading...");
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(getClass().getResource("/rbactual.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Double the size of the image
        int newWidth = originalImage.getWidth() * 2;
        int newHeight = originalImage.getHeight() * 2;
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // Update the ImageIcon with the new scaled Image
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        logoLabel = new JLabel(scaledIcon, JLabel.CENTER);

        add(logoLabel, BorderLayout.NORTH);

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        progressBar.setBackground(Color.BLACK);
        progressBar.setForeground(Color.decode("#96be25"));
        add(progressBar, BorderLayout.CENTER);
        progressBar.setUI(new CustomProgressBarUI());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Handle the window closing event, if needed
                // For example, you might want to cancel the background process here
                super.windowClosing(e);
            }
        });
        pack();
        setLocationRelativeTo(null);
        // Custom UI class to change the progress bar text color

    }
    private static class CustomProgressBarUI extends BasicProgressBarUI {
        @Override
        protected Color getSelectionBackground() {
            return Color.WHITE; // Set the desired text color here
        }

        @Override
        protected Color getSelectionForeground() {
            return Color.WHITE; // Set the desired text color here
        }
    }
    public void showLoadingWindow() {
        setVisible(true);
    }

    public void hideLoadingWindow() {
        setVisible(false);
    }

    public void setProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void setStatusText(String status) {
        progressBar.setStringPainted(true);
        progressBar.setString(status);
    }

}
