package cz.osu.exercises;

import cz.osu.main.V_RAM;

public class Cv01_RGB {
    public V_RAM redGreenGradientWithBlue(){
        V_RAM vRam = new V_RAM(256, 256);
        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                vRam.setPixel(x, y, x, y,128);
            }
        }
        return vRam;
    }

    public V_RAM greenBlueGradient(){
        V_RAM vRam = new V_RAM(256, 256);
        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                vRam.setPixel(x, y, 0, x,y);
            }
        }
        return vRam;
    }

    public V_RAM solidWithBorder(){
        //create image
        V_RAM vRam = new V_RAM(100, 100);
        //fill with color and set border
        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                vRam.setPixel(x, y, 255, 255, 0);
                if (y == 0 || y == vRam.getHeight()-1 || x == 0 || x == vRam.getWidth()-1) vRam.setPixel(x, y, 0, 255, 0);
            }
        }
        return vRam;
    }

    public V_RAM redGreenBlueInMiddle(){ // UNFINISHED
        V_RAM vRam = new V_RAM(256,256);
        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                int blue_x = x<=127 ? x : 255-x;
                int blue_y = y<=127 ? y : 255-y;
                int blue = blue_x + blue_y;
                vRam.setPixel(x, y, x, y, blue);
            }
        }
        return vRam;
    }

    /*
    1 Konstrukce RGB modelu (čtverec o straně 256 - X:Red, Y:Green, Blue - Konstanta)
    2 Práce s obrazem - Grayscale, Saturace, Hue
3 Konvoluce
4 Komprese obrazu
5 Vykreslení úsečky - Native, DDA, Bresenham
6 Křivky a plochy - Bezier
7 Tuhé transformace 2D
8 Vektorová grafika - Bod, Troúhelník
9 Vyplňování trojuhelníku
10 Tuhé transformace 3D
11 Kulové modely s viditelností a osvětlením, (skalární a vektorový součin, normálový vektor)
     */
}
