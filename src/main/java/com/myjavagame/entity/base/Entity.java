package com.myjavagame.entity.base;

import com.myjavagame.main.MainPanel;

public abstract class Entity {

    public MainPanel mp;

    public int worldX, worldY;

    public Entity(MainPanel mp) {
        this.mp = mp;
    }

    public abstract void update();
    public abstract void draw(java.awt.Graphics2D g2);
}
