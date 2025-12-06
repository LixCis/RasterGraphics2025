package cz.osu.tasks;

import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;
import cz.osu.utils.HSL;
import cz.osu.utils.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// KU1_ONE - Potlačení efektu červených očí

public class KU1_ONE {
    private final MainWindow mainWindow;

    private static final String IMAGE_PATH = "src/cz/osu/images/Red-eye-flash.jpg";

    // Oblasti očí
    private static final int LEFT_EYE_X1 = 323;
    private static final int LEFT_EYE_Y1 = 140;
    private static final int LEFT_EYE_X2 = 341;
    private static final int LEFT_EYE_Y2 = 156;

    private static final int RIGHT_EYE_X1 = 473;
    private static final int RIGHT_EYE_Y1 = 142;
    private static final int RIGHT_EYE_X2 = 491;
    private static final int RIGHT_EYE_Y2 = 159;

    public KU1_ONE(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }

    public void run() {
        V_RAM vRam = loadImageFromFile(IMAGE_PATH);
        if (vRam == null) return;

        // Zobrazí původni obrázek
        mainWindow.showImage(vRam.getImage());
        delay(1500);

        // Experimenty s různými hodnotami HUE
        // HUE 210° = tmavě modrá
        mainWindow.showImage(removeRedEye(vRam.getCopy(), 210).getImage());
        delay(1500);

        // HUE 180° = azurová/tyrkysová
        mainWindow.showImage(removeRedEye(vRam.getCopy(), 180).getImage());
        delay(1500);

        // HUE 240° = fialová
        mainWindow.showImage(removeRedEye(vRam.getCopy(), 240).getImage());
        delay(15000);
    }

    // Aplikuje korekci červených očí v definovaných oblastech
    // Provádí několik průchodů pro zachycení všech červených pixelů včetně okrajových
    private V_RAM removeRedEye(V_RAM vRam, float hueShift) {
        // Více průchodů zajisti smooth odstranění i okrajových červených pixelů
        for (int iteration = 0; iteration < 3; iteration++) {
            processEyeArea(vRam, LEFT_EYE_X1, LEFT_EYE_Y1, LEFT_EYE_X2, LEFT_EYE_Y2, hueShift);
            processEyeArea(vRam, RIGHT_EYE_X1, RIGHT_EYE_Y1, RIGHT_EYE_X2, RIGHT_EYE_Y2, hueShift);
        }
        return vRam;
    }

    // Zpracování obdélníkové oblasti oka - bezkontextové zpracování
    // Najde červené pixely a opraví je pomocí HUE posunu.
    private void processEyeArea(V_RAM vRam, int x1, int y1, int x2, int y2, float hueShift) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                RGB rgb = new RGB(vRam.getPixel(x, y));

                if (isRedPixel(rgb)) {
                    // RGB -> HSL
                    HSL hsl = new HSL(rgb);

                    // Posun HUE (barevný ton)
                    hsl.H = (hsl.H + hueShift) % 360;
                    if (hsl.H < 0) hsl.H += 360;

                    // Jemnější snížení sytosti a jasu
                    hsl.S *= 0.2f;
                    hsl.L *= 0.3f;

                    // HSL -> RGB
                    RGB corrected = new RGB(hsl);
                    vRam.setPixel(x, y, corrected.R, corrected.G, corrected.B);
                }
            }
        }
    }

    // Detekce červeneho pixelu
    private boolean isRedPixel(RGB rgb) {
        // Silně červené pixely (střed očí)
        boolean strongRed = rgb.R > 100 && rgb.R > rgb.G * 1.5 && rgb.R > rgb.B * 1.5;

        // Slabě červené pixely (okraje, halo efekt)
        boolean weakRed = rgb.R > 60 && rgb.R > rgb.G * 1.2 && rgb.R > rgb.B * 1.2;

        return strongRed || weakRed;
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
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
