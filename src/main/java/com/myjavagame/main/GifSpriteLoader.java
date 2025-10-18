package com.myjavagame.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Bir GIF dosyasını okuyarak içindeki tüm kareleri (frame)
 * BufferedImage listesi olarak döndüren bir yardımcı sınıf.
 */
public class GifSpriteLoader {

    /**
     * Verilen bir InputStream'den (genellikle bir resource dosyası)
     * GIF karelerini okur.
     *
     * @param inputStream Okunacak GIF dosyasının veri akışı
     * @return Kareleri içeren bir BufferedImage listesi
     * @throws IOException Dosya okuma sırasında bir hata oluşursa
     */
    public List<BufferedImage> loadSpritesFromGif(InputStream inputStream) throws IOException {

        List<BufferedImage> frames = new ArrayList<>();
        ImageReader reader = null;

        try {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) {
                throw new IOException("Bu format için (gif) bir okuyucu bulunamadı.");
            }
            reader = readers.next();

            // 2. Dosya (File) yerine InputStream kullanarak ImageInputStream oluştur
            try (ImageInputStream iis = ImageIO.createImageInputStream(inputStream)) {

                if (iis == null) {
                    throw new IOException("InputStream'den ImageInputStream oluşturulamadı.");
                }

                reader.setInput(iis);

                int numFrames = reader.getNumImages(true);

                for (int i = 0; i < numFrames; i++) {
                    BufferedImage frame = reader.read(i);
                    frames.add(frame);
                }
            }
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
        return frames;
    }
}