package cz.osu.utils;

// HSL - Hue, Saturation, Lightness color model
// HSL values in intervals:
// H = [0, 360)
// S = [0, 100]
// L = [0, 100]

public class HSL {
    public float H;
    public float S;
    public float L;

    public HSL(float h, float s, float l) {
        H = h;
        S = s;
        L = l;
    }

    public HSL(RGB rgb) {
        float r = (float) rgb.R / 255;
        float g = (float) rgb.G / 255;
        float b = (float) rgb.B / 255;

        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));

        H = 0;

        if (max == min)
            H = 0;
        else if (max == r)
            H = ((60 * (g - b) / (max - min)) + 360) % 360;
        else if (max == g)
            H = (60 * (b - r) / (max - min)) + 120;
        else if (max == b)
            H = (60 * (r - g) / (max - min)) + 240;

        L = (max + min) / 2;

        S = 0;

        if (max == min)
            S = 0;
        else if (L <= .5f)
            S = (max - min) / (max + min);
        else
            S = (max - min) / (2 - max - min);

        S *= 100;
        L *= 100;
    }

    public boolean checkIntervals() {
        if (H < 0 || H >= 360) return false;
        if (S < 0 || S > 100) return false;
        if (L < 0 || L > 100) return false;
        return true;
    }

    @Override
    public String toString() {
        return "HSL{" +
                "H=" + H +
                ", S=" + S +
                ", L=" + L +
                '}';
    }
}
