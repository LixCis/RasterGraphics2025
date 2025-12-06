package cz.osu.main;

import cz.osu.exercises.Cv01_RGB;

/**
 * Registry všech exercises - každý jako inner class
 */
public class Exercises {

    /**
     * CV01 - RGB Model
     */
    public static class CV01_RGB implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV01 - RGB Model";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            Cv01_RGB cv01 = new Cv01_RGB();
            double delay = 0.7;

            mainWindow.getImagePanel().setImage(cv01.solidWithBorder().getImage());
            delaySeconds(delay);

            mainWindow.getImagePanel().setImage(cv01.redGreenGradientWithBlue().getImage());
            delaySeconds(delay);

            mainWindow.getImagePanel().setImage(cv01.greenBlueGradient().getImage());
            delaySeconds(delay);

            mainWindow.getImagePanel().setImage(cv01.redGreenBlueInMiddle().getImage());
        }
    }

    /**
     * CV02 - Images (Grayscale, Hue shift, atd.)
     */
    public static class CV02_Images implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV02 - Images (Grayscale, Hue Shift)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("CV02: Vyberte obrázek pro zpracování...");

            V_RAM image = mainWindow.loadImageAsVRAM();

            if (image == null || image.getWidth() == 0) {
                System.out.println("CV02: Nebyl vybrán žádný obrázek");
                return;
            }

            double delay = 1.0;

            // Původní obrázek
            mainWindow.showImage(image.getImage());
            System.out.println("CV02: Původní obrázek");
            delaySeconds(delay);

            // Grayscale
            V_RAM grayscale = cz.osu.exercises.Cv02_Images.grayscale(image);
            mainWindow.showImage(grayscale.getImage());
            System.out.println("CV02: Grayscale");
            delaySeconds(delay);

            // Hue shift +60
            V_RAM hueShift60 = cz.osu.exercises.Cv02_Images.shiftHue(image, 60);
            mainWindow.showImage(hueShift60.getImage());
            System.out.println("CV02: Hue shift +60");
            delaySeconds(delay);

            // Hue shift +120
            V_RAM hueShift120 = cz.osu.exercises.Cv02_Images.shiftHue(image, 120);
            mainWindow.showImage(hueShift120.getImage());
            System.out.println("CV02: Hue shift +120");
            delaySeconds(delay);

            // Hue shift +180
            V_RAM hueShift180 = cz.osu.exercises.Cv02_Images.shiftHue(image, 180);
            mainWindow.showImage(hueShift180.getImage());
            System.out.println("CV02: Hue shift +180");
            delaySeconds(delay);

            // Zpět na původní
            mainWindow.showImage(image.getImage());
            System.out.println("CV02: Zpět na původní");
        }
    }

    /**
     * CV03 - Convolution (Blur, Sharpen)
     */
    public static class CV03_Convolution implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV03 - Convolution (Blur)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("CV03: Vyberte obrázek pro konvoluci...");

            V_RAM image = mainWindow.loadImageAsVRAM();

            if (image == null || image.getWidth() == 0) {
                System.out.println("CV03: Nebyl vybrán žádný obrázek");
                return;
            }

            double delay = 1.5;

            // Původní obrázek
            mainWindow.showImage(image.getImage());
            System.out.println("CV03: Původní obrázek");
            delaySeconds(delay);

            // Blur 3x3
            V_RAM blur3 = image.getCopy();
            cz.osu.exercises.Cv03_Convolution.convolution(blur3, cz.osu.utils.Kernel.simpleBlurKernel(3));
            mainWindow.showImage(blur3.getImage());
            System.out.println("CV03: Blur 3x3");
            delaySeconds(delay);

            // Blur 5x5
            V_RAM blur5 = image.getCopy();
            cz.osu.exercises.Cv03_Convolution.convolution(blur5, cz.osu.utils.Kernel.simpleBlurKernel(5));
            mainWindow.showImage(blur5.getImage());
            System.out.println("CV03: Blur 5x5");
            delaySeconds(delay);

            // Blur 10x10
            V_RAM blur10 = image.getCopy();
            cz.osu.exercises.Cv03_Convolution.convolution(blur10, cz.osu.utils.Kernel.simpleBlurKernel(10));
            mainWindow.showImage(blur10.getImage());
            System.out.println("CV03: Blur 10x10");
            delaySeconds(delay);

            // Zpět na původní
            mainWindow.showImage(image.getImage());
            System.out.println("CV03: Zpět na původní");
        }
    }

    /**
     * CV04 - Compression (TODO)
     */
    public static class CV04_Compression implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV04 - Compression (TODO)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("CV04: Zatím neimplementováno");
        }
    }

    /**
     * CV05 - Lines Drawing (DDA Algorithm)
     */
    public static class CV05_LinesDrawing implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV05 - Lines Drawing";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            V_RAM vRam = new V_RAM(400, 400);
            double delay = 0.8;

            // Nakreslit různé čáry
            System.out.println("CV05: Kreslení čar pomocí DDA algoritmu");

            // Horizontální čára
            cz.osu.exercises.Cv05_LinesDrawing.drawLine(vRam, 50, 50, 350, 50, java.awt.Color.RED);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            // Vertikální čára
            cz.osu.exercises.Cv05_LinesDrawing.drawLine(vRam, 50, 50, 50, 350, java.awt.Color.GREEN);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            // Diagonální čára
            cz.osu.exercises.Cv05_LinesDrawing.drawLine(vRam, 50, 50, 350, 350, java.awt.Color.BLUE);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            // Různé úhly
            cz.osu.exercises.Cv05_LinesDrawing.drawLine(vRam, 200, 200, 350, 100, java.awt.Color.YELLOW);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            cz.osu.exercises.Cv05_LinesDrawing.drawLine(vRam, 200, 200, 100, 350, java.awt.Color.MAGENTA);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            // Kružnice z čar
            int centerX = 200, centerY = 200, radius = 100;
            for (int angle = 0; angle < 360; angle += 15) {
                double rad = Math.toRadians(angle);
                int x = centerX + (int)(Math.cos(rad) * radius);
                int y = centerY + (int)(Math.sin(rad) * radius);
                cz.osu.exercises.Cv05_LinesDrawing.drawLine(vRam, centerX, centerY, x, y, java.awt.Color.CYAN);
            }
            mainWindow.showImage(vRam.getImage());
            System.out.println("CV05: Hotovo");
        }
    }

    /**
     * CV06 - Curves (Bézier)
     */
    public static class CV06_Curves implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV06 - Bézier Curves";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            V_RAM vRam = new V_RAM(400, 400);
            double delay = 1.0;

            System.out.println("CV06: Bézierovy křivky");

            // První křivka
            java.awt.Point p0 = new java.awt.Point(50, 350);
            java.awt.Point p1 = new java.awt.Point(100, 50);
            java.awt.Point p2 = new java.awt.Point(300, 50);
            java.awt.Point p3 = new java.awt.Point(350, 350);

            cz.osu.exercises.Cv06_Curves.drawCurve(vRam, p0, p1, p2, p3, java.awt.Color.RED);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            // Druhá křivka
            java.awt.Point p0b = new java.awt.Point(50, 200);
            java.awt.Point p1b = new java.awt.Point(150, 100);
            java.awt.Point p2b = new java.awt.Point(250, 300);
            java.awt.Point p3b = new java.awt.Point(350, 200);

            cz.osu.exercises.Cv06_Curves.drawCurve(vRam, p0b, p1b, p2b, p3b, java.awt.Color.BLUE);
            mainWindow.showImage(vRam.getImage());
            delaySeconds(delay);

            // Třetí křivka - S shape
            java.awt.Point p0c = new java.awt.Point(100, 100);
            java.awt.Point p1c = new java.awt.Point(100, 200);
            java.awt.Point p2c = new java.awt.Point(300, 200);
            java.awt.Point p3c = new java.awt.Point(300, 300);

            cz.osu.exercises.Cv06_Curves.drawCurve(vRam, p0c, p1c, p2c, p3c, java.awt.Color.GREEN);
            mainWindow.showImage(vRam.getImage());

            System.out.println("CV06: Hotovo");
        }
    }

    /**
     * CV07 - Affine Transformations 2D
     */
    public static class CV07_AffineTransformations2D implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "CV07 - Affine Transformations 2D";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            V_RAM vRam = new V_RAM(400, 400);
            double delay = 1.0;

            System.out.println("CV07: 2D Affinní transformace");

            // Původní čtverec
            cz.osu.exercises.Point2D p1 = new cz.osu.exercises.Point2D(100, 100);
            cz.osu.exercises.Point2D p3 = new cz.osu.exercises.Point2D(200, 200);

            cz.osu.exercises.Cv07_AffineTransformations2D.drawSquare(vRam, p1, p3, java.awt.Color.RED);
            mainWindow.showImage(vRam.getImage());
            System.out.println("CV07: Původní čtverec");
            delaySeconds(delay);

            // Získat body čtverce pro transformace
            java.util.ArrayList<cz.osu.exercises.Point2D> square =
                cz.osu.exercises.Cv07_AffineTransformations2D.getSquare(p1, p3);

            // Translace
            cz.osu.exercises.Matrix2D translate = cz.osu.exercises.Matrix2D.translate(100, 50);
            java.util.ArrayList<cz.osu.exercises.Point2D> translated =
                cz.osu.exercises.Cv07_AffineTransformations2D.transformPolygon(square, translate);
            cz.osu.exercises.Cv07_AffineTransformations2D.drawPolygon(vRam, translated, java.awt.Color.GREEN);
            mainWindow.showImage(vRam.getImage());
            System.out.println("CV07: Translace (+100, +50)");
            delaySeconds(delay);

            // Rotace kolem středu čtverce
            cz.osu.exercises.Point2D center = new cz.osu.exercises.Point2D(150, 150);
            cz.osu.exercises.Matrix2D rotate = cz.osu.exercises.Matrix2D.rotate(45, center);
            java.util.ArrayList<cz.osu.exercises.Point2D> rotated =
                cz.osu.exercises.Cv07_AffineTransformations2D.transformPolygon(square, rotate);
            cz.osu.exercises.Cv07_AffineTransformations2D.drawPolygon(vRam, rotated, java.awt.Color.BLUE);
            mainWindow.showImage(vRam.getImage());
            System.out.println("CV07: Rotace (45° kolem středu)");
            delaySeconds(delay);

            // Škálování kolem středu
            cz.osu.exercises.Matrix2D scale = cz.osu.exercises.Matrix2D.scale(1.5, 1.5, center);
            java.util.ArrayList<cz.osu.exercises.Point2D> scaled =
                cz.osu.exercises.Cv07_AffineTransformations2D.transformPolygon(square, scale);
            cz.osu.exercises.Cv07_AffineTransformations2D.drawPolygon(vRam, scaled, java.awt.Color.YELLOW);
            mainWindow.showImage(vRam.getImage());
            System.out.println("CV07: Škálování (1.5x)");
            delaySeconds(delay);

            // Kombinovaná transformace
            cz.osu.exercises.Matrix2D combined = rotate.multiply(scale);
            java.util.ArrayList<cz.osu.exercises.Point2D> combined_result =
                cz.osu.exercises.Cv07_AffineTransformations2D.transformPolygon(square, combined);
            cz.osu.exercises.Cv07_AffineTransformations2D.drawPolygon(vRam, combined_result, java.awt.Color.MAGENTA);
            mainWindow.showImage(vRam.getImage());
            System.out.println("CV07: Kombinovaná transformace (rotace + škálování)");

            System.out.println("CV07: Hotovo");
        }
    }

    /**
     * Test01 - Manual Matrix2D and Point2D Testing
     */
    public static class Test01 implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "Test01 - Matrix2D & Point2D Manual Test";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("Test01: Manuální testování Matrix2D a Point2D");

            V_RAM vRam = new V_RAM(200, 200);

            // Původní bod - bílý
            cz.osu.exercises.Point2D p1 = new cz.osu.exercises.Point2D(15.6, 10.3);
            java.awt.Point p1i = p1.getPoint();
            vRam.setPixel(p1i.x, p1i.y, 255, 255, 255);
            System.out.println("p1 (bílý): (" + p1i.x + ", " + p1i.y + ")");

            // Translace - zelený
            cz.osu.exercises.Matrix2D t1 = cz.osu.exercises.Matrix2D.translate(6, 11);
            cz.osu.exercises.Point2D p2 = t1.multiply(p1);
            java.awt.Point p2i = p2.getPoint();
            vRam.setPixel(p2i.x, p2i.y, 0, 255, 0);
            System.out.println("p2 (zelený) = translate(6,11) * p1: (" + p2i.x + ", " + p2i.y + ")");

            // Rotace aplikovaná na translovaný bod - modrý
            cz.osu.exercises.Matrix2D t2 = cz.osu.exercises.Matrix2D.rotate(10);
            cz.osu.exercises.Point2D p3 = t2.multiply(p2);
            java.awt.Point p3i = p3.getPoint();
            vRam.setPixel(p3i.x, p3i.y, 0, 0, 255);
            System.out.println("p3 (modrý) = rotate(10) * p2: (" + p3i.x + ", " + p3i.y + ")");

            // Kombinovaná transformace - cyan
            cz.osu.exercises.Matrix2D total = t2.multiply(t1);
            cz.osu.exercises.Point2D p4 = total.multiply(p1);
            java.awt.Point p4i = p4.getPoint();
            vRam.setPixel(p4i.x, p4i.y, 0, 255, 255);
            System.out.println("p4 (cyan) = (rotate(10) * translate(6,11)) * p1: (" + p4i.x + ", " + p4i.y + ")");

            // Ověření: p3 a p4 by měly být na stejné pozici
            if (p3i.x == p4i.x && p3i.y == p4i.y) {
                System.out.println("✓ Test úspěšný: p3 == p4 (maticová multiplikace funguje správně)");
            } else {
                System.out.println("✗ Test neúspěšný: p3 != p4");
                System.out.println("  p3: (" + p3i.x + ", " + p3i.y + ")");
                System.out.println("  p4: (" + p4i.x + ", " + p4i.y + ")");
            }

            mainWindow.getImagePanel().setImage(vRam.getImage());
            System.out.println("Test01: Hotovo");
        }
    }

    // Pomocná metoda pro delay
    // Pokud je thread přerušen (interrupt), vyhodí InterruptedException
    private static void delaySeconds(double seconds) {
        try {
            Thread.sleep((long)(seconds * 1000d));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);  // Propagovat přerušení
        }
    }
}
