package cz.osu.exercises;

import java.awt.*;

public class Point2D {
    public double x;
    public double y;
    public double a;

    public Point2D (double x, double y){
        this.x = x;
        this.y = y;
        this.a = 1;
    }
    public Point getPoint(){
        int xt = (int) Math.round(this.x/this.a);
        int yt = (int) Math.round(this.y/this.a);
        return new Point(xt, yt);
    }
}