package com.myjavagame.entity.base;

// ...
import com.myjavagame.main.MainPanel;

public abstract class Mob extends Entity {

    // BU SATIR KALDIRILDI (Artık Entity sınıfında):
    // public String direction = "down";

    public Mob(MainPanel mp) {
        super(mp);
    }
}