package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SettingsPanel extends JPanel {

    private MazeConfig currentConfig;

    private JTextField widthField;
    private JTextField heightField;
    private JTextField gridColorField;
    private JTextField wallColorField;
    private JTextField gridLinesField;
    private JTextField pathColorField;
    private JTextField stepDelayField;

    public SettingsPanel(int x, int y, int width, int height) {
        this.setBounds(x, y, width, height);
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(new Color(43, 43, 43));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        JLabel title2 = createLabel("Please choose your settings:", 20, SwingConstants.CENTER);
        title2.setForeground(new Color(200, 200, 200));
        topPanel.add(title2);
        this.add(topPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(10, 30, 20, 30));

        gridPanel.add(createLabel("Width", 18, SwingConstants.LEFT));
        widthField = createTextField("30", true);
        gridPanel.add(widthField);


        gridPanel.add(createLabel("Height", 18, SwingConstants.LEFT));
        heightField = createTextField("30", true);
        gridPanel.add(heightField);

        gridPanel.add(createLabel("Grid color", 18, SwingConstants.LEFT));
        gridColorField = createTextField("", false);
        gridPanel.add(gridColorField);

        gridPanel.add(createLabel("Wall color", 18, SwingConstants.LEFT));
        wallColorField = createTextField("", false);
        gridPanel.add(wallColorField);

        gridPanel.add(createLabel("Grid lines", 18, SwingConstants.LEFT));
        gridLinesField = createTextField("", false);
        gridPanel.add(gridLinesField);

        gridPanel.add(createLabel("Path color", 18, SwingConstants.LEFT));
        pathColorField = createTextField("", false);
        gridPanel.add(pathColorField);

        gridPanel.add(createLabel("Step delay", 18, SwingConstants.LEFT));
        stepDelayField = createTextField("", false);
        gridPanel.add(stepDelayField);

        this.add(gridPanel, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(0, 30, 15, 30));


        JButton refreshBtn = createButton("Refresh!");
        refreshBtn.addActionListener(e -> loadConfigFromServer());

        JButton getMazeBtn = createButton("GET MAZE!");
        getMazeBtn.addActionListener(e -> {
            int validWidth = validateInput(widthField.getText());
            int validHeight = validateInput(heightField.getText());

            widthField.setText(String.valueOf(validWidth));
            heightField.setText(String.valueOf(validHeight));

            BufferedImage mazeImage = MazeApiClient.fetchMazeImage(validWidth, validHeight);

            if (mazeImage != null) {
                boolean[][] mazeLogic = parseMazePixels(mazeImage, validWidth, validHeight);

                JFrame mazeWindow = new JFrame("The Maze");
                mazeWindow.setLayout(new BorderLayout());
                mazeWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                MazePanel mazePanel = new MazePanel(mazeLogic, currentConfig);

                JScrollPane scrollPane = new JScrollPane(mazePanel);
                scrollPane.setBorder(null);
                mazeWindow.add(scrollPane, BorderLayout.CENTER);

                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
                bottomPanel.setBackground(new Color(43, 43, 43));

                JButton checkSolutionBtn = createButton("Check Solution");
                JButton speedUpBtn = createButton("Speed Up");
                speedUpBtn.setEnabled(false);

                speedUpBtn.addActionListener(ev -> {
                    mazePanel.speedUpAnimation();
                });

                checkSolutionBtn.addActionListener(ev -> {
                    checkSolutionBtn.setEnabled(false);
                    speedUpBtn.setEnabled(true);

                    boolean isSolvable = mazePanel.solveAndAnimate(() -> {
                        checkSolutionBtn.setEnabled(true);
                        speedUpBtn.setEnabled(false);
                    });

                    if (!isSolvable) {
                        JOptionPane.showMessageDialog(mazeWindow, "No solution found!", "Dead End", JOptionPane.WARNING_MESSAGE);
                        checkSolutionBtn.setEnabled(true);
                        speedUpBtn.setEnabled(false);
                    }
                });

                bottomPanel.add(checkSolutionBtn);
                bottomPanel.add(speedUpBtn);
                mazeWindow.add(bottomPanel, BorderLayout.SOUTH);

                mazeWindow.pack();
                mazeWindow.setLocationRelativeTo(null);
                mazeWindow.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Failed to fetch maze image.");
            }
        });


        buttonsPanel.add(refreshBtn);
        buttonsPanel.add(getMazeBtn);

        bottomContainer.add(buttonsPanel, BorderLayout.CENTER);
        this.add(bottomContainer, BorderLayout.SOUTH);

        loadConfigFromServer();
    }

    private JLabel createLabel(String text, int size, int alignment) {
        JLabel lbl = new JLabel(text, alignment);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, size));
        return lbl;
    }

    private JTextField createTextField(String text, boolean editable) {
        JTextField tf = new JTextField(text);
        tf.setEditable(editable);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (!editable) {
            tf.setBackground(new Color(235, 240, 235));
            tf.setForeground(new Color(80, 80, 80));
        }
        return tf;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(225, 240, 245));
        btn.setForeground(new Color(30, 60, 80));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadConfigFromServer() {
        currentConfig = MazeApiClient.fetchConfig();
        if (currentConfig != null) {
            wallColorField.setText("#" + Integer.toHexString(currentConfig.wallCellColor.getRGB()).substring(2).toUpperCase());
            pathColorField.setText("#" + Integer.toHexString(currentConfig.pathColor.getRGB()).substring(2).toUpperCase());
            gridColorField.setText("#" + Integer.toHexString(currentConfig.gridColor.getRGB()).substring(2).toUpperCase());
            gridLinesField.setText(String.valueOf(currentConfig.drawGrid));
            stepDelayField.setText(String.valueOf(currentConfig.animationDelayMs) + " ms");
        } else {
            wallColorField.setText("Error");
            pathColorField.setText("Error");
        }
    }

    private int validateInput(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            if (value < 5 || value > 100) return 30;
            return value;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    private boolean[][] parseMazePixels(BufferedImage image, int logicalWidth, int logicalHeight) {
        boolean[][] maze = new boolean[logicalHeight][logicalWidth];

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        double scaleX = (double) imgWidth / logicalWidth;
        double scaleY = (double) imgHeight / logicalHeight;

        for (int y = 0; y < logicalHeight; y++) {
            for (int x = 0; x < logicalWidth; x++) {

                int sampleX = (int) (x * scaleX + (scaleX / 2));
                int sampleY = (int) (y * scaleY + (scaleY / 2));

                if (sampleX >= imgWidth) sampleX = imgWidth - 1;
                if (sampleY >= imgHeight) sampleY = imgHeight - 1;

                int rgb = image.getRGB(sampleX, sampleY);
                Color color = new Color(rgb, false);

                if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) {
                    maze[y][x] = true;
                } else {
                    maze[y][x] = false;
                }
            }
        }
        return maze;
    }
}