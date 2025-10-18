package com.myjavagame.tile;

import java.awt.image.BufferedImage;

/**
 * Tek bir Tile (karo) türünün özelliklerini tutan model sınıfı.
 * (örn: Water, Stone, Sand)
 */
public class Tile {

    public BufferedImage image;

    /**
     * Bu tile'ın çarpışmaya (collision) sahip olup olmadığını belirtir.
     * true = Üzerinden geçilemez (örn: Water, Wall)
     * false = Üzerinden geçilebilir (örn: Sand, Stone)
     */
    public boolean collision = false;
}