package com.myjavagame.main;

import java.awt.Rectangle;

import com.myjavagame.entity.base.Entity;

/**
 * Varlıkların (Entity) çarpışmalarını yöneten yardımcı sınıf.
 * Hem tile (zemin) çarpışmalarını hem de nesne/NPC etkileşimlerini
 * yönetmek için tasarlanmıştır.
 */
public class CollisionChecker {

    MainPanel mp;

    public CollisionChecker(MainPanel mp) {
        this.mp = mp;
    }

    /**
     * Bir varlığın (Entity) bir sonraki hareketinde
     * geçilemez bir Tile'a çarpıp çarpmayacağını kontrol eder.
     * 
     * @param entity Kontrol edilecek varlık (Player, Monster vb.)
     */
    public void checkTile(Entity entity) {

        // Varlığın hitbox'ının dünyadaki (world) mevcut koordinatları
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Bu koordinatların hangi tile indekslerine (satır/sütun) denk geldiği
        int entityLeftCol = entityLeftWorldX / MainPanel.tileSize;
        int entityRightCol = entityRightWorldX / MainPanel.tileSize;
        int entityTopRow = entityTopWorldY / MainPanel.tileSize;
        int entityBottomRow = entityBottomWorldY / MainPanel.tileSize;

        int tileNum1, tileNum2;

        // Varlığın GİDECEĞİ YÖN'e göre HAREKETİ SİMÜLE ET
        switch (entity.direction) {
            case "up":
                // YUKARI gidecekse, hitbox'ın BİR SONRAKİ üst Y pozisyonunu hesapla
                int nextTopRow = (entityTopWorldY - entity.speed) / MainPanel.tileSize;

                // Hata Kontrolü: Haritanın dışına mı çıkıyor?
                if (nextTopRow < 0) {
                    entity.collisionOn = true;
                    return; // Harita sınırı = çarpışma
                }

                // Hitbox'ın sol üst ve sağ üst köşelerinin değeceği 2 tile'ı kontrol et
                tileNum1 = mp.tileManager.mapTileNum[entityLeftCol][nextTopRow];
                tileNum2 = mp.tileManager.mapTileNum[entityRightCol][nextTopRow];

                if (mp.tileManager.tileTypes[tileNum1].collision || mp.tileManager.tileTypes[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case "down":
                // AŞAĞI gidecekse, hitbox'ın BİR SONRAKİ alt Y pozisyonunu hesapla
                int nextBottomRow = (entityBottomWorldY + entity.speed) / MainPanel.tileSize;

                // Hata Kontrolü
                if (nextBottomRow >= mp.tileManager.MAP_ROWS) {
                    entity.collisionOn = true;
                    return;
                }

                tileNum1 = mp.tileManager.mapTileNum[entityLeftCol][nextBottomRow];
                tileNum2 = mp.tileManager.mapTileNum[entityRightCol][nextBottomRow];

                if (mp.tileManager.tileTypes[tileNum1].collision || mp.tileManager.tileTypes[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case "left":
                // SOLA gidecekse, hitbox'ın BİR SONRAKİ sol X pozisyonunu hesapla
                int nextLeftCol = (entityLeftWorldX - entity.speed) / MainPanel.tileSize;

                // Hata Kontrolü
                if (nextLeftCol < 0) {
                    entity.collisionOn = true;
                    return;
                }

                tileNum1 = mp.tileManager.mapTileNum[nextLeftCol][entityTopRow];
                tileNum2 = mp.tileManager.mapTileNum[nextLeftCol][entityBottomRow];

                if (mp.tileManager.tileTypes[tileNum1].collision || mp.tileManager.tileTypes[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;

            case "right":
                // SAĞA gidecekse, hitbox'ın BİR SONRAKİ sağ X pozisyonunu hesapla
                int nextRightCol = (entityRightWorldX + entity.speed) / MainPanel.tileSize;

                // Hata Kontrolü
                if (nextRightCol >= mp.tileManager.MAP_COLS) {
                    entity.collisionOn = true;
                    return;
                }

                tileNum1 = mp.tileManager.mapTileNum[nextRightCol][entityTopRow];
                tileNum2 = mp.tileManager.mapTileNum[nextRightCol][entityBottomRow];

                if (mp.tileManager.tileTypes[tileNum1].collision || mp.tileManager.tileTypes[tileNum2].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    // --- ETKİLEŞİM İÇİN TEMEL ---
    // Burası senin "etkileşim" temelin olacak.
    // Bu metot, bir varlığın diğer varlıklara (NPC, canavar, item)
    // çarpıp çarpmadığını kontrol edecek.
    /**
     * Bir varlığın (entity) hedefler listesindeki (targets) başka bir
     * varlıkla çarpışıp çarpmadığını kontrol eder.
     * * @param entity Hareketi yapan varlık (örn: Player)
     * 
     * @param targets Kontrol edilecek varlık listesi (örn: mp.entityList)
     * @return Çarpışma varsa o varlığın listedeki 'index'ini, yoksa -1 döndürür.
     */
    public int checkObject(Entity entity, java.util.List<Entity> targets) {

        int hitIndex = -1; // -1 = hiçbir şeye çarpmadı

        for (int i = 0; i < targets.size(); i++) {
            Entity target = targets.get(i);

            // Kendisiyle çarpışmayı es geç
            if (target == entity) {
                continue;
            }

            // Varlığın bir sonraki adımdaki hitbox pozisyonunu al
            Rectangle nextStepHitbox = new Rectangle(
                    entity.worldX + entity.solidArea.x,
                    entity.worldY + entity.solidArea.y,
                    entity.solidArea.width,
                    entity.solidArea.height);

            // Hareketi simüle et
            switch (entity.direction) {
                case "up":
                    nextStepHitbox.y -= entity.speed;
                    break;
                case "down":
                    nextStepHitbox.y += entity.speed;
                    break;
                case "left":
                    nextStepHitbox.x -= entity.speed;
                    break;
                case "right":
                    nextStepHitbox.x += entity.speed;
                    break;
            }

            // Hedef varlığın mevcut hitbox pozisyonu
            Rectangle targetHitbox = new Rectangle(
                    target.worldX + target.solidArea.x,
                    target.worldY + target.solidArea.y,
                    target.solidArea.width,
                    target.solidArea.height);

            // YENİ: İki hitbox kesişiyor mu? (Etkileşim/Çarpışma)
            if (nextStepHitbox.intersects(targetHitbox)) {
                // Bu bir "katı" nesne ise, hareketi engelle
                // (Gelecekte: target.isSolid gibi bir şeye bakabiliriz)
                entity.collisionOn = true;
                hitIndex = i; // Hangi nesneye çarptığımızı döndür
                break; // İlk çarptığımız nesne yeterli
            }
        }

        return hitIndex;
    }
}