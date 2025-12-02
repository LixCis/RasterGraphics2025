package cz.osu.utils;

public class RGB {
    public int R;
    public int G;
    public int B;

    public RGB(int argb){
        R = (argb >> 16) & 0xFF;
        G = (argb >> 8) & 0xFF;
        B = argb & 0xFF; //0x11111111
    }

    public RGB(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }

    public static int toInt(RGB rgb){
        return toInt(rgb.R, rgb.G, rgb.B);
    }

    public static int toInt(int r, int g, int b){ //255 red green blue
        //int argb = 11111111 red green blue
        //když red == 255 tak:
        //int red = 00000000 00000000 00000000 11111111
        int argb = 0;
        argb |= (255 << 24);
        argb |= ((Math.clamp(r, 0, 255) & 0b11111111) << 16);
        argb |= ((Math.clamp(g, 0, 255) & 0b11111111) << 8);
        argb |= (Math.clamp(b, 0, 255) & 0b11111111);
        return argb;
    }
}
