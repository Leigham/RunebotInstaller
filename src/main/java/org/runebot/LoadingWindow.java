package org.runebot;

import com.formdev.flatlaf.FlatDarkLaf;

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
import java.util.Objects;

public class LoadingWindow extends JFrame {
    private final JProgressBar progressBar;
    private final int fadeDuration = 1000;
    public int currentprogress = 100;
    private JTextArea consoleTextArea; // New component for the console

    public LoadingWindow(boolean debug) throws NullPointerException {
        // Set the FlatDarkLaf look and feel for a dark theme
        FlatDarkLaf.setup();
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUndecorated(true);
        setTitle("Loading...");
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/rbactual.gif")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Double the size of the image
        assert originalImage != null;
        int newWidth = originalImage.getWidth() * 2;
        int newHeight = originalImage.getHeight() * 2;
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // Update the ImageIcon with the new scaled Image
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon, JLabel.CENTER);
        add(logoLabel, BorderLayout.NORTH);

        if (debug) {
            consoleTextArea = new JTextArea();
            consoleTextArea.setBackground(Color.BLACK);
            consoleTextArea.setForeground(Color.decode("#96be25"));
            consoleTextArea.setEditable(false);
            consoleTextArea.setLineWrap(true); // Wrap lines to fit the width

            // Set the preferred width of the console text area to match the image's width, with 90% of its width
            int consoleWidth = (int) (newWidth * 0.9);
            consoleTextArea.setPreferredSize(new Dimension(consoleWidth, newHeight));

            JPanel consolePanel = new JPanel(new BorderLayout());
            consolePanel.setBackground(Color.BLACK);
            consolePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Add padding (top, left, bottom, right)
            consolePanel.add(consoleTextArea);

            JScrollPane scrollPane = new JScrollPane(consolePanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setPreferredSize(new Dimension(newWidth, newHeight / 2)); // Set the preferred size of the scroll pane
            add(scrollPane, BorderLayout.CENTER); // Place the scroll pane at the center of the main panel
        }

        progressBar = new JProgressBar(0, 100);
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        progressBar.setBackground(Color.BLACK);
        progressBar.setForeground(Color.decode("#96be25"));
        add(progressBar, BorderLayout.SOUTH); // Place the progress bar at the bottom of the main panel

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
            return Color.decode("#96be25"); // Set the desired text color here
        }

        @Override
        protected Color getSelectionForeground() {
            return Color.BLACK; // Set the desired text color here
        }
    }

    public void showLoadingWindow() {
        setVisible(true);
    }

    public void setProgress(int progress) {
        this.currentprogress = progress;
        progressBar.setValue(progress);
    }

    public void setStatusText(String status) {
        progressBar.setStringPainted(true);
        progressBar.setString(status);
    }

    public void debugPrintln(String message) {
        System.out.println("message");
        System.out.println(message);
        if (consoleTextArea != null) {
            consoleTextArea.append(message + "\n");
            consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
        }
    }
}
