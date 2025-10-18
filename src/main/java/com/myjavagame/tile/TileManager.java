package com.myjavagame.tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import com.myjavagame.main.MainPanel;

public class TileManager {

    MainPanel mp;
    public Tile[] tileTypes; // Farklı tile türlerini tutan dizi (water, sand, stone)
    public int[][] mapTileNum; // .txt'den okunan harita verisi (100x100)

    // Harita Boyutları (MapGenerator ile aynı olmalı)
    public static final int MAP_COLS = 100;
    public static final int MAP_ROWS = 100;

    public TileManager(MainPanel mp) {
        this.mp = mp;

        tileTypes = new Tile[10]; // 10 farklı tile türüne kadar destekleyebiliriz
        mapTileNum = new int[MAP_COLS][MAP_ROWS];

        loadTileImages();
        loadMap("/maps/world_map.txt");
    }

    /**
     * Tile görsellerini /resources/tiles/ klasöründen yükler.
     */
    public void loadTileImages() {
        // Tile 0: Water (Su) - Çarpışma var
        setupTile(0, "water", true);

        // Tile 1: Sand (Kum) - Çarpışma yok
        setupTile(1, "sand", false);

        // Tile 2: Stone (Taş) - Çarpışma yok
        setupTile(2, "stone", false);
    }

    /**
     * Tek bir tile'ı yüklemek için yardımcı metot.
     * Kod tekrarını önler.
     */
    private void setupTile(int index, String imageName, boolean collision) {
        try {
            tileTypes[index] = new Tile();

            String imagePath = "/tiles/" + imageName + ".png";
            InputStream is = getClass().getResourceAsStream(imagePath);
            if (is == null) {
                throw new IOException("Tile görseli bulunamadı: " + imagePath);
            }

            tileTypes[index].image = ImageIO.read(is);
            tileTypes[index].collision = collision; // Collision özelliğini ayarla

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verilen yoldaki harita dosyasını (.txt) okur ve
     * mapTileNum dizisine yükler.
     */
    public void loadMap(String mapFilePath) {
        try {
            InputStream is = getClass().getResourceAsStream(mapFilePath);
            if (is == null) {
                throw new IOException("Harita dosyası bulunamadı: " + mapFilePath);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (row < MAP_ROWS) {
                String line = br.readLine(); // Satırı oku (örn: "0 0 1 2 1 ...")
                if (line == null)
                    break;

                String[] numbers = line.split(" "); // Satırı boşluklara göre ayır

                col = 0;
                while (col < MAP_COLS) {
                    if (col >= numbers.length)
                        break;

                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num; // [col][row] (x, y) olarak saklıyoruz
                    col++;
                }
                row++;
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Haritayı ekrana çizer.
     * Bu metot SADECE ekranda görünmesi gereken tile'ları çizerek optimizasyon
     * yapar.
     */
    public void draw(Graphics2D g2) {

        int tileSize = MainPanel.tileSize;

        // Oyuncunun dünyadaki pozisyonunu al
        int playerWorldX = mp.player.worldX;
        int playerWorldY = mp.player.worldY;

        // Oyuncunun ekrandaki (sabit) pozisyonunu al
        int playerScreenX = mp.player.screenX;
        int playerScreenY = mp.player.screenY;

        // --- Kamera ve Optimizasyon ---
        // Ekranda görünecek tile'ların dünya koordinatlarındaki aralığını hesapla
        // Sadece bu aralıktaki tile'ları çizeceğiz, 100x100'ün tamamını değil.

        // Ekranda görünecek ilk tile'ın indeksi (sol-üst)
        int worldColStart = Math.max(0, (playerWorldX - playerScreenX) / tileSize);
        int worldRowStart = Math.max(0, (playerWorldY - playerScreenY) / tileSize);

        // Ekranda görünecek son tile'ın indeksi (sağ-alt)
        // (Ekran genişliğini/yüksekliğini ekleyip 1 tile pay bırakıyoruz)
        int worldColEnd = Math.min(MAP_COLS, (playerWorldX + (mp.screenWidth - playerScreenX) + tileSize) / tileSize);
        int worldRowEnd = Math.min(MAP_ROWS, (playerWorldY + (mp.screenHeight - playerScreenY) + tileSize) / tileSize);

        for (int worldRow = worldRowStart; worldRow < worldRowEnd; worldRow++) {
            for (int worldCol = worldColStart; worldCol < worldColEnd; worldCol++) {

                // Harita dosyasından hangi tile'ı çizeceğimizi al (0, 1, veya 2)
                int tileNum = mapTileNum[worldCol][worldRow];

                // Tile'ın dünyadaki (world) koordinatları
                int tileWorldX = worldCol * tileSize;
                int tileWorldY = worldRow * tileSize;

                // Tile'ın ekrandaki (screen) koordinatları
                // (Tile'ın dünya pozisyonu - Oyuncunun dünya pozisyonu + Oyuncunun ekran
                // pozisyonu)
                int tileScreenX = tileWorldX - playerWorldX + playerScreenX;
                int tileScreenY = tileWorldY - playerWorldY + playerScreenY;

                // Tile'ı ekrana çiz
                g2.drawImage(tileTypes[tileNum].image, tileScreenX, tileScreenY, tileSize, tileSize, null);
            }
        }
    }
}