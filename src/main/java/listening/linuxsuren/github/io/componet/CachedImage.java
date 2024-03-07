package listening.linuxsuren.github.io.componet;

import listening.linuxsuren.github.io.server.CacheServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CachedImage {
    public static ImageIcon ScaledImageIcon(String url) {
        return ScaledImageIcon(url, 80, 80);
    }

    public static ImageIcon ScaledImageIcon(String url, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(CacheServer.wrapURL(url));

            return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // TODO provide a default image
    }
}
