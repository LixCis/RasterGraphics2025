package cz.osu.tasks;

import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;
import cz.osu.utils.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * KU1_TWO - Kontextové zpracování šedotónového obrazu s konvolučním vyhlazením s prahem
 */
public class KU1_TWO {
    private final MainWindow mainWindow;

    private static final String[] IMAGE_PATHS = {
            "src/cz/osu/images/image1.png",
            "src/cz/osu/images/image2.png",
            "src/cz/osu/images/image3.png"
    };

    private static final int[] THRESHOLD_VALUES = {5, 15, 30, 50, 100};

    // Aritmetický průměr (metoda 1)
    private static final float[][] KERNEL_ARITHMETIC = {
            {1/9f, 1/9f, 1/9f},
            {1/9f, 1/9f, 1/9f},
            {1/9f, 1/9f, 1/9f}
    };

    // Vážený průměr (metoda 2) - použito
    private static final float[][] KERNEL_WEIGHTED = {
            {1/16f, 2/16f, 1/16f},
            {2/16f, 4/16f, 2/16f},
            {1/16f, 2/16f, 1/16f}
    };

    public KU1_TWO(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void run() {
        System.out.println("=== KU1_TWO: Kontextové zpracování šedotónového obrazu ===\n");

        for (int i = 0; i < IMAGE_PATHS.length; i++) {
            System.out.println("Zpracování obrázku " + (i + 1) + ": " + IMAGE_PATHS[i]);

            V_RAM original = loadImageFromFile(IMAGE_PATHS[i]);
            if (original == null) {
                System.out.println("  Chyba při načítání obrázku!");
                continue;
            }

            mainWindow.showImage(original.getImage());
            delay(1000);

            V_RAM grayscale = convertToGrayscale(original);
            mainWindow.showImage(grayscale.getImage());
            System.out.println("  Převod na šedotónový obraz dokončen");
            delay(1000);

            for (int threshold : THRESHOLD_VALUES) {
                System.out.println("  Aplikace konvoluce s prahem T = " + threshold);

                V_RAM processed = applyConvolutionWithThreshold(
                        grayscale.getCopy(),
                        KERNEL_WEIGHTED,
                        threshold
                );

                mainWindow.showImage(processed.getImage());
                delay(1500);
            }

            System.out.println("  Zpracování obrázku " + (i + 1) + " dokončeno\n");
            delay(500);
        }

        System.out.println("=== Demonstrace dokončena ===");
    }

    // Převod barevného obrázku na šedotónový
    // I(x,y) = 0.299·R + 0.587·G + 0.114·B
    private V_RAM convertToGrayscale(V_RAM vRam) {
        V_RAM grayscale = new V_RAM(vRam.getWidth(), vRam.getHeight());

        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                RGB rgb = new RGB(vRam.getPixel(x, y));

                int intensity = (int)(0.299f * rgb.R + 0.587f * rgb.G + 0.114f * rgb.B);
                intensity = Math.clamp(intensity, 0, 255);

                grayscale.setPixel(x, y, intensity, intensity, intensity);
            }
        }

        return grayscale;
    }

    // Aplikace konvolučního vyhlazení s prahem
    private V_RAM applyConvolutionWithThreshold(V_RAM vRam, float[][] kernel, int threshold) {
        int width = vRam.getWidth();
        int height = vRam.getHeight();
        V_RAM result = new V_RAM(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RGB originalRgb = new RGB(vRam.getPixel(x, y));
                int originalValue = originalRgb.R;

                float convolvedValue = applyKernel(vRam, x, y, kernel);

                int difference = Math.abs((int)convolvedValue - originalValue);
                int finalValue;

                if (difference < threshold) {
                    finalValue = (int)convolvedValue;
                } else {
                    finalValue = originalValue;
                }

                finalValue = Math.clamp(finalValue, 0, 255);
                result.setPixel(x, y, finalValue, finalValue, finalValue);
            }
        }

        return result;
    }

    // Aplikace konvolučního jádra na pixel (x, y)
    // Použita metoda fiktivního zvětšení (edge padding)
    private float applyKernel(V_RAM vRam, int x, int y, float[][] kernel) {
        int width = vRam.getWidth();
        int height = vRam.getHeight();
        int kernelSize = kernel.length;
        int offset = kernelSize / 2;

        float sum = 0;

        for (int ky = 0; ky < kernelSize; ky++) {
            for (int kx = 0; kx < kernelSize; kx++) {
                int imageX = x + kx - offset;
                int imageY = y + ky - offset;

                // Ošetření hranic - edge padding
                imageX = Math.clamp(imageX, 0, width - 1);
                imageY = Math.clamp(imageY, 0, height - 1);

                RGB rgb = new RGB(vRam.getPixel(imageX, imageY));
                int pixelValue = rgb.R;

                sum += pixelValue * kernel[ky][kx];
            }
        }

        return sum;
    }

    private V_RAM loadImageFromFile(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            if (image == null) {
                System.err.println("Nelze načíst obrázek: " + path);
                return null;
            }

            V_RAM vRam = new V_RAM(image.getWidth(), image.getHeight());
            for (int y = 0; y < vRam.getHeight(); y++) {
                for (int x = 0; x < vRam.getWidth(); x++) {
                    vRam.setPixelsInt(x, y, image.getRGB(x, y));
                }
            }
            return vRam;

        } catch (IOException e) {
            System.err.println("Chyba při čtení souboru: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Pomocná metoda pro ruční testování konkrétního obrázku a prahu
    public void testSingleImage(String imagePath, int threshold) {
        System.out.println("Test: " + imagePath + " s prahem T = " + threshold);

        V_RAM original = loadImageFromFile(imagePath);
        if (original == null) return;

        V_RAM grayscale = convertToGrayscale(original);
        V_RAM processed = applyConvolutionWithThreshold(grayscale, KERNEL_WEIGHTED, threshold);

        mainWindow.showImage(original.getImage());
        delay(1000);
        mainWindow.showImage(grayscale.getImage());
        delay(1000);
        mainWindow.showImage(processed.getImage());
    }

    // Ukázka porovnání obou metod průměrování
    public void compareAveraging(String imagePath, int threshold) {
        System.out.println("Porovnání metod průměrování na: " + imagePath);

        V_RAM original = loadImageFromFile(imagePath);
        if (original == null) return;

        V_RAM grayscale = convertToGrayscale(original);

        V_RAM arithmetic = applyConvolutionWithThreshold(
                grayscale.getCopy(),
                KERNEL_ARITHMETIC,
                threshold
        );

        V_RAM weighted = applyConvolutionWithThreshold(
                grayscale.getCopy(),
                KERNEL_WEIGHTED,
                threshold
        );

        System.out.println("Původní:");
        mainWindow.showImage(original.getImage());
        delay(1500);

        System.out.println("Aritmetický průměr:");
        mainWindow.showImage(arithmetic.getImage());
        delay(1500);

        System.out.println("Vážený průměr:");
        mainWindow.showImage(weighted.getImage());
        delay(1500);
    }
}
