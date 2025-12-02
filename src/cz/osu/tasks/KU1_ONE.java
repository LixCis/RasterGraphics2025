package cz.osu.tasks;

import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;
import cz.osu.utils.HLS;
import cz.osu.utils.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * KU1_ONE - Potlačení efektu červených očí
 * Převod RGB -> HLS, drobný HUE posun, lehké ztmavení
 */
public class KU1_ONE {
    private final MainWindow mainWindow;

    private static final String IMAGE_PATH = "src/cz/osu/images/Red-eye-flash.jpg";

    // Souřadnice rect oblastí očí z image-map
    private static final int LEFT_RECT_X1 = 341;
    private static final int LEFT_RECT_Y1 = 156;
    private static final int LEFT_RECT_X2 = 323;
    private static final int LEFT_RECT_Y2 = 140;

    private static final int RIGHT_RECT_X1 = 473;
    private static final int RIGHT_RECT_Y1 = 142;
    private static final int RIGHT_RECT_X2 = 491;
    private static final int RIGHT_RECT_Y2 = 159;

    // Centrum a poloměry odvozené z rect
    private static final int LEFT_EYE_X = (LEFT_RECT_X1 + LEFT_RECT_X2) / 2;
    private static final int LEFT_EYE_Y = (LEFT_RECT_Y1 + LEFT_RECT_Y2) / 2;
    private static final int LEFT_RECT_WIDTH = Math.abs(LEFT_RECT_X1 - LEFT_RECT_X2);
    private static final int LEFT_RECT_HEIGHT = Math.abs(LEFT_RECT_Y1 - LEFT_RECT_Y2);
    private static final int LEFT_EYE_RADIUS = Math.max(LEFT_RECT_WIDTH, LEFT_RECT_HEIGHT) / 2 + 2;

    private static final int RIGHT_EYE_X = (RIGHT_RECT_X1 + RIGHT_RECT_X2) / 2;
    private static final int RIGHT_EYE_Y = (RIGHT_RECT_Y1 + RIGHT_RECT_Y2) / 2;
    private static final int RIGHT_RECT_WIDTH = Math.abs(RIGHT_RECT_X1 - RIGHT_RECT_X2);
    private static final int RIGHT_RECT_HEIGHT = Math.abs(RIGHT_RECT_Y1 - RIGHT_RECT_Y2);
    private static final int RIGHT_EYE_RADIUS = Math.max(RIGHT_RECT_WIDTH, RIGHT_RECT_HEIGHT) / 2 + 2;

    public KU1_ONE(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }

    public void run() {
        V_RAM vRam = loadImageFromFile(IMAGE_PATH);
        if (vRam == null) return;

        // Zobrazí původní a různé varianty úprav
        mainWindow.showImage(vRam.getImage());
        delay(1200);

        mainWindow.showImage(removeRedEye(vRam.getCopy(), 210).getImage());
        delay(1200);

        mainWindow.showImage(removeRedEye(vRam.getCopy(), -160).getImage());
        delay(1200);

        // Finální varianta
        mainWindow.showImage(removeRedEye(vRam, 180).getImage());
    }

    /**
     * Aplikuje korekci očí v oblasti center a poloměrů
     */
    private V_RAM removeRedEye(V_RAM vRam, float hueShift) {
        processEye(vRam, LEFT_EYE_X, LEFT_EYE_Y, LEFT_EYE_RADIUS, hueShift);
        processEye(vRam, RIGHT_EYE_X, RIGHT_EYE_Y, RIGHT_EYE_RADIUS, hueShift);
        return vRam;
    }

    /**
     * Zpracování oka - najde červené pixely a opraví je pomocí HUE posunu.
     * Také upraví okolní pixely (halo) pro hladší přechod.
     */
    private void processEye(V_RAM vRam, int centerX, int centerY, int radius, float hueShift) {
        int minX = Math.max(0, centerX - radius);
        int maxX = Math.min(vRam.getWidth() - 1, centerX + radius);
        int minY = Math.max(0, centerY - radius);
        int maxY = Math.min(vRam.getHeight() - 1, centerY + radius);

        boolean[][] redMask = new boolean[maxY - minY + 1][maxX - minX + 1];

        // První průchod: najde červené pixely
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                if (dx * dx + dy * dy > radius * radius) continue;

                if (isRedPixel(new RGB(vRam.getPixel(x, y)))) {
                    redMask[y - minY][x - minX] = true;
                }
            }
        }

        // Druhý průchod: aplikuje korekci
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                if (dx * dx + dy * dy > radius * radius) continue;

                boolean isCore = redMask[y - minY][x - minX];
                boolean isHalo = !isCore && isNearRed(x, y, minX, minY, redMask);

                if (!isCore && !isHalo) continue;

                RGB corrected = correctPixel(new RGB(vRam.getPixel(x, y)), hueShift);

                if (isCore) {
                    vRam.setPixel(x, y, corrected.R, corrected.G, corrected.B);
                } else {
                    // Halo blend
                    RGB orig = new RGB(vRam.getPixel(x, y));
                    int r = (int)(orig.R * 0.6f + corrected.R * 0.4f);
                    int g = (int)(orig.G * 0.6f + corrected.G * 0.4f);
                    int b = (int)(orig.B * 0.6f + corrected.B * 0.4f);
                    vRam.setPixel(x, y, r, g, b);
                }
            }
        }
    }

    private boolean isNearRed(int x, int y, int minX, int minY, boolean[][] mask) {
        int px = x - minX;
        int py = y - minY;

        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                int ny = py + dy;
                int nx = px + dx;
                if (ny >= 0 && ny < mask.length && nx >= 0 && nx < mask[0].length) {
                    if (mask[ny][nx] && dx * dx + dy * dy <= 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private RGB correctPixel(RGB orig, float hueShift) {
        HLS hls = new HLS(orig);
        hls.hueShift(hueShift);
        hls.saturation *= 0.6f;
        hls.lightness *= 0.6f;
        return new RGB(hls.toRGB());
    }

    private boolean isRedPixel(RGB rgb) {
        return rgb.R > 100 && rgb.R > rgb.G * 1.5 && rgb.R > rgb.B * 1.5;
    }

    private V_RAM loadImageFromFile(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            if (image == null) return null;

            V_RAM vRam = new V_RAM(image.getWidth(), image.getHeight());
            for (int y = 0; y < vRam.getHeight(); y++) {
                for (int x = 0; x < vRam.getWidth(); x++) {
                    vRam.setPixelsInt(x, y, image.getRGB(x, y));
                }
            }
            return vRam;
        } catch (IOException e) {
            return null;
        }
    }

    private void delay(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}
