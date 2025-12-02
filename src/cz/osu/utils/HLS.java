package cz.osu.utils;

import static java.lang.Math.clamp;

public class HLS {
    public int hue;
    public float lightness;
    public float saturation;

    public HLS(int hue, float lightness, float saturation) {
        this.hue = (hue % 360);
        this.lightness = clamp(lightness, 0, 1);
        this.saturation = clamp(saturation, 0, 1);
    }
    public HLS(RGB rgb){
        float[] hls = rgbToHLS(rgb);
        this.hue = Math.round(hls[0] * 360f) % 360;
        this.lightness = hls[1];
        this.saturation = hls[2];
    }
    private float[] rgbToHLS(RGB rgb){
        float[] normalized = normalizeRGB(rgb);

        float min = Math.min(normalized[0], Math.min(normalized[1], normalized[2]));
        float max = Math.max(normalized[0], Math.max(normalized[1], normalized[2]));
        float lightness = (min + max) / 2f;
        float saturation;
        float hue;
        if (min == max) {
            hue = 0;
            saturation = 0;
        } else {
            float delta = max - min;
            saturation = lightness<0.5 ? delta / (max + min) : delta / (2 - max - min);
            if (max == normalized[0]) {
                hue = (normalized[1] - normalized[2]) / delta + (normalized[1] < normalized[2] ? 6 : 0);
            } else if (max == normalized[1]) {
                hue = (normalized[2] - normalized[0]) / delta + 2;
            } else {
                hue = (normalized[0] - normalized[1]) / delta + 4;
            }
            hue /= 6;
        }
        return new float[]{hue, lightness, saturation};
    }
    private RGB toRgb(HLS hls){
        float normalizedHue = hls.hue / 360f;

        int red, green, blue;
        if (hls.saturation == 0) {
            red = green = blue = Math.round(hls.lightness * 255);
        } else {
            float var_2 = (hls.lightness < 0.5) ? (hls.lightness * (1 + hls.saturation)) :
                    (hls.lightness + hls.saturation - (hls.lightness * hls.saturation));
            float var_1 = 2 * hls.lightness - var_2;

            red = Math.round(hueToRgb(var_1, var_2, normalizedHue + (1f / 3))*255);
            green = Math.round(hueToRgb(var_1, var_2, normalizedHue)*255);
            blue = Math.round(hueToRgb(var_1, var_2, normalizedHue - (1f / 3))*255);
        }
        return new RGB(red, green, blue);
    }
    private float hueToRgb(float v1, float v2, float vH) {
        if (vH < 0) vH += 1;
        if (vH > 1) vH -= 1;
        if (6 * vH < 1) return (v1 + (v2 - v1) * 6 * vH);
        if (2 * vH < 1) return v2;
        if (3 * vH < 2) return (v1 + (v2 - v1) * ((2f / 3) - vH) * 6);
        return v1;
    }
    private float[] normalizeRGB(RGB rgb){
        float[] normalized = new float[3];
        normalized[0] = rgb.R/(float)255;
        normalized[1] = rgb.G/(float)255;
        normalized[2] = rgb.B/(float)255;
        return normalized;
    }
    public void hueShift(float shift){
        float shiftedHue;
        if (shift<0){ //-1000
            float correctedAbsolute = Math.abs(shift) % 360; //280
            shiftedHue = this.hue - correctedAbsolute; //100 - 280 = -180
            if (shiftedHue<0) shiftedHue += 360;//-180 + 360 = 180
        }else{ //1000
            float correctedShift = shift % 360; //280
            shiftedHue = this.hue + correctedShift;
            shiftedHue %= 360;
        }
        this.hue = Math.round(shiftedHue);
    }
    public int toRGB(){
        return RGB.toInt(toRgb(this));
    }
}
