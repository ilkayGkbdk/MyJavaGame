package com.myjavagame.entity.base;

import java.awt.Rectangle; // YENİ IMPORT
import com.myjavagame.main.MainPanel;

public abstract class Entity {

    public MainPanel mp;
    public int worldX, worldY;

    public int speed;

    // YENİ: Çarpışma için "katı alan" (hitbox)
    // Bu, varlığın görseline göre (worldX, worldY) bir ofsettir.
    public Rectangle solidArea;

    // solidArea'nın varsayılan x, y ofsetlerini saklamak için
    public int solidAreaDefaultX, solidAreaDefaultY;

    // YENİ: Çarpışma durumunu belirten bayrak
    // true ise, varlık bir sonraki adımda hareket edemez.
    public boolean collisionOn = false;

    // YENİ: Mob'dan buraya taşıdık, çünkü tüm varlıkların yönü olabilir.
    public String direction = "down";

    public Entity(MainPanel mp) {
        this.mp = mp;
    }

    public abstract void update();

    public abstract void draw(java.awt.Graphics2D g2);
}