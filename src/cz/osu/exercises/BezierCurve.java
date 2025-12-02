package cz.osu.exercises;

import java.awt.*;

public class BezierCurve {

    public Point p0;
    public Point p1;
    public Point p2;
    public Point p3;

    private double a0x;
    private double a1x;
    private double a2x;
    private double a3x;

    private double a0y;
    private double a1y;
    private double a2y;
    private double a3y;


    public BezierCurve(Point p0, Point p1, Point p2, Point p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        preComputer();
    }

    private void preComputer(){
        a0x = p0.x;
        a1x = -3 * p0.x + 3 * p1.x;
        a2x = 3 * p0.x - 6 * p1.x + 3 * p2.x;
        a3x = -p0.x + 3 * p1.x - 3 * p2.x + p3.x;

        a0y = p0.y;
        a1y = -3 * p0.y + 3 * p1.y;
        a2y = 3 * p0.y - 6 * p1.y + 3 * p2.y;
        a3y = -p0.y + 3 * p1.y - 3 * p2.y + p3.y;
    }

    public Point getPoint(double t) {
        double x = a0x + a1x * t + a2x * t * t + a3x * t * t * t;
        double y = a0y + a1y * t + a2y * t * t + a3y * t * t * t;
        return new Point((int) x, (int) y);
    }
}
