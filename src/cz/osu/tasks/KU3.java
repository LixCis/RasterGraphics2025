package cz.osu.tasks;

import cz.osu.main.MainWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class KU3 extends SwingWorker<Void, BufferedImage> {

    private final MainWindow mainWindow;
    private BufferedImage dial, hourHand, minuteHand, secondHand;
    private int canvasSize;

    private static final String DIAL_PATH = "src/cz/osu/images/hodiny/cifernikB.png";
    private static final String HOUR_HAND_PATH = "src/cz/osu/images/hodiny/hodinovka.png";
    private static final String MINUTE_HAND_PATH = "src/cz/osu/images/hodiny/minutovka.png";
    private static final String SECOND_HAND_PATH = "src/cz/osu/images/hodiny/sekundovka.png";

    public KU3(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    protected Void doInBackground() throws Exception {
        dial = ImageIO.read(new File(DIAL_PATH));
        hourHand = ImageIO.read(new File(HOUR_HAND_PATH));
        minuteHand = ImageIO.read(new File(MINUTE_HAND_PATH));
        secondHand = ImageIO.read(new File(SECOND_HAND_PATH));

        // Výpočet velikosti plátna
        double maxHandDiagonal = 0;
        BufferedImage[] hands = {hourHand, minuteHand, secondHand};
        for (BufferedImage hand : hands) {
            double diagonal = Math.sqrt(hand.getWidth() * hand.getWidth() + hand.getHeight() * hand.getHeight());
            if (diagonal > maxHandDiagonal) {
                maxHandDiagonal = diagonal;
            }
        }
        canvasSize = (int) Math.ceil(Math.max(maxHandDiagonal, Math.max(dial.getWidth(), dial.getHeight())));

        double startSecondsTotal = 8 * 3600;
        double endSecondsTotal = (8 * 3600) + (18 * 60) + 35;

        double animationDuration = 8.0;
        int fps = 30;
        int totalFrames = (int) (animationDuration * fps);
        long frameDelay = 1000 / fps;

        for (int i = 0; i <= totalFrames; i++) {
            if (isCancelled()) {
                break;
            }

            double progress = (double) i / totalFrames;
            double currentSecondsTotal = startSecondsTotal + progress * (endSecondsTotal - startSecondsTotal);

            int hours = (int) (currentSecondsTotal / 3600) % 12;
            int minutes = (int) ((currentSecondsTotal % 3600) / 60);
            double seconds = currentSecondsTotal % 60;

            BufferedImage frame = createClockFrame(hours, minutes, seconds);
            publish(frame);

            try {
                Thread.sleep(frameDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return null;
    }

    @Override
    protected void process(List<BufferedImage> chunks) {
        if (!chunks.isEmpty()) {
            mainWindow.showImage(chunks.get(chunks.size() - 1));
        }
    }

    @Override
    protected void done() {
        try {
            if (!isCancelled()) {
                BufferedImage finalFrame = createClockFrame(8, 18, 35);
                mainWindow.showImage(finalFrame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage createClockFrame(int hours, int minutes, double seconds) {
        double secondAngle = seconds * 6;
        double minuteAngle = (minutes + seconds / 60.0) * 6;
        double hourAngle = (hours % 12 + minutes / 60.0 + seconds / 3600.0) * 30;

        BufferedImage rotatedHourHand = rotateImage(hourHand, hourAngle);
        BufferedImage rotatedMinuteHand = rotateImage(minuteHand, minuteAngle);
        BufferedImage rotatedSecondHand = rotateImage(secondHand, secondAngle);

        BufferedImage frame = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(dial, (canvasSize - dial.getWidth()) / 2, (canvasSize - dial.getHeight()) / 2, null);
        int centerX = canvasSize / 2;
        int centerY = canvasSize / 2;
        g.drawImage(rotatedHourHand, centerX - rotatedHourHand.getWidth() / 2, centerY - rotatedHourHand.getHeight() / 2, null);
        g.drawImage(rotatedMinuteHand, centerX - rotatedMinuteHand.getWidth() / 2, centerY - rotatedMinuteHand.getHeight() / 2, null);
        g.drawImage(rotatedSecondHand, centerX - rotatedSecondHand.getWidth() / 2, centerY - rotatedSecondHand.getHeight() / 2, null);
        g.dispose();

        return frame;
    }

    private BufferedImage rotateImage(BufferedImage image, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int w = image.getWidth();
        int h = image.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);
        g2d.rotate(rads, w / 2.0, h / 2.0);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotated;
    }
}
