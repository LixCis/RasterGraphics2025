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
}
