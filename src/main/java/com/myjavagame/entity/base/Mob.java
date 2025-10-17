package com.myjavagame.entity.base;

import com.myjavagame.main.MainPanel;

public abstract class Mob extends Entity {

    public int speed;
    public String direction = "down";

    public Mob(MainPanel mp) {
        super(mp);
        //TODO Auto-generated constructor stub
    }
}
