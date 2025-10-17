package com.myjavagame;

import javax.swing.JPanel;
import java.awt.Dimension;

public class MainPanel extends JPanel {

    public static final int tileSize = 64;
    public static final int maxScreenCol = 16;
    public static final int maxScreenRow = 12;

    public static final int screenWidth = tileSize * maxScreenCol; // 1024 pixels
    public static final int screenHeight = tileSize * maxScreenRow; // 768 pixels

    public MainPanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(java.awt.Color.PINK);
    }
}
