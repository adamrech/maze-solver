package org.example;

import java.awt.*;

public class MazeConfig {
    public Color wallCellColor;
    public Color pathColor;
    public boolean drawGrid;
    public Color gridColor;
    public int animationDelayMs;

      public static Color parseColor(String hex) {
        return Color.decode(hex);
    }
}
