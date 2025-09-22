package cz.osu.main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class V_RAM {

    private int width;
    private int height;
    private int[][] rawData;

    public V_RAM(int width, int height){

        this.width = width;
        this.height = height;
        rawData = new int[height][width];
    }

    public int getWidth(){

        return width;
    }

    public int getHeight(){

        return height;
    }

    public int getPixel(int x, int y){

        return rawData[y][x];
    }

    public void setPixel(int x, int y, int red, int green, int blue){

        rawData[y][x] = 255 << 24 | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    public BufferedImage getImage(){

        int[] rgbArray = new int[width * height];
        int counter = 0;

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){

                rgbArray[counter++] = rawData[y][x];
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, rgbArray, 0, width);

        return image;
    }
}
