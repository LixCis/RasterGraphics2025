package cz.osu.tasks;

import cz.osu.exercises.Cv05_LinesDrawing;
import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;

import java.awt.*;

// KU2 - Kreslení spline pomocí kubických Bézierových křivek

public class KU2 {
	private final MainWindow mainWindow;

	public KU2(MainWindow mainWindow){
		this.mainWindow = mainWindow;
	}

	public void run() {
		V_RAM vRam = new V_RAM(800, 600);

		for (int y = 0; y < vRam.getHeight(); y++) {
			for (int x = 0; x < vRam.getWidth(); x++) {
				vRam.setPixel(x, y, 255, 255, 255);
			}
		}

		// Příklad 1: Jednoduchá vlnka
		Point[] points1 = {
			new Point(100, 300),
			new Point(200, 200),
			new Point(300, 400),
			new Point(400, 200),
			new Point(500, 300)
		};

		drawSpline(vRam, points1, Color.BLUE);
		mainWindow.showImage(vRam.getImage());
		delay(2000);

		// Příklad 2: Tvar mraku
		Point[] points2 = {
			new Point(150, 350),
			new Point(120, 320),
			new Point(130, 280),
			new Point(160, 250),
			new Point(200, 240),
			new Point(240, 250),
			new Point(270, 230),
			new Point(310, 220),
			new Point(350, 230),
			new Point(380, 250),
			new Point(420, 240),
			new Point(460, 250),
			new Point(490, 280),
			new Point(500, 320),
			new Point(470, 350),
			new Point(400, 360),
			new Point(320, 360),
			new Point(240, 360),
			new Point(150, 350)
		};

		for (int y = 0; y < vRam.getHeight(); y++) {
			for (int x = 0; x < vRam.getWidth(); x++) {
				vRam.setPixel(x, y, 255, 255, 255);
			}
		}

		drawSpline(vRam, points2, Color.RED);

		for (Point p : points2) {
			drawPoint(vRam, p.x, p.y, Color.BLACK);
		}

		mainWindow.showImage(vRam.getImage());
		delay(15000);
	}

	// Vykreslení spline: vstup P1, P2, ..., Pn (n >= 3)
    private void drawSpline(V_RAM vRam, Point[] points, Color color) {
        int n = points.length;

		if (n < 3) {
			throw new IllegalArgumentException("At least 3 points are required for a spline");
		}

		// Rozšíření o fantomové body
		Point[] extendedPoints = new Point[n + 2];
		extendedPoints[0] = points[0];
		System.arraycopy(points, 0, extendedPoints, 1, n);
		extendedPoints[n + 1] = points[n - 1];

		// Výpočet pomocných kontrolních bodů L a R
		Point[] L = new Point[n + 2];
		Point[] R = new Point[n + 2];

		for (int i = 1; i <= n; i++) {
			// Li = Pi - (P(i+1) - P(i-1)) / 6
			double lx = extendedPoints[i].x - (extendedPoints[i + 1].x - extendedPoints[i - 1].x) / 6.0;
			double ly = extendedPoints[i].y - (extendedPoints[i + 1].y - extendedPoints[i - 1].y) / 6.0;
			L[i] = new Point((int) Math.round(lx), (int) Math.round(ly));

			double rx = extendedPoints[i].x + (extendedPoints[i + 1].x - extendedPoints[i - 1].x) / 6.0;
			double ry = extendedPoints[i].y + (extendedPoints[i + 1].y - extendedPoints[i - 1].y) / 6.0;
			R[i] = new Point((int) Math.round(rx), (int) Math.round(ry));
		}

		// Vykreslení segmentů
		double step = 0.01;

		for (int i = 1; i < n; i++) {
			Point cp0 = extendedPoints[i];
			Point cp1 = R[i];
			Point cp2 = L[i + 1];
			Point cp3 = extendedPoints[i + 1];

			// Koeficienty Bézierovy křivky
			double qx0 = cp0.x;
			double qy0 = cp0.y;

			double qx1 = 3 * (cp1.x - cp0.x);
			double qy1 = 3 * (cp1.y - cp0.y);

			double qx2 = 3 * (cp0.x - 2 * cp1.x + cp2.x);
			double qy2 = 3 * (cp0.y - 2 * cp1.y + cp2.y);

			double qx3 = cp3.x - 3 * cp2.x + 3 * cp1.x - cp0.x;
			double qy3 = cp3.y - 3 * cp2.y + 3 * cp1.y - cp0.y;

			Point prevPoint = null;

			for (double t = 0.0; t <= 1.0; t += step) {
				double t2 = t * t;
				double t3 = t2 * t;

				double x = qx0 + qx1 * t + qx2 * t2 + qx3 * t3;
				double y = qy0 + qy1 * t + qy2 * t2 + qy3 * t3;

				Point currentPoint = new Point((int) Math.round(x), (int) Math.round(y));

				if (prevPoint != null) {
					Cv05_LinesDrawing.drawLine(vRam, prevPoint.x, prevPoint.y,
							currentPoint.x, currentPoint.y, color);
				}

				prevPoint = currentPoint;
			}

			// Závěrečný bod
			Point finalPoint = new Point((int) Math.round(qx0 + qx1 + qx2 + qx3),
					(int) Math.round(qy0 + qy1 + qy2 + qy3));
			if (prevPoint != null) {
				Cv05_LinesDrawing.drawLine(vRam, prevPoint.x, prevPoint.y,
						finalPoint.x, finalPoint.y, color);
			}
		}
	}

	private void drawPoint(V_RAM vRam, int x, int y, Color color) {
		int size = 3;
		for (int dy = -size; dy <= size; dy++) {
			for (int dx = -size; dx <= size; dx++) {
				int px = x + dx;
				int py = y + dy;
				if (px >= 0 && px < vRam.getWidth() && py >= 0 && py < vRam.getHeight()) {
					vRam.setPixel(px, py, color.getRed(), color.getGreen(), color.getBlue());
				}
			}
		}
	}

	private void delay(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}
}
