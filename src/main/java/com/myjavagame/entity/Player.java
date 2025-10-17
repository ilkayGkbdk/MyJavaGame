package com.myjavagame.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import com.myjavagame.entity.base.Mob;
import com.myjavagame.main.MainPanel;

public class Player extends Mob {

    public Player(MainPanel mp) {
        super(mp);
        // TODO Auto-generated constructor stub
    }

    public void setDefaultValues() {
        worldX = mp.getHalfScreenWidth();
        worldY = mp.getHalfScreenHeight();
        speed = 4;
        direction = "down";
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        if (isMoving()) {
            checkDirection();
            move();
        }
    }

    void move() {
        switch (direction) {
            case "up":
                worldY -= speed;
                break;
            case "down":
                worldY += speed;
                break;
            case "left":
                worldX -= speed;
                break;
            case "right":
                worldX += speed;
                break;
        }
    }

    void checkDirection() {
        if (mp.keyHandler.upPressed) {
            direction = "up";
        } else if (mp.keyHandler.downPressed) {
            direction = "down";
        } else if (mp.keyHandler.leftPressed) {
            direction = "left";
        } else if (mp.keyHandler.rightPressed) {
            direction = "right";
        }
    }

    boolean isMoving() {
        return mp.keyHandler.upPressed || mp.keyHandler.downPressed || mp.keyHandler.leftPressed
                || mp.keyHandler.rightPressed;
    }

    @Override
    public void draw(Graphics2D g2) {
        // TODO Auto-generated method stub
        g2.setColor(Color.blue);
        g2.fillRect(worldX, worldY, MainPanel.tileSize, MainPanel.tileSize);
    }

}
