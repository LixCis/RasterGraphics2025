package cz.osu.exercises;

import cz.osu.main.V_RAM;

import java.awt.*;
import java.util.ArrayList;

public class Cv07_AffineTransformations2D {

    /**
     * Vytvoří čtverec ze dvou rohů (p1 a p3 jsou protilehlé rohy)
     */
    public static ArrayList<Point2D> getSquare(Point2D p1, Point2D p3){
        ArrayList<Point2D> points = new ArrayList<>();

        Point p1i = p1.getPoint();
        Point p3i = p3.getPoint();

        if (p1i.x == p3i.x && p1i.y == p3i.y){
            points.add(new Point2D(p1i.x, p1i.y));
            points.add(new Point2D(p1i.x, p1i.y));
            points.add(new Point2D(p1i.x, p1i.y));
            points.add(new Point2D(p1i.x, p1i.y));
            return points;
        }

        double dx = p3i.x - p1i.x;
        double dy = p3i.y - p1i.y;
        double centerX = (p1i.x + p3i.x) / 2.0;
        double centerY = (p1i.y + p3i.y) / 2.0;

        Point2D p2 = new Point2D(centerX + dx / 2, centerY - dy / 2);
        Point2D p4 = new Point2D(centerX - dx / 2, centerY + dy / 2);

        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);

        return points;
    }

    /**
     * Nakreslí čtverec
     */
    public static void drawSquare(V_RAM vRam, Point2D p1, Point2D p3, Color color){
        ArrayList<Point2D> square = getSquare(p1, p3);
        drawPolygon(vRam, square, color);
    }

    /**
     * Aplikuje transformaci na všechny body polygonu
     */
    public static ArrayList<Point2D> transformPolygon(ArrayList<Point2D> points, Matrix2D matrix){
        ArrayList<Point2D> transformed = new ArrayList<>();
        for (Point2D point : points) {
            transformed.add(matrix.multiply(point));
        }
        return transformed;
    }

    /**
     * Nakreslí polygon (spojí body čarami)
     */
    public static void drawPolygon(V_RAM vRam, ArrayList<Point2D> points, Color color){
        if (points.size() < 2) return;

        for (int i = 0; i < points.size(); i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get((i + 1) % points.size());

            Point p1i = p1.getPoint();
            Point p2i = p2.getPoint();

            Cv05_LinesDrawing.drawLine(vRam, p1i.x, p1i.y, p2i.x, p2i.y, color);
        }
    }
}
