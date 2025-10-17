package com.myjavagame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class MainPanel extends JPanel implements Runnable {

    // Screen settings
    public static final int tileSize = 64;
    public static final int maxScreenCol = 16;
    public static final int maxScreenRow = 12;

    public static final int screenWidth = tileSize * maxScreenCol; // 1024 pixels
    public static final int screenHeight = tileSize * maxScreenRow; // 768 pixels

    // Game Variables
    public Thread gameThread;
    private final double limit = 60d;
    private final double updateRate = 1e9 / limit;
    private long nextStatTime;
    private int fps, ups;
    public int avgFPS, avgUPS;

    public MainPanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(java.awt.Color.PINK);
        setDoubleBuffered(true);
        setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double accumulator = 0;
        long currentTime, lastUpdate = System.currentTimeMillis();
        nextStatTime = System.currentTimeMillis() + 1000;

        while (gameThread != null) {
            currentTime = System.currentTimeMillis();
            double lastRenderTimeInSeconds = (currentTime - lastUpdate) / 1000d;
            accumulator += lastRenderTimeInSeconds;
            lastUpdate = currentTime;

            while (accumulator > updateRate) {
                update();
                repaint();
                accumulator -= updateRate;
                ups++;
            }

            fps++;
            if (System.currentTimeMillis() > nextStatTime ) {
                avgFPS = fps;
                avgUPS = ups;
                fps = 0;
                ups = 0;
                nextStatTime = System.currentTimeMillis() + 1000;
            }
        }
    }

    public void update() {
        // Update game state
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
    }
}
