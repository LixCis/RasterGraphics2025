package cz.osu.utils;

public class Kernel {
    public int width;
    public int height;
    public int[][] pattern;
    public int divisor;

    private Kernel(int width, int height) {
        this.width = width;
        this.height = height;
        pattern = new int[height][width];
    }

    public static Kernel simpleBlurKernel(int size){
        Kernel kernel1 = new Kernel(size, size);
        for (int y = 0; y < kernel1.height; y++) {
            for (int x = 0; x < kernel1.width; x++) {
                kernel1.pattern[y][x] = 1;
            }
        }
        kernel1.calculateDivisor();
        return kernel1;
    }
    private void calculateDivisor(){
        int sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pattern[y][x] == 0) continue; //TODO Does this line bring any benefit??
                sum += pattern[y][x];
            }
        }
        if (sum == 0) sum = 1; //TODO Is this correct??
        divisor = sum;
    }
}
