package cz.osu.main;

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

    public V_RAM(BufferedImage sourceImage){
        width = sourceImage.getWidth();
        height = sourceImage.getHeight();
        rawData = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rawData[y][x] = sourceImage.getRGB(x, y);
            }
        }
    }

    public V_RAM(V_RAM[] vRams){
        //check number of VRams
        int numOfVRams = vRams.length;
        //calculate good layout based on squares 4:3
        //check VRams' dimensions
        //calculate final VRam image size
        //merge VRam images into final VRam image
        //write final VRam image to rawData
    }

    public V_RAM getCopy(){
        V_RAM copy = new V_RAM(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                copy.rawData[y][x] = this.rawData[y][x];
            }
        }
        return copy;
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

    public int[] getPixelsComponents(int x, int y){
        int rgb = rawData[y][x];
        int blue = rgb & 0b11111111; // FF == 1111 1111
        int green = (rgb >> 8) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        //int alpha = (rgb >> 24) & 0xFF;
        return new int[]{255, red, green, blue};
    }

    public void setPixel(int x, int y, int red, int green, int blue){

        rawData[y][x] = 255 << 24 | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    public void setPixelsInt(int x, int y, int value){
        rawData[y][x] = value;
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
