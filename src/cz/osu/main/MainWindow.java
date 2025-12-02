package cz.osu.main;

import cz.osu.exercises.Cv01_RGB;
import cz.osu.exercises.Matrix2D;
import cz.osu.exercises.Point2D;
import cz.osu.tasks.KU1_TWO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JPanel{

    private ImagePanel imagePanel;

    public MainWindow(){

        initialize();

        V_RAM vRam = new V_RAM(200, 200);

        Point2D p1 = new Point2D(15.6, 10.3);
        Point p1i = p1.getPoint();
        vRam.setPixel(p1i.x, p1i.y, 255, 255, 255);

        Matrix2D t1 = Matrix2D.translate(6, 11);
        Point2D p2 = t1.multiply(p1);
        Point p2i = p2.getPoint();
        vRam.setPixel(p2i.x, p2i.y, 0, 255, 0);

        Matrix2D t2 = Matrix2D.rotate(10);
        Point2D p3 = t2.multiply(p2);
        Point p3i = p3.getPoint();
        vRam.setPixel(p3i.x, p3i.y, 0, 0, 255);

        Matrix2D total = t2.multiply(t1);
        Point2D p4 = total.multiply(p1);
        Point p4i = p4.getPoint();
        vRam.setPixel(p4i.x, p4i.y, 0, 255, 255);

        imagePanel.setImage(vRam.getImage());

        // Okno je připraveno pro zobrazení obrázků
    }

    private void initialize(){

        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        imagePanel = new ImagePanel();
        imagePanel.setBounds(10,60, 970, 600);
        this.add(imagePanel);

        //open image
        JButton button = new JButton();
        button.setBounds(150,10,120,30);
        button.setText("Load Image");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                openImage();
            }
        });
        this.add(button);

        //save image as PNG
        JButton button4 = new JButton();
        button4.setBounds(10,10,120,30);
        button4.setText("Save as PNG");
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageAsPNG();
            }
        });
        this.add(button4);

        JFrame frame = new JFrame("Raster Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setSize(1004, 705);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void openImage(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Documents/.OSU/3s ZS/");
        fc.setDialogTitle("Load Image");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            try {

                BufferedImage temp = ImageIO.read(file);

                if(temp != null){
                    //TODO THIS IS THE ENTRY POINT

                    imagePanel.setImage(temp);

                }else {

                    JOptionPane.showMessageDialog(null, "Unable to load image", "Open image: ", JOptionPane.ERROR_MESSAGE);
                }

            }catch (IOException e){

                e.printStackTrace();
            }
        }
    }

    public V_RAM loadImageAsVRAM(){
        BufferedImage image = loadImage();
        showImage(image);
        return convertBufferedToVRAM(image);
    }

    private BufferedImage loadImage(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Documents/.OSU/3s ZS/GALP");
        fc.setDialogTitle("Load Image");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            try {

                return ImageIO.read(file);

            }catch (IOException e){

                e.printStackTrace();
            }
        }
        return null;
    }

    private V_RAM convertBufferedToVRAM(BufferedImage image){
        if (image==null) return new V_RAM(0,0);

        V_RAM vRam = new V_RAM(image.getWidth(), image.getHeight());
        //System.out.println(); //debug
        for (int y = 0; y < vRam.getHeight(); y++) {
            for (int x = 0; x < vRam.getWidth(); x++) {
                vRam.setPixelsInt(x, y, image.getRGB(x, y));
                //int[] rgb = vRam.getPixelRGB(x, y); //2 lines debug code prints values of image
                //System.out.print(" | " + rgb[1] + ":" + rgb[2] + ":" + rgb[3]);
            }
            //System.out.print(" |\n"); //debug
        }
        //System.out.println(); //debug
        return vRam;
    }

    public void showImage(BufferedImage bufferedImage){
        if(bufferedImage != null){

            imagePanel.setImage(bufferedImage);

        }else {

            JOptionPane.showMessageDialog(null, "Unable to load image", "Open image: ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveImageAsPNG(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Save Image as PNG");

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            String fname = file.getAbsolutePath();

            if(!fname.endsWith(".png") ) file = new File(fname + ".png");

            try {

                ImageIO.write(imagePanel.getImage(), "png", file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void delay(long millis){
        if (millis < 0) {
            System.out.println("Cannot wait for negative or 0 time.");
            return;
        }
        if (millis == 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e){
            System.out.println("Thread interrupted.");
            e.printStackTrace();
        }
    }

    private static void delaySeconds(double seconds){
        delay((long)(seconds * 1000d));
    }

    private static void confirm(){

    }

    public void runCv01(MainWindow mainWindow){
        Cv01_RGB cv01Rgb = new Cv01_RGB();
        double delay = 0.7;
        mainWindow.imagePanel.setImage(cv01Rgb.solidWithBorder().getImage());
        delaySeconds(delay);
        mainWindow.imagePanel.setImage(cv01Rgb.redGreenGradientWithBlue().getImage());
        delaySeconds(delay);
        mainWindow.imagePanel.setImage(cv01Rgb.greenBlueGradient().getImage());
        delaySeconds(delay);
        mainWindow.imagePanel.setImage(cv01Rgb.redGreenBlueInMiddle().getImage());
    }

    public static void main(String[] args) {

        MainWindow window = new MainWindow();

        // Spuštění úlohy KU1_TWO - kontextové zpracování šedotónového obrazu
        //KU1_TWO ku1TWO = new KU1_TWO(window);
        //ku1TWO.run();

        // Spuštění úlohy KU1_ONE - odstranění červených očí
        //cz.osu.tasks.KU1_ONE ku1 = new cz.osu.tasks.KU1_ONE(window);
        //ku1.run();


        /*V_RAM grayVram = Cv02_Images.grayscale(image);
        window.showImage(image.getImage());
        delaySeconds(0.5);
        window.showImage(grayVram.getImage());
        delaySeconds(0.5);*/

        /*V_RAM blurred = new V_RAM(image.getImage());
        Cv03_Convolution.convolution(blurred, Kernel.simpleBlurKernel(10));
        window.showImage(blurred.getImage());*/

        //delaySeconds(1);
        /*hueShiftedImage = Cv02_Images.shiftHue(image,50);
        window.showImage(hueShiftedImage.getImage());
        delaySeconds(1);
        hueShiftedImage = Cv02_Images.shiftHue(image,180);
        window.showImage(hueShiftedImage.getImage());
        delaySeconds(1);
        hueShiftedImage = Cv02_Images.shiftHue(image,-180);
        window.showImage(hueShiftedImage.getImage());
        delaySeconds(1);
        hueShiftedImage = Cv02_Images.shiftHue(image,1000);
        window.showImage(hueShiftedImage.getImage());
        delaySeconds(1);
        hueShiftedImage = Cv02_Images.shiftHue(image,-2000);
        window.showImage(hueShiftedImage.getImage());*/
    }

    /*
    private static V_RAM gridMerge(V_RAM[] vRams){

    }*/

}
