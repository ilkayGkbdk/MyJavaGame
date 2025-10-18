package com.myjavagame.main;

import java.awt.Graphics2D;

import com.myjavagame.entity.base.Entity;
import com.myjavagame.entity.Player; // Player'ı import et

public class Debugger {

    public MainPanel mp;

    public Debugger(MainPanel mp) {
        this.mp = mp;
    }

    public void drawHitsBoxes(Graphics2D g2) {
        g2.setColor(java.awt.Color.RED);

        Player player = mp.player; // Kolay erişim için

        for (Entity entity : mp.entityList) {

            // Varlığın ekran konumunu oyuncuya göre hesapla
            int screenX = entity.worldX - player.worldX + player.screenX;
            int screenY = entity.worldY - player.worldY + player.screenY;

            // TODO: Her entity'nin kendi width/height'ı olmalı.
            // Şimdilik varsayılan tileSize kullanıyoruz.
            int width = MainPanel.tileSize;
            int height = MainPanel.tileSize;

            // YENİ: Varlık ekranın dışındaysa, onu ÇİZME (Culling)
            if (screenX + width < 0 || screenX > mp.screenWidth ||
                    screenY + height < 0 || screenY > mp.screenHeight) {
                continue; // Döngüde bir sonraki varlığa geç
            }

            g2.drawRect(screenX, screenY, width, height);
        }

        // Player'ın hitbox'ını her zaman çiz (o zaten ekranda)
        g2.setColor(java.awt.Color.CYAN); // Oyuncunun hitbox'ı farklı renk olsun
        if (player.solidArea != null) {
            g2.drawRect(
                    player.screenX + player.solidArea.x,
                    player.screenY + player.solidArea.y,
                    player.solidArea.width,
                    player.solidArea.height);
        }
    }
}