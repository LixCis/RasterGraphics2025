package cz.osu.main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JPanel{

    private ImagePanel imagePanel;

    private V_RAM vram;

    public MainWindow(){

        initialize();
        
        vram = new V_RAM(100, 300);
        int borderSize = 1;

        for (int w = 0; w < vram.getWidth(); w++) {
            for (int h = 0; h < vram.getHeight(); h++) {
                vram.setPixel(w, h, 255, 255, 0);
                if (w < borderSize || w >= vram.getWidth() - borderSize ||
                        h < borderSize || h >= vram.getHeight() - borderSize) {
                    vram.setPixel(w, h, 0, 255, 0);
                }
            }
        }

        imagePanel.setImage(vram.getImage());
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
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Load Image");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            try {

                BufferedImage temp = ImageIO.read(file);

                if(temp != null){

                    imagePanel.setImage(temp);

                }else {

                    JOptionPane.showMessageDialog(null, "Unable to load image", "Open image: ", JOptionPane.ERROR_MESSAGE);
                }

            }catch (IOException e){

                e.printStackTrace();
            }
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

    public static void main(String[] args) {

        new MainWindow();
    }
}
