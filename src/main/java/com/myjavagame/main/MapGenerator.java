package com.myjavagame.main;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Sadece 1 kez çalıştırılmak üzere tasarlanmış bir yardımcı araç.
 * 'src/main/resources/maps/' klasörü altına 100x100'lük bir
 * 'world_map.txt' harita dosyası oluşturur.
 */
public class MapGenerator {

    public static final int MAP_COLS = 100;
    public static final int MAP_ROWS = 100;

    // Tile Numaraları: 0 = Water, 1 = Sand, 2 = Stone
    public static void main(String[] args) {

        // Proje kök dizinini bul ve resources klasörüne git
        String filePath = "src/main/resources/maps/world_map.txt";
        File mapFile = new File(filePath);

        // maps klasörünün var olduğundan emin ol
        mapFile.getParentFile().mkdirs();

        Random rand = new Random();

        try (PrintWriter writer = new PrintWriter(mapFile)) {
            System.out.println("Harita dosyası oluşturuluyor: " + mapFile.getAbsolutePath());

            for (int row = 0; row < MAP_ROWS; row++) {
                for (int col = 0; col < MAP_COLS; col++) {

                    int tileNum = 2; // Varsayılan: Stone (2)

                    // Haritanın kenarlarına 5 tile'lık bir su (0) çerçevesi koy
                    if (row < 5 || row >= MAP_ROWS - 5 || col < 5 || col >= MAP_COLS - 5) {
                        tileNum = 0; // Water
                    }
                    // Suyun etrafına 5 tile'lık bir kumsal (1) alanı koy
                    else if (row < 10 || row >= MAP_ROWS - 10 || col < 10 || col >= MAP_COLS - 10) {
                        tileNum = 1; // Sand
                    }
                    // Geri kalan alanların %5'ine rastgele kum (1) serp
                    else if (rand.nextInt(100) < 5) {
                        tileNum = 1; // %5 ihtimalle Sand
                    }

                    writer.print(tileNum + " ");
                }
                writer.println(); // Bir sonraki satıra geç
            }
            System.out.println("Harita dosyası başarıyla oluşturuldu!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Harita dosyası oluşturulurken hata oluştu!");
        }
    }
}