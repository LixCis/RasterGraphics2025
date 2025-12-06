package cz.osu.utils;

public class RGB {
    public int R;
    public int G;
    public int B;

    public RGB(int argb) {
        R = (argb >> 16) & 0xFF;
        G = (argb >> 8) & 0xFF;
        B = argb & 0xFF;
    }

    public RGB(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }

    public RGB(HSL hsl) {
        float h = hsl.H / 360.0f;
        float s = hsl.S / 100f;
        float l = hsl.L / 100f;

        float q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        float p = 2 * l - q;

        float r = Math.max(0, hueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, hueToRGB(p, q, h));
        float b = Math.max(0, hueToRGB(p, q, h - (1.0f / 3.0f)));

        R = (int) (r * 255 + 0.5);
        G = (int) (g * 255 + 0.5);
        B = (int) (b * 255 + 0.5);
    }

    public int getARGB() {
        return 255 << 24 | (R & 0xFF) << 16 | (G & 0xFF) << 8 | (B & 0xFF);
    }

    private static float hueToRGB(float p, float q, float h) {
        if (h < 0) h += 1;
        if (h > 1) h -= 1;
        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }
        if (2 * h < 1) {
            return q;
        }
        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }
        return p;
    }

    public static int toInt(RGB rgb) {
        return toInt(rgb.R, rgb.G, rgb.B);
    }

    public static int toInt(int r, int g, int b) {
        int argb = 0;
        argb |= (255 << 24);
        argb |= ((clamp(r, 0, 255) & 0b11111111) << 16);
        argb |= ((clamp(g, 0, 255) & 0b11111111) << 8);
        argb |= (clamp(b, 0, 255) & 0b11111111);
        return argb;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public String toString() {
        return "RGB{" +
                "R=" + R +
                ", G=" + G +
                ", B=" + B +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RGB rgb = (RGB) o;

        if (R != rgb.R) return false;
        if (G != rgb.G) return false;
        return B == rgb.B;
    }

    @Override
    public int hashCode() {
        return getARGB();
    }
}
