package org.example;
import javax.swing.*;

public class Main {
    public static int WINDOW_WIDTH=700;
    public static int WINDOW_HEIGHT=600;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame window = new JFrame("Maze Generator");
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        SettingsPanel settingsScene = new SettingsPanel(0, 0, 400, 400);
        window.add(settingsScene);

        window.setVisible(true);
    }
}
