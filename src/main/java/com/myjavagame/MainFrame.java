package com.myjavagame;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {
        MainPanel mainPanel = new MainPanel();

        setTitle("My Java Game");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(mainPanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
