package cz.osu.exercises;

import cz.osu.main.V_RAM;
import cz.osu.utils.Kernel;
import cz.osu.utils.RGB;

public class Cv03_Convolution {

    public static void convolution(V_RAM vRam, Kernel kernel){

        V_RAM copy = vRam.getCopy();

        /*int[][] kernel = new int[3][3];
        kernel[0][0] = 1;
        kernel[0][1] = 1;
        kernel[0][2] = 1;

        kernel[1][0] = 1;
        kernel[1][1] = 1;
        kernel[1][2] = 1;

        kernel[2][0] = 1;
        kernel[2][1] = 1;
        kernel[2][2] = 1;*/

        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {

                //kernel
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                for (int yK = 0; yK < kernel.height; yK++) {
                    int yTemp = Math.clamp(y + yK - kernel.height / 2, 0, vRam.getHeight()-1);
                    for (int xK = 0; xK < kernel.width; xK++) {

                        int xTemp = Math.clamp(x + xK - kernel.width / 2, 0, vRam.getWidth()-1);

                        RGB pixel = new RGB(copy.getPixel(xTemp, yTemp));

                        sumR += kernel.pattern[yK][xK] * pixel.R;
                        sumG += kernel.pattern[yK][xK] * pixel.G;
                        sumB += kernel.pattern[yK][xK] * pixel.B;
                    }
                }

                sumR /= Math.clamp(kernel.divisor, 0, 255);
                sumG /= Math.clamp(kernel.divisor, 0, 255);
                sumB /= Math.clamp(kernel.divisor, 0, 255);

                vRam.setPixel(x, y, sumR, sumG, sumB);
            }
        }
    }
}
