package cz.osu.tasks;

import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;
import cz.osu.utils.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// KU1_TWO - Kontextové zpracování šedotónového obrazu - konvoluční vyhlazení s prahem

public class KU1_TWO {
    private final MainWindow mainWindow;

    private static final String[] IMAGE_PATHS = {
            "src/cz/osu/images/image1.png",
            "src/cz/osu/images/image2.png",
            "src/cz/osu/images/image3.png"
    };

    // Vážený průměr 3x3 (Gaussovské vyhlazení)
    private static final float[][] KERNEL = {
            {1/16f, 2/16f, 1/16f},
            {2/16f, 4/16f, 2/16f},
            {1/16f, 2/16f, 1/16f}
    };

    public KU1_TWO(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void run() {
        // Pro každý obrázek ukáže: původni -> šedotónový -> různé prahy
        for (String imagePath : IMAGE_PATHS) {
            V_RAM vRam = loadImageFromFile(imagePath);
            if (vRam == null) continue;

            System.out.println("Zpracování: " + imagePath);

            // Původní barevný obrázek
            mainWindow.showImage(vRam.getImage());
            delay(1500);

            // Převod na šedotónový
            V_RAM grayscale = convertToGrayscale(vRam);
            mainWindow.showImage(grayscale.getImage());
            delay(1500);

            // Různé hodnoty prahu T
            System.out.println("  T = 10");
            mainWindow.showImage(applySmoothing(grayscale.getCopy(), 5).getImage());
            delay(1500);

            System.out.println("  T = 15"); // Image 3
            mainWindow.showImage(applySmoothing(grayscale.getCopy(), 15).getImage());
            delay(1500);

            System.out.println("  T = 25"); // Image 1
            mainWindow.showImage(applySmoothing(grayscale.getCopy(), 25).getImage());
            delay(1500);

            System.out.println("  T = 35"); // Image 2
            mainWindow.showImage(applySmoothing(grayscale.getCopy(), 35).getImage());
            delay(1500);

            System.out.println("  T = 50");
            mainWindow.showImage(applySmoothing(grayscale.getCopy(), 50).getImage());
            delay(1500);

            System.out.println();
        }
    }

    // Převod barevného obrázku na šedotónový podle vzorce:
    // I(x,y) = 0.299·R + 0.587·G + 0.114·B
    private V_RAM convertToGrayscale(V_RAM vRam) {
        V_RAM grayscale = new V_RAM(vRam.getWidth(), vRam.getHeight());

        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                RGB rgb = new RGB(vRam.getPixel(x, y));
                int intensity = (int)(0.299f * rgb.R + 0.587f * rgb.G + 0.114f * rgb.B);
                grayscale.setPixel(x, y, intensity, intensity, intensity);
            }
        }
        return grayscale;
    }

    // Konvoluční vyhlazení s prahem - kontextové zpracování
    // F(x,y) = (a⊗f)(x,y) pokud |(a⊗f)(x,y) - f(x,y)| < T
    // F(x,y) = f(x,y) jinak
    private V_RAM applySmoothing(V_RAM vRam, int threshold) {
        V_RAM result = new V_RAM(vRam.getWidth(), vRam.getHeight());

        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                RGB original = new RGB(vRam.getPixel(x, y));
                int originalValue = original.R;

                // Aplikace konvoluce
                int smoothedValue = (int)applyKernel(vRam, x, y);

                // Rozhodnutí podle prahu
                int difference = Math.abs(smoothedValue - originalValue);
                int finalValue = (difference < threshold) ? smoothedValue : originalValue;

                result.setPixel(x, y, finalValue, finalValue, finalValue);
            }
        }
        return result;
    }

    // Aplikace konvolučního jadra na pixel (x, y)
    // Ošetření hranic metodou edge padding (fiktivní zvětšení)
    private float applyKernel(V_RAM vRam, int x, int y) {
        int offset = KERNEL.length / 2;
        float sum = 0;

        for (int ky = 0; ky < KERNEL.length; ky++) {
            for (int kx = 0; kx < KERNEL.length; kx++) {
                int imageX = Math.clamp(x + kx - offset, 0, vRam.getWidth() - 1);
                int imageY = Math.clamp(y + ky - offset, 0, vRam.getHeight() - 1);

                RGB rgb = new RGB(vRam.getPixel(imageX, imageY));
                sum += rgb.R * KERNEL[ky][kx];
            }
        }

        return sum;
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
