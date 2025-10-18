package com.myjavagame.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.myjavagame.entity.base.Mob;
import com.myjavagame.main.MainPanel;

public class Player extends Mob {

    // --- YENİ: Animasyon Listeleri ---
    // Her durum ve yön için ayrı listeler tutacağız.

    // Orijinal 8 karelik "durma" animasyonları
    public List<BufferedImage> idleSprites;

    // Yeni "koşma" animasyonları
    public List<BufferedImage> runSouthSprites;
    public List<BufferedImage> runNorthSprites;
    public List<BufferedImage> runWestSprites;
    public List<BufferedImage> runEastSprites;

    // --- YENİ: Durum (State) Yönetimi ---
    // Oyuncunun "idle" mı "moving" mi olduğunu takip eder.
    private String currentState = "idle";

    // (screenX, screenY, playerWidth, playerHeight, spriteCounter, spriteNum
    // bir önceki adımdaki gibi kalıyor)
    public final int screenX;
    public final int screenY;
    public final int playerWidth = MainPanel.tileSize * 2;
    public final int playerHeight = MainPanel.tileSize * 2;
    public int spriteCounter = 0;
    public int spriteNum = 0;

    public Player(MainPanel mp) {
        super(mp);

        this.screenX = mp.getHalfScreenWidth() - (playerWidth / 2);
        this.screenY = mp.getHalfScreenHeight() - (playerHeight / 2);

        // Listeleri başlat (NullPointerException önlemi)
        idleSprites = new ArrayList<>();
        runSouthSprites = new ArrayList<>();
        runNorthSprites = new ArrayList<>();
        runWestSprites = new ArrayList<>();
        runEastSprites = new ArrayList<>();

        // YENİ: Katı alanı (solidArea) başlat
        // 64x64'lük bir oyuncu görseli için:
        // x=16 -> (64 - 32) / 2 (Görselin ortasında 32px genişlikte)
        // y=32 -> (Görselin alt yarısı)
        // width=32
        // height=32
        solidArea = new Rectangle(16, 32, 32, 32);

        // Varsayılan ofsetleri kaydet
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void setDefaultValues() {
        worldX = 500;
        worldY = 500;
        speed = 4;
        direction = "down";
        currentState = "idle";

        // YENİ: Oyuncu yeniden doğduğunda hitbox'ı sıfırla
        solidArea.x = solidAreaDefaultX;
        solidArea.y = solidAreaDefaultY;
    }

    /**
     * YENİ: Tüm animasyon GIF'lerini yükler.
     */
    public void loadSprites() {
        try {
            // 1. Durma (Idle) animasyonunu yükle
            // Bu, 4 yönü de içeren 8 karelik tek GIF'imizdi.
            idleSprites = loadAnimation("/player/player_sprite.gif");

            // 2. Koşma (Run) animasyonlarını yükle
            // Bunlar 4 ayrı GIF dosyası.
            runSouthSprites = loadAnimation("/player/run/player_run_south.gif");
            runNorthSprites = loadAnimation("/player/run/player_run_north.gif");
            runWestSprites = loadAnimation("/player/run/player_run_west.gif");
            runEastSprites = loadAnimation("/player/run/player_run_east.gif");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Sprite yüklenirken hata oluştu!");
        }
    }

    /**
     * YENİ: Kaynak yolundan bir GIF yükleyip karelerini liste olarak döndüren
     * yardımcı metot. Kod tekrarını önler.
     */
    private List<BufferedImage> loadAnimation(String resourcePath) throws IOException {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Kaynak dosyası bulunamadı: " + resourcePath);
        }
        return mp.gifSpriteLoader.loadSpritesFromGif(is);
    }

    /**
     * YENİ: Güncellenmiş update metodu.
     * Oyuncunun durumunu ("idle" veya "moving") ve yönünü kontrol eder.
     * Durum veya yön değişirse animasyon sayacını sıfırlar.
     */
    @Override
    public void update() {

        boolean moving = isMoving();
        String newState = moving ? "moving" : "idle";
        String oldDirection = direction;

        if (moving) {
            checkDirection(); // 1. Yönü belirle

            // 2. YENİ: Çarpışmayı kontrol et
            collisionOn = false; // Her 'update' başında bayrağı sıfırla
            mp.collisionChecker.checkTile(this); // Tile'larla çarpışmayı kontrol et

            // TODO: checkObject(this) buraya eklenecek (Etkileşim için)

            // 3. YENİ: Sadece çarpışma yoksa hareket et
            if (!collisionOn) {
                move();
            }
        }

        // 2. Durum (State) veya Yön Değişimini Kontrol Et
        // Eğer duruyorken yürümeye başlarsa (idle -> moving)
        // VEYA yürüyorken durursa (moving -> idle)
        // VEYA yürürken yön değiştirirse (up -> left)
        // -> Animasyon karesini başa sar (spriteNum = 0).
        if (!newState.equals(currentState) || !direction.equals(oldDirection)) {
            spriteCounter = 0;
            spriteNum = 0;
        }

        // 3. Mevcut Durumu Kaydet
        currentState = newState;

        // 4. Animasyon Karesini İlerlet
        spriteCounter++;
        // Animasyon hızı: 4 update'te bir (saniyede 15 kez) kare değişir.
        if (spriteCounter > 4) {
            spriteCounter = 0;
            spriteNum++;

            if (currentState.equals("moving")) {
                // HAREKETLİ: Mevcut koşma animasyon listesini al
                List<BufferedImage> currentAnimation = getRunningAnimationList();
                if (currentAnimation != null && !currentAnimation.isEmpty()) {
                    // Animasyonun sonuna geldiyse başa dön
                    if (spriteNum >= currentAnimation.size()) {
                        spriteNum = 0;
                    }
                } else {
                    spriteNum = 0; // Liste boşsa (hata durumu)
                }
            } else {
                // DURUYOR (IDLE): Idle animasyonu yön başına 2 kareydi
                spriteNum = 0;
            }
        }
    }

    /**
     * YENİ: Mevcut yöne göre doğru "koşma" animasyon listesini döndürür.
     */
    private List<BufferedImage> getRunningAnimationList() {
        switch (direction) {
            case "up":
                return runNorthSprites;
            case "down":
                return runSouthSprites;
            case "left":
                return runWestSprites;
            case "right":
                return runEastSprites;
            default:
                return runSouthSprites; // Varsayılan
        }
    }

    // move(), checkDirection(), isMoving() metodları aynı kalabilir
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

    /**
     * YENİ: Güncellenmiş draw metodu.
     * Çizilecek kareyi currentState'e ("idle" veya "moving") göre seçer.
     */
    @Override
    public void draw(Graphics2D g2) {

        BufferedImage imageToDraw = null;

        try {
            if (currentState.equals("moving")) {
                // --- Hareketli (Running) Animasyonunu Çiz ---
                List<BufferedImage> currentAnimation = getRunningAnimationList();
                if (!currentAnimation.isEmpty()) {
                    imageToDraw = currentAnimation.get(spriteNum);
                }
            } else {
                // --- Durma (Idle) Animasyonunu Çiz ---
                // (Eski 8 karelik GIF'ten doğru 2 kareyi seçme mantığı)
                if (idleSprites != null && !idleSprites.isEmpty()) {
                    switch (direction) {
                        case "down":
                            imageToDraw = idleSprites.get(0 + spriteNum); // 0 veya 1
                            break;
                        case "right":
                            imageToDraw = idleSprites.get(2 + spriteNum); // 2 veya 3
                            break;
                        case "up":
                            imageToDraw = idleSprites.get(4 + spriteNum); // 4 veya 5
                            break;
                        case "left":
                            imageToDraw = idleSprites.get(6 + spriteNum); // 6 veya 7
                            break;
                        default:
                            imageToDraw = idleSprites.get(0 + spriteNum);
                            break;
                    }
                }
            }

            // --- Görüntüyü Ekrana Çiz ---
            if (imageToDraw != null) {
                g2.drawImage(imageToDraw, screenX, screenY, playerWidth, playerHeight, null);
            } else {
                // Hata durumu: Sprite yüklenememişse kırmızı kutu çiz
                g2.setColor(Color.RED);
                g2.fillRect(screenX, screenY, playerWidth, playerHeight);
            }

        } catch (IndexOutOfBoundsException e) {
            // Animasyon listesi boşsa veya spriteNum hatalıysa (hata ayıklama için)
            g2.setColor(Color.MAGENTA); // Mor bir kutu çiz
            g2.fillRect(screenX, screenY, playerWidth, playerHeight);
            System.err.println("Animasyon hatası: " + currentState + ", " + direction + ", spriteNum: " + spriteNum);
            e.printStackTrace();
        }
    }
}