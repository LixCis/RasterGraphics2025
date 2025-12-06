package cz.osu.tasks;

import cz.osu.exercises.Cv05_LinesDrawing;
import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * KU2_EXT2 - Animace přechodu mezi dvěma Bézierovými křivkami
 */
public class KU2_EXT2 {
	private final MainWindow mainWindow;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	static class CurvePoint {
		Point main;
		Point leftHandle;
		Point rightHandle;

		CurvePoint(int x, int y, int lx, int ly, int rx, int ry) {
			this.main = new Point(x, y);
			this.leftHandle = new Point(lx, ly);
			this.rightHandle = new Point(rx, ry);
		}

		CurvePoint(Point main, Point leftHandle, Point rightHandle) {
			this.main = main;
			this.leftHandle = leftHandle;
			this.rightHandle = rightHandle;
		}

		static CurvePoint interpolate(CurvePoint p1, CurvePoint p2, double t) {
			return new CurvePoint(
				interpolatePoint(p1.main, p2.main, t),
				interpolatePoint(p1.leftHandle, p2.leftHandle, t),
				interpolatePoint(p1.rightHandle, p2.rightHandle, t)
			);
		}

		private static Point interpolatePoint(Point p1, Point p2, double t) {
			int x = (int) Math.round(p1.x * (1 - t) + p2.x * t);
			int y = (int) Math.round(p1.y * (1 - t) + p2.y * t);
			return new Point(x, y);
		}
	}

	public KU2_EXT2(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public void run() {
		List<CurvePoint> wave1 = createWave1();
		List<CurvePoint> wave2 = createWave2();

		if (wave1.size() != wave2.size()) {
			throw new IllegalStateException("Obě křivky musí mít stejný počet kontrolních bodů!");
		}

		int cycles = 3;
		int stepsPerCycle = 100;
		int frameDelay = 30;
		int pauseDelay = 1000;

		for (int cycle = 0; cycle < cycles; cycle++) {
			// Animace tam: 0 -> 1
			for (int step = 0; step <= stepsPerCycle; step++) {
				double t = (double) step / stepsPerCycle;
				double smoothT = smoothstep(t);

				List<CurvePoint> interpolated = interpolateCurves(wave1, wave2, smoothT);
				drawFrame(interpolated, smoothT);

				if (t == 0.0 || Math.abs(t - 0.5) < 0.01 || t == 1.0) {
					delay(pauseDelay);
				} else {
					delay(frameDelay);
				}
			}

			// Animace zpět: 1 -> 0
			for (int step = stepsPerCycle; step >= 0; step--) {
				double t = (double) step / stepsPerCycle;
				double smoothT = smoothstep(t);

				List<CurvePoint> interpolated = interpolateCurves(wave1, wave2, smoothT);
				drawFrame(interpolated, smoothT);

				if (t == 1.0 || Math.abs(t - 0.5) < 0.01 || t == 0.0) {
					delay(pauseDelay);
				} else {
					delay(frameDelay);
				}
			}
		}

		delay(2000);
	}

	// Smooth step: 3t^2 - 2t^3
	private double smoothstep(double t) {
		return t * t * (3 - 2 * t);
	}

	private List<CurvePoint> createWave1() {
		List<CurvePoint> points = new ArrayList<>();
		points.add(new CurvePoint(100, 300, 100, 300, 150, 250));
		points.add(new CurvePoint(250, 200, 200, 250, 300, 150));
		points.add(new CurvePoint(400, 300, 350, 250, 450, 350));
		points.add(new CurvePoint(550, 400, 500, 350, 600, 450));
		points.add(new CurvePoint(700, 300, 650, 350, 700, 300));
		return points;
	}

	private List<CurvePoint> createWave2() {
		List<CurvePoint> points = new ArrayList<>();
		points.add(new CurvePoint(100, 300, 100, 300, 130, 250));
		points.add(new CurvePoint(200, 150, 170, 200, 230, 100));
		points.add(new CurvePoint(400, 450, 350, 500, 450, 400));
		points.add(new CurvePoint(600, 120, 570, 80, 630, 170));
		points.add(new CurvePoint(700, 300, 670, 250, 700, 300));
		return points;
	}

	private List<CurvePoint> interpolateCurves(List<CurvePoint> curve1, List<CurvePoint> curve2, double t) {
		List<CurvePoint> result = new ArrayList<>();
		for (int i = 0; i < curve1.size(); i++) {
			CurvePoint p1 = curve1.get(i);
			CurvePoint p2 = curve2.get(i);
			result.add(CurvePoint.interpolate(p1, p2, t));
		}

		return result;
	}

	private void drawFrame(List<CurvePoint> points, double t) {
		V_RAM vRam = new V_RAM(WIDTH, HEIGHT);
		clearBackground(vRam, Color.WHITE);

		Color curveColor = interpolateColor(new Color(50, 100, 255), new Color(255, 100, 50), t);
		drawSpline(vRam, points, curveColor, 3);
		drawProgressIndicator(vRam, t);

		mainWindow.showImage(vRam.getImage());
	}

	private Color interpolateColor(Color c1, Color c2, double t) {
		int r = (int) Math.round(c1.getRed() * (1 - t) + c2.getRed() * t);
		int g = (int) Math.round(c1.getGreen() * (1 - t) + c2.getGreen() * t);
		int b = (int) Math.round(c1.getBlue() * (1 - t) + c2.getBlue() * t);
		return new Color(r, g, b);
	}

	private void drawProgressIndicator(V_RAM vRam, double t) {
		int barWidth = 600;
		int barHeight = 20;
		int barX = (WIDTH - barWidth) / 2;
		int barY = HEIGHT - 50;

		// Pozadí
		for (int y = barY; y < barY + barHeight; y++) {
			for (int x = barX; x < barX + barWidth; x++) {
				vRam.setPixel(x, y, 200, 200, 200);
			}
		}

		// Vyplněná část
		int fillWidth = (int) (barWidth * t);
		for (int y = barY; y < barY + barHeight; y++) {
			for (int x = barX; x < barX + fillWidth; x++) {
				Color c = interpolateColor(new Color(50, 100, 255), new Color(255, 100, 50), t);
				vRam.setPixel(x, y, c.getRed(), c.getGreen(), c.getBlue());
			}
		}

		drawRectangle(vRam, barX, barY, barWidth, barHeight, Color.BLACK);
	}

	private void drawRectangle(V_RAM vRam, int x, int y, int width, int height, Color color) {
		for (int i = 0; i < width; i++) {
			vRam.setPixel(x + i, y, color.getRed(), color.getGreen(), color.getBlue());
			vRam.setPixel(x + i, y + height - 1, color.getRed(), color.getGreen(), color.getBlue());
		}
		for (int i = 0; i < height; i++) {
			vRam.setPixel(x, y + i, color.getRed(), color.getGreen(), color.getBlue());
			vRam.setPixel(x + width - 1, y + i, color.getRed(), color.getGreen(), color.getBlue());
		}
	}

	@SuppressWarnings("unused")
	private void drawControlPoints(V_RAM vRam, List<CurvePoint> points) {
		for (CurvePoint p : points) {
			Cv05_LinesDrawing.drawLine(vRam, p.main.x, p.main.y,
				p.leftHandle.x, p.leftHandle.y, new Color(150, 150, 255));
			Cv05_LinesDrawing.drawLine(vRam, p.main.x, p.main.y,
				p.rightHandle.x, p.rightHandle.y, new Color(150, 150, 255));

			drawPoint(vRam, p.leftHandle.x, p.leftHandle.y, new Color(100, 100, 255), 3);
			drawPoint(vRam, p.rightHandle.x, p.rightHandle.y, new Color(100, 100, 255), 3);
			drawPoint(vRam, p.main.x, p.main.y, Color.RED, 5);
		}
	}

	private void drawPoint(V_RAM vRam, int x, int y, Color color, int size) {
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

	// Vykreslení spline pomocí kubických Bézierových křivek
	private void drawSpline(V_RAM vRam, List<CurvePoint> points, Color color, int thickness) {
		int n = points.size();
		if (n < 2) return;

		double step = 0.01;

		for (int i = 0; i < n - 1; i++) {
			CurvePoint p0 = points.get(i);
			CurvePoint p1 = points.get(i + 1);

			Point cp0 = p0.main;
			Point cp1 = p0.rightHandle;
			Point cp2 = p1.leftHandle;
			Point cp3 = p1.main;

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
					drawThickLine(vRam, prevPoint.x, prevPoint.y,
						currentPoint.x, currentPoint.y, color, thickness);
				}

				prevPoint = currentPoint;
			}

			// Závěrečný bod segmentu
			Point finalPoint = new Point(
				(int) Math.round(qx0 + qx1 + qx2 + qx3),
				(int) Math.round(qy0 + qy1 + qy2 + qy3)
			);
			if (prevPoint != null) {
				drawThickLine(vRam, prevPoint.x, prevPoint.y,
					finalPoint.x, finalPoint.y, color, thickness);
			}
		}
	}

	private void drawThickLine(V_RAM vRam, int x1, int y1, int x2, int y2, Color color, int thickness) {
		int width = vRam.getWidth();
		int height = vRam.getHeight();

		for (int dy = -thickness/2; dy <= thickness/2; dy++) {
			for (int dx = -thickness/2; dx <= thickness/2; dx++) {
				int nx1 = x1 + dx;
				int ny1 = y1 + dy;
				int nx2 = x2 + dx;
				int ny2 = y2 + dy;

				if (nx1 >= 0 && nx1 < width && ny1 >= 0 && ny1 < height &&
					nx2 >= 0 && nx2 < width && ny2 >= 0 && ny2 < height) {
					Cv05_LinesDrawing.drawLine(vRam, nx1, ny1, nx2, ny2, color);
				}
			}
		}
	}

	private void clearBackground(V_RAM vRam, Color color) {
		for (int y = 0; y < vRam.getHeight(); y++) {
			for (int x = 0; x < vRam.getWidth(); x++) {
				vRam.setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue());
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
