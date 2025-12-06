package cz.osu.exercises;

public class Matrix2D {

    public double a1, a2, a3;
    public double b1, b2, b3;
    public double c1, c2, c3;

    public Matrix2D(
            double a1, double a2, double a3,
            double b1, double b2, double b3,
            double c1, double c2, double c3)
    {
        this.a1 = a1; this.a2 = a2; this.a3 = a3;
        this.b1 = b1; this.b2 = b2; this.b3 = b3;
        this.c1 = c1; this.c2 = c2; this.c3 = c3;
    }

    public static Matrix2D identity(){
        return new Matrix2D(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1);
    }

    public static Matrix2D translate(double tx, double ty){
        Matrix2D result = Matrix2D.identity();
        result.a3 = tx;
        result.b3 = ty;
        return result;
    }

    public static Matrix2D rotate(double degrees){
        Matrix2D result = Matrix2D.identity();
        double rad = Math.toRadians(degrees);
        result.a1 = Math.cos(rad);
        result.a2 = -Math.sin(rad);
        result.b1 = Math.sin(rad);
        result.b2 = Math.cos(rad);
        return result;
    }

    public static Matrix2D scale(double ratio){
        Matrix2D result = Matrix2D.identity();
        result.a1 = ratio;
        result.b2 = ratio;
        return result;
    }

    public static Matrix2D rotate(double degrees, Point2D pivot){
        // Rotate around pivot point: translate to origin, rotate, translate back
        Matrix2D translateToOrigin = translate(-pivot.x, -pivot.y);
        Matrix2D rotation = rotate(degrees);
        Matrix2D translateBack = translate(pivot.x, pivot.y);
        return translateBack.multiply(rotation).multiply(translateToOrigin);
    }

    public static Matrix2D scale(double sx, double sy, Point2D pivot){
        // Scale around pivot point: translate to origin, scale, translate back
        Matrix2D translateToOrigin = translate(-pivot.x, -pivot.y);
        Matrix2D scaleMatrix = Matrix2D.identity();
        scaleMatrix.a1 = sx;
        scaleMatrix.b2 = sy;
        Matrix2D translateBack = translate(pivot.x, pivot.y);
        return translateBack.multiply(scaleMatrix).multiply(translateToOrigin);
    }

    public Point2D multiply(Point2D point){
        double xn = a1 * point.x + a2 * point.y + a3 * point.a;
        double yn = b1 * point.x + b2 * point.y + b3 * point.a;
        double an = c1 * point.x + c2 * point.y + c3 * point.a;
        Point2D result = new Point2D(xn, yn);
        result.a = an;
        return result;
    }

    public Matrix2D multiply(Matrix2D matrix){
        Matrix2D m = matrix;
        double r_a1 = a1 * m.a1 + a2 * m.b1 + a3 * m.c1;
        double r_a2 = a1 * m.a2 + a2 * m.b2 + a3 * m.c2;
        double r_a3 = a1 * m.a3 + a2 * m.b3 + a3 * m.c3;

        double r_b1 = b1 * m.a1 + b2 * m.b1 + b3 * m.c1;
        double r_b2 = b1 * m.a2 + b2 * m.b2 + b3 * m.c2;
        double r_b3 = b1 * m.a3 + b2 * m.b3 + b3 * m.c3;

        double r_c1 = c1 * m.a1 + c2 * m.b1 + c3 * m.c1;
        double r_c2 = c1 * m.a2 + c2 * m.b2 + c3 * m.c2;
        double r_c3 = c1 * m.a3 + c2 * m.b3 + c3 * m.c3;
        return new Matrix2D(
                r_a1, r_a2, r_a3,
                r_b1, r_b2, r_b3,
                r_c1, r_c2, r_c3);
    }

    @Override
    public String toString() {
        return "Matrix2D{\n" +
                a1 + " " + a2 + " " + a3 + "\n" +
                b1 + " " + b2 + " " + b3 + "\n" +
                c1 + " " + c2 + " " + c3 + "}";
    }
}
