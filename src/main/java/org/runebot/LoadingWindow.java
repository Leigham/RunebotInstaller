package org.runebot;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LoadingWindow extends JFrame {
    private final JProgressBar progressBar;
    private final JLabel logoLabel;
    private final int fadeDuration = 1000;
    public  int currentprogress = 100;
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
        // Set the initial window opacity to 0 (fully transparent)
        setOpacity(0.0f);

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


    public void fadeIn() {
        Timer fadeInTimer = new Timer(fadeDuration / 10, new ActionListener() {
            private float opacity = 0.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1.0f) {
                    setOpacity(opacity);
                    opacity += 0.1f; // Increase opacity gradually
                } else {
                    setOpacity(1.0f); // Ensure fully opaque at the end
                    ((Timer) e.getSource()).stop(); // Stop the timer
                }
            }
        });

        fadeInTimer.start();
        setVisible(true);
    }

    // Method to fade out the loading window
    public void fadeOut() {
        Timer fadeOutTimer = new Timer(fadeDuration / 10, new ActionListener() {
            private float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity > 0.0f) {
                    setOpacity(opacity);
                    opacity -= 0.1f; // Decrease opacity gradually
                } else {
                    setOpacity(0.0f); // Ensure fully transparent at the end
                    setVisible(false);
                    ((Timer) e.getSource()).stop(); // Stop the timer
                }
            }
        });

        fadeOutTimer.start();
    }

    // Override the setOpacity method to support transparency
    @Override
    public void setOpacity(float opacity) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        if (gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
            super.setOpacity(opacity);
        }
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
        this.currentprogress = progress;
        progressBar.setValue(progress);
    }

    public void setStatusText(String status) {
        progressBar.setStringPainted(true);
        progressBar.setString(status);
    }

}
