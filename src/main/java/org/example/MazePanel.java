package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class MazePanel extends JPanel {
    private boolean[][] mazeLogic;
    private MazeConfig config;
    private int cellSize;
    private List<Point> pathToDraw = new ArrayList<>();
    private Timer animationTimer;

    public MazePanel(boolean[][] mazeLogic, MazeConfig config) {
        this.mazeLogic = mazeLogic;
        this.config = config;

        int cols = mazeLogic[0].length;
        int rows = mazeLogic.length;
        int maxDimension = 650;

        cellSize = Math.min(maxDimension / cols, maxDimension / rows);

        if (cellSize < 4) cellSize = 4;
        if (cellSize > 25) cellSize = 25;

        int width = cols * cellSize;
        int height = rows * cellSize;
        this.setPreferredSize(new Dimension(width, height));
    }

    public boolean solveAndAnimate(Runnable onComplete) {
        if (animationTimer != null && animationTimer.isRunning()) {
            return true;
        }

        pathToDraw.clear();
        repaint();

        int height = mazeLogic.length;
        int width = mazeLogic[0].length;

        if (!mazeLogic[0][0] || !mazeLogic[height - 1][width - 1]) {
            return false;
        }

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        boolean[][] visited = new boolean[height][width];
        Point[][] parent = new Point[height][width];
        Queue<Point> queue = new LinkedList<>();

        queue.add(new Point(0, 0));
        visited[0][0] = true;
        boolean found = false;

        while (!queue.isEmpty()) {
            Point p = queue.poll();

            if (p.x == width - 1 && p.y == height - 1) {
                found = true;
                break;
            }

            for (int[] d : dirs) {
                int nx = p.x + d[0];
                int ny = p.y + d[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height && mazeLogic[ny][nx] && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    parent[ny][nx] = p;
                    queue.add(new Point(nx, ny));
                }
            }
        }

        if (!found) {
            return false;
        }

        List<Point> fullPath = new ArrayList<>();
        Point curr = new Point(width - 1, height - 1);
        while (curr != null) {
            fullPath.add(curr);
            curr = parent[curr.y][curr.x];
        }
        Collections.reverse(fullPath);

        animationTimer = new Timer(config.animationDelayMs, e -> {
            if (pathToDraw.size() < fullPath.size()) {
                pathToDraw.add(fullPath.get(pathToDraw.size()));
                repaint();
            } else {
                animationTimer.stop();
                if (onComplete != null) onComplete.run();
            }
        });
        animationTimer.start();
        return true;
    }

    public void speedUpAnimation() {
        if (animationTimer != null) {
            int currentDelay = animationTimer.getDelay();
            int newDelay = Math.max(1, currentDelay / 3);
            animationTimer.setDelay(newDelay);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < mazeLogic.length; y++) {
            for (int x = 0; x < mazeLogic[0].length; x++) {
                if (mazeLogic[y][x]) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(config.wallCellColor);
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

                if (config.drawGrid) {
                    g.setColor(config.gridColor);
                    g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        g.setColor(config.pathColor);
        for (Point p : pathToDraw) {
            g.fillRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
        }
    }
}