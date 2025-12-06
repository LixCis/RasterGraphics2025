package cz.osu.exercises;

import cz.osu.main.V_RAM;

import java.awt.*;

public class Cv05_LinesDrawing {
    public static void drawLine(V_RAM vRam, int x1, int y1, int x2, int y2, Color color) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            vRam.setPixel(x1, y1, color.getRed(), color.getGreen(), color.getBlue());
            return;
        }

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            vRam.setPixel((int) Math.round(x), (int) Math.round(y), color.getRed(), color.getGreen(), color.getBlue());
            x += xIncrement;
            y += yIncrement;
        }
    }

    /**
     * Kreslení tlustší čáry
     */
    public static void drawThickLine(V_RAM vRam, int x1, int y1, int x2, int y2, Color color, int thickness) {
        int width = vRam.getWidth();
        int height = vRam.getHeight();

        for (int dy = -thickness/2; dy <= thickness/2; dy++) {
            for (int dx = -thickness/2; dx <= thickness/2; dx++) {
                int nx1 = x1 + dx;
                int ny1 = y1 + dy;
                int nx2 = x2 + dx;
                int ny2 = y2 + dy;

                if (nx1 >= 0 && nx1 < width && ny1 >= 0 && ny1 < height &&
                    nx2 >= 0 && nx2 < width && ny2 >= 0 && ny2 < height) {
                    drawLine(vRam, nx1, ny1, nx2, ny2, color);
                }
            }
        }
    }

    /**
     * Vykreslení bodu
     */
    public static void drawPoint(V_RAM vRam, int x, int y, Color color, int size) {
        for (int dy = -size; dy <= size; dy++) {
            for (int dx = -size; dx <= size; dx++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < vRam.getWidth() && py >= 0 && py < vRam.getHeight()) {
                    vRam.setPixel(px, py, color.getRed(), color.getGreen(), color.getBlue());
                }
            }
        }
    }
}
