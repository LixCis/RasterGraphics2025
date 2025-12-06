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
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JPanel{

	private ImagePanel imagePanel;
	private List<RunnableExercise> exercises;
	private List<RunnableExercise> tasks;

	// Tracking běžících úloh
	private Thread currentExerciseThread;
	private RunnableExercise currentExercise;
	private JLabel statusLabel;

	// Sdílená instance pro KU2_EXT1 wrapper (kvůli onInterrupt)
	private final Tasks.KU2_EXT1 ku2ext1Wrapper = new Tasks.KU2_EXT1();

    public MainWindow(){
        exercises = new ArrayList<>();
        tasks = new ArrayList<>();

        initialize();
        registerExercisesAndTasks();

        // Spustit default úlohu pokud existuje
        SwingUtilities.invokeLater(this::runDefaultExercise);
    }

    private void runDefaultExercise() {
        // Hledat default úlohu v exercises
        for (RunnableExercise exercise : exercises) {
            if (exercise.isDefault()) {
                runExercise(exercise);
                return;
            }
        }

        // Pokud není v exercises, hledat v tasks
        for (RunnableExercise task : tasks) {
            if (task.isDefault()) {
                runExercise(task);
                return;
            }
        }
    }

    private void initialize(){

        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        imagePanel = new ImagePanel();
        imagePanel.setBounds(10,60, 970, 600);
        this.add(imagePanel);

        // Status label pro zobrazení běžící úlohy
        statusLabel = new JLabel("Žádná úloha neběží");
        statusLabel.setBounds(290, 10, 400, 30);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(60, 60, 60));
        this.add(statusLabel);

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
        frame.setJMenuBar(createMenuBar());
        frame.getContentPane().add(this);
        frame.setSize(1004, 705);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu exercisesMenu = new JMenu("Exercises");
        JMenu tasksMenu = new JMenu("Tasks");

        menuBar.add(exercisesMenu);
        menuBar.add(tasksMenu);

        return menuBar;
    }

	private void registerExercisesAndTasks() {
		// Registrace všech exercises
		addExercise(new Exercises.CV01_RGB());
		addExercise(new Exercises.CV02_Images());
		addExercise(new Exercises.CV03_Convolution());
		addExercise(new Exercises.CV04_Compression());
		addExercise(new Exercises.CV05_LinesDrawing());
		addExercise(new Exercises.CV06_Curves());
		addExercise(new Exercises.CV07_AffineTransformations2D());
		addExercise(new Exercises.Test01());

		// Registrace všech tasks
		addTask(new Tasks.KU1_ONE());
		addTask(new Tasks.KU1_TWO());
		addTask(new Tasks.KU2());
		addTask(ku2ext1Wrapper); // Použít sdílenou instanci
		addTask(new Tasks.KU2_EXT2());
		addTask(new Tasks.KU3());
	}

    private void populateMenu(JMenu menu, List<RunnableExercise> items) {
        menu.removeAll();

        for (RunnableExercise item : items) {
            JMenuItem menuItem = new JMenuItem(item.getDisplayName());
            menuItem.addActionListener(e -> runExercise(item));
            menu.add(menuItem);
        }

        if (items.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("(žádné úlohy)");
            emptyItem.setEnabled(false);
            menu.add(emptyItem);
        }
    }

    private void runExercise(RunnableExercise exercise) {
        // Ukončit předchozí úlohu pokud běží
        stopCurrentExercise();

        // Aktualizovat status
        statusLabel.setText("Běží: " + exercise.getDisplayName());
        statusLabel.setForeground(new Color(0, 128, 0));

        // Spustit novou úlohu v samostatném vlákně
        currentExercise = exercise;
        currentExerciseThread = new Thread(() -> {
            try {
                exercise.execute(this);
            } catch (Exception e) {
                if (!(e instanceof InterruptedException)) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "Chyba při běhu úlohy: " + e.getMessage(),
                            "Chyba",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            } finally {
                SwingUtilities.invokeLater(() -> {
                    if (currentExercise == exercise) {
                        statusLabel.setText("Dokončeno: " + exercise.getDisplayName());
                        statusLabel.setForeground(new Color(60, 60, 60));
                        currentExercise = null;
                        currentExerciseThread = null;
                    }
                });
            }
        });
        currentExerciseThread.start();
    }

    private void stopCurrentExercise() {
        if (currentExerciseThread != null && currentExerciseThread.isAlive()) {
            System.out.println("MainWindow.stopCurrentExercise() - Ukončuji předchozí úlohu: " + currentExercise.getDisplayName());
            System.out.println("MainWindow.stopCurrentExercise() - currentExercise instance = " + currentExercise);

            // Zavolat onInterrupt callback
            if (currentExercise != null) {
                System.out.println("MainWindow.stopCurrentExercise() - volám currentExercise.onInterrupt()");
                currentExercise.onInterrupt();
            }

            // Přerušit thread
            currentExerciseThread.interrupt();

            try {
                // Počkat max 500ms na ukončení
                currentExerciseThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            currentExercise = null;
            currentExerciseThread = null;
        } else {
            System.out.println("MainWindow.stopCurrentExercise() - žádná úloha neběží nebo není alive");
        }
    }

    public void addExercise(RunnableExercise exercise) {
        exercises.add(exercise);
        updateMenus();
    }

    public void addTask(RunnableExercise task) {
        tasks.add(task);
        updateMenus();
    }

    private void updateMenus() {
        JMenuBar menuBar = ((JFrame) SwingUtilities.getWindowAncestor(this)).getJMenuBar();
        if (menuBar != null && menuBar.getMenuCount() >= 2) {
            populateMenu(menuBar.getMenu(0), exercises);
            populateMenu(menuBar.getMenu(1), tasks);
        }
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

    /**
     * Thread-safe metoda pro načtení obrázku jako V_RAM
     * Lze volat z exercise threadu
     */
    public V_RAM loadImageAsVRAM(){
        BufferedImage image = loadImage();
        if (image != null) {
            showImage(image);
        }
        return convertBufferedToVRAM(image);
    }

    /**
     * Thread-safe metoda pro načtení obrázku
     * Lze volat z exercise threadu
     */
    public BufferedImage loadImage(){
        if (SwingUtilities.isEventDispatchThread()) {
            return loadImageInternal();
        } else {
            final BufferedImage[] result = new BufferedImage[1];
            try {
                SwingUtilities.invokeAndWait(() -> result[0] = loadImageInternal());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result[0];
        }
    }

    private BufferedImage loadImageInternal(){
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
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);  // Propagovat přerušení
        }
    }

    private static void delaySeconds(double seconds){
        delay((long)(seconds * 1000d));
    }

    private static void confirm(){

    }

    public ImagePanel getImagePanel() {
        return imagePanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
        });
    }
}
