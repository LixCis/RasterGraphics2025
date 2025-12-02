package cz.osu.exercises;

import cz.osu.main.V_RAM;
import cz.osu.utils.HLS;
import cz.osu.utils.RGB;

public class Cv02_Images {

    public static V_RAM shiftHue(V_RAM vRam, float shift){
        V_RAM shiftedHueImage = new V_RAM(vRam.getWidth(),vRam.getHeight());
        for (int y = 0; y < shiftedHueImage.getHeight(); y++) {
            for (int x = 0; x < shiftedHueImage.getWidth(); x++) {
                shiftedHueImage.setPixelsInt(x, y, shiftHue(vRam.getPixel(x, y), shift));
            }
        }
        return shiftedHueImage;
    }

    private static int shiftHue(int argb, float shift){
        HLS hlsValue = new HLS(new RGB(argb));
        hlsValue.hueShift(shift);
        return hlsValue.toRGB();
    }

    private static int[] disolveAsRGB(int rgb){
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new int[]{red,green,blue};
    }

    private static int averageGray(int rgb){
        int[] rgbComponents = disolveAsRGB(rgb);
        int averageGrayComponent = (rgbComponents[0] + rgbComponents[1] + rgbComponents[2])/rgbComponents.length;
        int averageGray = 255 << 24 | (averageGrayComponent & 0xFF) << 16 | (averageGrayComponent & 0xFF) << 8 | (averageGrayComponent & 0xFF);
        return averageGray;
    }

    /*public static int betterGray(int argb){
        RGB
        return (int) Math.round(0.299)
    }*/

    private static int toGrayscale(int rgb){
        int grayRGB = 0;
        return grayRGB;
    }

    public static V_RAM grayscale(V_RAM vRam){
        V_RAM grayImage = new V_RAM(vRam.getWidth(),vRam.getHeight());
        for (int y = 0; y < grayImage.getHeight(); y++) {
            for (int x = 0; x < grayImage.getWidth(); x++) {
                grayImage.setPixelsInt(x, y, averageGray(vRam.getPixel(x, y)));
            }
        }
        return grayImage;
    }
}
