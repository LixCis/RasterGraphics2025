package cz.osu.exercises;

import cz.osu.main.V_RAM;

import java.awt.*;

public class Cv06_Curves {
    public static void drawCurve(V_RAM vRam, Point p0, Point p1, Point p2, Point p3, Color color){

        BezierCurve curve = new BezierCurve(p0, p1, p2, p3);

        for (double t = 0; t < 1; t += 0.01) {
            Point point1 = curve.getPoint(t);
            Point point2 = curve.getPoint(t + 0.01);
            Cv05_LinesDrawing.drawLine(vRam, point1.x, point1.y, point2.x, point2.y, color);
        }
    }
}
