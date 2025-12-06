package cz.osu.tasks;

import cz.osu.exercises.Cv05_LinesDrawing;
import cz.osu.main.MainWindow;
import cz.osu.main.V_RAM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * KU2_EXT1 - Editor písmenového fontu pomocí Bézierových křivek
 *
 * Interaktivní editor pro tvorbu písmen pomocí kubických Bézierových křivek.
 * Umožňuje úpravu kontrolních bodů a vodítek (handles) myší a ukládání/načítání z JSON.
 */
public class KU2_EXT1 {
	private final MainWindow mainWindow;
	private List<CurvePoint> controlPoints;
	private CurvePoint selectedPoint = null;
	private int selectedIndex = -1;
	private HandleType selectedHandle = HandleType.NONE;
	private static final int POINT_RADIUS = 6;
	private static final int HANDLE_RADIUS = 4;
	private static final String SAVE_FILE = "letter_S_points.json";
	private static final int CANVAS_WIDTH = 600;
	private static final int CANVAS_HEIGHT = 600;
	private boolean showHandles = true;
	private boolean closedCurve = false; // Uzavřená křivka (spojuje konec se začátkem)
	private JFrame editorFrame; // Reference na editor okno pro možnost zavření

	enum HandleType {
		NONE, MAIN, LEFT_HANDLE, RIGHT_HANDLE, BOTH_HANDLES
	}

	/**
	 * Bod s vodítky pro Bézierovu křivku
	 */
	class CurvePoint {
		Point main;        // Hlavní bod
		Point leftHandle;  // Levé vodítko (L)
		Point rightHandle; // Pravé vodítko (R)

		CurvePoint(int x, int y) {
			this.main = new Point(x, y);
			// Výchozí vodítka - automaticky vypočítaná
			this.leftHandle = new Point(x - 40, y);
			this.rightHandle = new Point(x + 40, y);
		}

		CurvePoint(Point main, Point leftHandle, Point rightHandle) {
			this.main = main;
			this.leftHandle = leftHandle;
			this.rightHandle = rightHandle;
		}

		/**
		 * Resetování handleru na výchozí pozici
		 */
		void resetHandles() {
			this.leftHandle = new Point(main.x, main.y);
			this.rightHandle = new Point(main.x, main.y);
		}

		// Automatický výpočet vodítek na základě sousedních bodů
		void autoCalculateHandles(CurvePoint prev, CurvePoint next) {
			if (prev == null || next == null) return;

			double dx = (next.main.x - prev.main.x) / 6.0;
			double dy = (next.main.y - prev.main.y) / 6.0;

			leftHandle = new Point(
				(int) Math.round(main.x - dx),
				(int) Math.round(main.y - dy)
			);
			rightHandle = new Point(
				(int) Math.round(main.x + dx),
				(int) Math.round(main.y + dy)
			);
		}
	}

	public KU2_EXT1(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.controlPoints = new ArrayList<>();
		loadPoints();
	}

	public void run() {
		System.out.println("KU2_EXT1.run() - START");

		// Pokud nejsou body, vytvoř výchozí
		if (controlPoints.isEmpty()) {
			createDefaultSPoints();
		}

		// Vytvoř editor okno
		createEditorWindow();

		// Čekej, dokud není thread přerušen
		// Editor běží v samostatném okně, ale tento thread musí zůstat živý
		try {
			System.out.println("KU2_EXT1.run() - čekám na přerušení...");
			while (!Thread.currentThread().isInterrupted()) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			System.out.println("KU2_EXT1.run() - thread byl přerušen");
			Thread.currentThread().interrupt();
		} finally {
			System.out.println("KU2_EXT1.run() - END");
		}
	}

	/**
	 * Vytvoření výchozích bodů pro písmeno S
	 */
	private void createDefaultSPoints() {
		controlPoints.add(new CurvePoint(300, 150));
	}

	/**
	 * Přepočítání všech vodítek automaticky
	 */
	private void recalculateAllHandles() {
		int n = controlPoints.size();
		if (n < 2) return; // Potřebujeme alespoň 2 body

		if (n == 2) {
			// Pro 2 body vypočítej jednoduché vodítka
			CurvePoint p0 = controlPoints.get(0);
			CurvePoint p1 = controlPoints.get(1);

			double dx = (p1.main.x - p0.main.x) / 3.0;
			double dy = (p1.main.y - p0.main.y) / 3.0;

			p0.rightHandle = new Point(
				(int) Math.round(p0.main.x + dx),
				(int) Math.round(p0.main.y + dy)
			);
			p0.leftHandle = new Point(
				(int) Math.round(p0.main.x - dx),
				(int) Math.round(p0.main.y - dy)
			);

			p1.leftHandle = new Point(
				(int) Math.round(p1.main.x - dx),
				(int) Math.round(p1.main.y - dy)
			);
			p1.rightHandle = new Point(
				(int) Math.round(p1.main.x + dx),
				(int) Math.round(p1.main.y + dy)
			);
			return;
		}

		// Pro 3+ bodů použij fantomové body nebo uzavřenou křivku
		for (int i = 0; i < n; i++) {
			CurvePoint prev, curr, next;

			curr = controlPoints.get(i);

			if (closedCurve) {
				// Pro uzavřenou křivku použij modulo
				prev = controlPoints.get((i - 1 + n) % n);
				next = controlPoints.get((i + 1) % n);
			} else {
				// Pro otevřenou křivku použij fantomové body na koncích
				if (i == 0) {
					prev = controlPoints.get(0);
				} else {
					prev = controlPoints.get(i - 1);
				}

				if (i == n - 1) {
					next = controlPoints.get(n - 1);
				} else {
					next = controlPoints.get(i + 1);
				}
			}

			curr.autoCalculateHandles(prev, next);
		}
	}

	/**
	 * Načtení bodů z JSON souboru
	 */
	private void loadPoints() {
		File file = new File(SAVE_FILE);
		if (!file.exists()) {
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder json = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				json.append(line.trim());
			}

			String content = json.toString();

			// Najdi začátek pole points
			int pointsStart = content.indexOf("[");
			int pointsEnd = content.lastIndexOf("]");

			if (pointsStart == -1 || pointsEnd == -1) {
				throw new Exception("Neplatný formát JSON");
			}

			// Načtení příznaku closedCurve (pokud existuje)
			int closedCurvePos = content.indexOf("\"closedCurve\"");
			if (closedCurvePos != -1) {
				int colonPos = content.indexOf(":", closedCurvePos);
				int commaPos = content.indexOf(",", colonPos);
				if (colonPos != -1 && commaPos != -1) {
					String closedValue = content.substring(colonPos + 1, commaPos).trim();
					closedCurve = Boolean.parseBoolean(closedValue);
					System.out.println("Načten příznak closedCurve: " + closedCurve);
				}
			}

			String pointsArray = content.substring(pointsStart + 1, pointsEnd);
			controlPoints.clear();

			// Parsování jednotlivých objektů
			int depth = 0;
			StringBuilder currentObject = new StringBuilder();

			for (int i = 0; i < pointsArray.length(); i++) {
				char c = pointsArray.charAt(i);

				if (c == '{') {
					depth++;
					currentObject.setLength(0);
				} else if (c == '}') {
					depth--;
					if (depth == 0 && currentObject.length() > 0) {
						// Parsuj tento objekt
						parsePointObject(currentObject.toString());
					}
				} else if (depth > 0) {
					currentObject.append(c);
				}
			}

			System.out.println("Načteno " + controlPoints.size() + " bodů z " + SAVE_FILE);

			// Pokud body nemají vodítka, vypočítej je
			if (!controlPoints.isEmpty() && controlPoints.get(0).leftHandle == null) {
				recalculateAllHandles();
			}
		} catch (Exception e) {
			System.out.println("Chyba při načítání: " + e.getMessage());
			e.printStackTrace();
			controlPoints.clear();
		}
	}

	/**
	 * Parsování jednoho bodu z JSON objektu
	 */
	private void parsePointObject(String objStr) {
		try {
			Integer x = null, y = null;
			Integer lx = null, ly = null, rx = null, ry = null;

			// Rozděl na páry klíč:hodnota
			String[] pairs = objStr.split(",");

			for (String pair : pairs) {
				String[] kv = pair.split(":");
				if (kv.length != 2) continue;

				String key = kv[0].trim().replace("\"", "");
				String value = kv[1].trim();

				switch (key) {
					case "x": x = Integer.parseInt(value); break;
					case "y": y = Integer.parseInt(value); break;
					case "lx": lx = Integer.parseInt(value); break;
					case "ly": ly = Integer.parseInt(value); break;
					case "rx": rx = Integer.parseInt(value); break;
					case "ry": ry = Integer.parseInt(value); break;
				}
			}

			if (x != null && y != null) {
				if (lx != null && ly != null && rx != null && ry != null) {
					// S vodítky
					controlPoints.add(new CurvePoint(
						new Point(x, y),
						new Point(lx, ly),
						new Point(rx, ry)
					));
				} else {
					// Bez vodítek
					controlPoints.add(new CurvePoint(x, y));
				}
			}
		} catch (Exception e) {
			System.out.println("Chyba při parsování bodu: " + e.getMessage());
		}
	}

	/**
	 * Uložení bodů do JSON souboru
	 */
	private void savePoints() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
			writer.println("{");
			writer.println("  \"closedCurve\": " + closedCurve + ",");
			writer.println("  \"points\": [");

			for (int i = 0; i < controlPoints.size(); i++) {
				CurvePoint p = controlPoints.get(i);
				writer.print("    {");
				writer.print("\"x\": " + p.main.x + ", ");
				writer.print("\"y\": " + p.main.y + ", ");
				writer.print("\"lx\": " + p.leftHandle.x + ", ");
				writer.print("\"ly\": " + p.leftHandle.y + ", ");
				writer.print("\"rx\": " + p.rightHandle.x + ", ");
				writer.print("\"ry\": " + p.rightHandle.y);
				writer.print("}");

				if (i < controlPoints.size() - 1) {
					writer.println(",");
				} else {
					writer.println();
				}
			}

			writer.println("  ]");
			writer.println("}");

			System.out.println("Uloženo " + controlPoints.size() + " bodů do " + SAVE_FILE + " (closedCurve: " + closedCurve + ")");
		} catch (IOException e) {
			System.out.println("Chyba při ukládání: " + e.getMessage());
		}
	}

	/**
	 * Vytvoření okna editoru
	 */
	private void createEditorWindow() {
		editorFrame = new JFrame("Editor písmene S - Bézierovy křivky (Ctrl+klik = oba handlers)");
		editorFrame.setSize(700, 800);
		editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		editorFrame.setResizable(true); // Povolení změny velikosti

		// Panel pro kreslení s ohraničením
		JPanel drawPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(CANVAS_WIDTH + 100, CANVAS_HEIGHT + 100);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Vyčištění pozadí (světle šedé)
				g2d.setColor(new Color(240, 240, 240));
				g2d.fillRect(0, 0, getWidth(), getHeight());

				// Vykreslení plátna (bílé)
				int offsetX = (getWidth() - CANVAS_WIDTH) / 2;
				int offsetY = (getHeight() - CANVAS_HEIGHT) / 2;
				g2d.setColor(Color.WHITE);
				g2d.fillRect(offsetX, offsetY, CANVAS_WIDTH, CANVAS_HEIGHT);

				// Ohraničení plátna
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(2));
				g2d.drawRect(offsetX, offsetY, CANVAS_WIDTH, CANVAS_HEIGHT);

				// Translace pro vycentrování
				g2d.translate(offsetX, offsetY);

				if (controlPoints.size() >= 2) {
					// Vykreslení spline
					drawSplineOnGraphics(g2d, controlPoints, Color.BLACK);
				}

				// Vykreslení vodítek a kontrolních bodů
				for (int i = 0; i < controlPoints.size(); i++) {
					CurvePoint p = controlPoints.get(i);

					if (showHandles) {
						// Vodítka - čáry
						g2d.setColor(new Color(150, 150, 255));
						g2d.setStroke(new BasicStroke(1));
						g2d.drawLine(p.main.x, p.main.y, p.leftHandle.x, p.leftHandle.y);
						g2d.drawLine(p.main.x, p.main.y, p.rightHandle.x, p.rightHandle.y);

						// Levé vodítko
						boolean isSelectedLeft = (i == selectedIndex && selectedHandle == HandleType.LEFT_HANDLE);
						boolean isBothSelected = (i == selectedIndex && selectedHandle == HandleType.BOTH_HANDLES);
						g2d.setColor((isSelectedLeft || isBothSelected) ? Color.RED : new Color(100, 100, 255));
						g2d.fillOval(p.leftHandle.x - HANDLE_RADIUS, p.leftHandle.y - HANDLE_RADIUS,
								HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);

						// Pravé vodítko
						boolean isSelectedRight = (i == selectedIndex && selectedHandle == HandleType.RIGHT_HANDLE);
						g2d.setColor((isSelectedRight || isBothSelected) ? Color.RED : new Color(100, 100, 255));
						g2d.fillOval(p.rightHandle.x - HANDLE_RADIUS, p.rightHandle.y - HANDLE_RADIUS,
								HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
					}

					// Spojnice mezi hlavními body
					if (i > 0) {
						g2d.setColor(new Color(200, 200, 200));
						g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0));
						CurvePoint prev = controlPoints.get(i - 1);
						g2d.drawLine(prev.main.x, prev.main.y, p.main.x, p.main.y);
					}

					// Spojnice posledního bodu s prvním (pokud je uzavřená křivka)
					if (closedCurve && i == controlPoints.size() - 1 && controlPoints.size() > 2) {
						g2d.setColor(new Color(200, 200, 200));
						g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0));
						CurvePoint first = controlPoints.get(0);
						g2d.drawLine(p.main.x, p.main.y, first.main.x, first.main.y);
					}

					// Hlavní bod
					boolean isSelectedMain = (i == selectedIndex &&
						(selectedHandle == HandleType.MAIN || selectedHandle == HandleType.BOTH_HANDLES));
					g2d.setColor(isSelectedMain ? Color.RED : Color.BLUE);
					g2d.fillOval(p.main.x - POINT_RADIUS, p.main.y - POINT_RADIUS,
							POINT_RADIUS * 2, POINT_RADIUS * 2);

					// Číslo bodu
					g2d.setColor(Color.BLACK);
					g2d.setFont(new Font("Arial", Font.PLAIN, 10));
					g2d.drawString(String.valueOf(i), p.main.x + POINT_RADIUS + 2, p.main.y - POINT_RADIUS);
				}
			}
		};

		drawPanel.setBackground(Color.WHITE);

		// Mouse listener pro editaci bodů
		MouseAdapter mouseAdapter = new MouseAdapter() {
			private int getOffsetX() {
				return (drawPanel.getWidth() - CANVAS_WIDTH) / 2;
			}

			private int getOffsetY() {
				return (drawPanel.getHeight() - CANVAS_HEIGHT) / 2;
			}

			private int toCanvasX(int mouseX) {
				return mouseX - getOffsetX();
			}

			private int toCanvasY(int mouseY) {
				return mouseY - getOffsetY();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int canvasX = toCanvasX(e.getX());
				int canvasY = toCanvasY(e.getY());
				boolean ctrlPressed = e.isControlDown();

				// Nejprve hledej vodítka, pak hlavní body
				for (int i = 0; i < controlPoints.size(); i++) {
					CurvePoint p = controlPoints.get(i);

					if (showHandles) {
						// Levé vodítko
						if (isNear(canvasX, canvasY, p.leftHandle.x, p.leftHandle.y, HANDLE_RADIUS)) {
							selectedPoint = p;
							selectedIndex = i;
							selectedHandle = HandleType.LEFT_HANDLE;
							drawPanel.repaint();
							return;
						}

						// Pravé vodítko
						if (isNear(canvasX, canvasY, p.rightHandle.x, p.rightHandle.y, HANDLE_RADIUS)) {
							selectedPoint = p;
							selectedIndex = i;
							selectedHandle = HandleType.RIGHT_HANDLE;
							drawPanel.repaint();
							return;
						}
					}

					// Hlavní bod - s Ctrl = oba handlers, bez Ctrl = jen bod
					if (isNear(canvasX, canvasY, p.main.x, p.main.y, POINT_RADIUS)) {
						selectedPoint = p;
						selectedIndex = i;
						selectedHandle = ctrlPressed ? HandleType.BOTH_HANDLES : HandleType.MAIN;
						drawPanel.repaint();
						return;
					}
				}

				selectedPoint = null;
				selectedIndex = -1;
				selectedHandle = HandleType.NONE;
				drawPanel.repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (selectedPoint != null) {
					int canvasX = toCanvasX(e.getX());
					int canvasY = toCanvasY(e.getY());
					int clampedX = clamp(canvasX, 0, CANVAS_WIDTH - 1);
					int clampedY = clamp(canvasY, 0, CANVAS_HEIGHT - 1);

					switch (selectedHandle) {
						case MAIN:
							// Posuň hlavní bod i vodítka
							int dx = clampedX - selectedPoint.main.x;
							int dy = clampedY - selectedPoint.main.y;
							selectedPoint.main.x = clampedX;
							selectedPoint.main.y = clampedY;
							selectedPoint.leftHandle.x = clamp(selectedPoint.leftHandle.x + dx, 0, CANVAS_WIDTH - 1);
							selectedPoint.leftHandle.y = clamp(selectedPoint.leftHandle.y + dy, 0, CANVAS_HEIGHT - 1);
							selectedPoint.rightHandle.x = clamp(selectedPoint.rightHandle.x + dx, 0, CANVAS_WIDTH - 1);
							selectedPoint.rightHandle.y = clamp(selectedPoint.rightHandle.y + dy, 0, CANVAS_HEIGHT - 1);
							break;
						case LEFT_HANDLE:
							selectedPoint.leftHandle.x = clampedX;
							selectedPoint.leftHandle.y = clampedY;
							break;
						case RIGHT_HANDLE:
							selectedPoint.rightHandle.x = clampedX;
							selectedPoint.rightHandle.y = clampedY;
							break;
						case BOTH_HANDLES:
							// Symetrický pohyb obou handlers - myš ovládá směr
							int deltaX = clampedX - selectedPoint.main.x;
							int deltaY = clampedY - selectedPoint.main.y;

							// Pravý handle jde tam, kam je myš
							selectedPoint.rightHandle.x = clampedX;
							selectedPoint.rightHandle.y = clampedY;

							// Levý handle jde na opačnou stranu symetricky
							selectedPoint.leftHandle.x = clamp(selectedPoint.main.x - deltaX, 0, CANVAS_WIDTH - 1);
							selectedPoint.leftHandle.y = clamp(selectedPoint.main.y - deltaY, 0, CANVAS_HEIGHT - 1);
							break;
					}
					drawPanel.repaint();
					updateMainWindow();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (selectedPoint != null) {
					updateMainWindow();
				}
			}

			private boolean isNear(int x1, int y1, int x2, int y2, int radius) {
				return Math.abs(x1 - x2) <= radius && Math.abs(y1 - y2) <= radius;
			}
		};

		drawPanel.addMouseListener(mouseAdapter);
		drawPanel.addMouseMotionListener(mouseAdapter);

		// Panel s tlačítky
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 2, 5, 5));

		JButton saveButton = new JButton("Uložit");
		saveButton.addActionListener(e -> {
			savePoints();
			updateMainWindow();
		});

		JButton toggleClosedButton = new JButton("Křivka: " + (closedCurve ? "UZAVŘENÁ" : "OTEVŘENÁ"));

		JButton loadButton = new JButton("Načíst");
		loadButton.addActionListener(e -> {
			loadPoints();
			toggleClosedButton.setText("Křivka: " + (closedCurve ? "UZAVŘENÁ" : "OTEVŘENÁ"));
			drawPanel.repaint();
			updateMainWindow();
		});

		JButton addButton = new JButton("Přidat bod");
		addButton.addActionListener(e -> {
			// Přidat nový bod bez přepočítávání existujících handlers
			controlPoints.add(new CurvePoint(150, 150));
			drawPanel.repaint();
			updateMainWindow();
		});

		JButton removeButton = new JButton("Odebrat bod");
		removeButton.addActionListener(e -> {
			if (selectedIndex >= 0 && selectedIndex < controlPoints.size()) {
				controlPoints.remove(selectedIndex);
				selectedIndex = -1;
				selectedPoint = null;
				selectedHandle = HandleType.NONE;
				drawPanel.repaint();
				updateMainWindow();
			}
		});

		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(e -> {
			controlPoints.clear();
			createDefaultSPoints();
			toggleClosedButton.setText("Křivka: " + (closedCurve ? "UZAVŘENÁ" : "OTEVŘENÁ"));
			drawPanel.repaint();
			updateMainWindow();
		});

		JButton toggleHandlesButton = new JButton("Vodítka: ZAP");
		toggleHandlesButton.addActionListener(e -> {
			showHandles = !showHandles;
			toggleHandlesButton.setText("Vodítka: " + (showHandles ? "ZAP" : "VYP"));
			drawPanel.repaint();
		});

		JButton resetHandlesButton = new JButton("Reset vodítka");
		resetHandlesButton.addActionListener(e -> {
			if (selectedIndex >= 0 && selectedIndex < controlPoints.size()) {
				controlPoints.get(selectedIndex).resetHandles();
				drawPanel.repaint();
				updateMainWindow();
			} else {
				JOptionPane.showMessageDialog(editorFrame,
					"Nejdřív vyberte bod kliknutím na něj.",
					"Info",
					JOptionPane.INFORMATION_MESSAGE);
			}
		});

		toggleClosedButton.addActionListener(e -> {
			closedCurve = !closedCurve;
			toggleClosedButton.setText("Křivka: " + (closedCurve ? "UZAVŘENÁ" : "OTEVŘENÁ"));
			drawPanel.repaint();
			updateMainWindow();
		});

		buttonPanel.add(saveButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(toggleHandlesButton);
		buttonPanel.add(resetHandlesButton);
		buttonPanel.add(toggleClosedButton);

		// Sestavení okna
		editorFrame.setLayout(new BorderLayout());
		editorFrame.add(drawPanel, BorderLayout.CENTER);
		editorFrame.add(buttonPanel, BorderLayout.SOUTH);

		editorFrame.setVisible(true);

		// Počáteční vykreslení
		updateMainWindow();
	}

	/**
	 * Aktualizace hlavního okna s výsledným písmem
	 */
	private void updateMainWindow() {
		V_RAM vRam = new V_RAM(CANVAS_WIDTH, CANVAS_HEIGHT);
		clearBackground(vRam, Color.WHITE);

		if (controlPoints.size() >= 2) {
			drawSplineVRAM(vRam, controlPoints, Color.BLACK, 2);
		}

		mainWindow.showImage(vRam.getImage());
	}

	/**
	 * Vykreslení spline na Graphics2D (pro editor)
	 */
	private void drawSplineOnGraphics(Graphics2D g2d, List<CurvePoint> points, Color color) {
		int n = points.size();
		if (n < 2) return;

		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(2));

		// Definice a vykreslení segmentů
		double step = 0.01;

		int segments = closedCurve ? n : n - 1; // Pokud uzavřená, přidáme segment z posledního do prvního

		for (int i = 0; i < segments; i++) {
			CurvePoint p0 = points.get(i);
			CurvePoint p1 = points.get((i + 1) % n); // Modulo pro uzavřenou křivku

			// 4 kontrolní body pro kubickou Bézierovu křivku
			Point cp0 = p0.main;           // Začátek
			Point cp1 = p0.rightHandle;    // Pravé vodítko prvního bodu
			Point cp2 = p1.leftHandle;     // Levé vodítko druhého bodu
			Point cp3 = p1.main;           // Konec

			// Pre-kalkulace koeficientů
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
					g2d.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
				}

				prevPoint = currentPoint;
			}
		}
	}

	/**
	 * Vykreslení spline do V_RAM
	 */
	private void drawSplineVRAM(V_RAM vRam, List<CurvePoint> points, Color color, int thickness) {
		int n = points.size();
		if (n < 2) return;

		int width = vRam.getWidth();
		int height = vRam.getHeight();

		// Definice a vykreslení segmentů
		double step = 0.005;

		int segments = closedCurve ? n : n - 1; // Pokud uzavřená, přidáme segment z posledního do prvního

		for (int i = 0; i < segments; i++) {
			CurvePoint p0 = points.get(i);
			CurvePoint p1 = points.get((i + 1) % n); // Modulo pro uzavřenou křivku

			// 4 kontrolní body
			Point cp0 = p0.main;
			Point cp1 = p0.rightHandle;
			Point cp2 = p1.leftHandle;
			Point cp3 = p1.main;

			// Pre-kalkulace koeficientů
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

				// Bounds checking
				int px = (int) Math.round(x);
				int py = (int) Math.round(y);
				if (px < 0 || px >= width || py < 0 || py >= height) {
					prevPoint = null; // Přeskoč kreslení mimo hranice
					continue;
				}

				Point currentPoint = new Point(px, py);

				if (prevPoint != null) {
					drawThickLine(vRam, prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y, color, thickness);
				}

				prevPoint = currentPoint;
			}

			// Závěrečný segment
			int fx = (int) Math.round(qx0 + qx1 + qx2 + qx3);
			int fy = (int) Math.round(qy0 + qy1 + qy2 + qy3);

			if (fx >= 0 && fx < width && fy >= 0 && fy < height) {
				Point finalPoint = new Point(fx, fy);
				if (prevPoint != null) {
					drawThickLine(vRam, prevPoint.x, prevPoint.y, finalPoint.x, finalPoint.y, color, thickness);
				}
			}
		}
	}

	/**
	 * Kreslení tlustší čáry
	 */
	private void drawThickLine(V_RAM vRam, int x1, int y1, int x2, int y2, Color color, int thickness) {
		int width = vRam.getWidth();
		int height = vRam.getHeight();

		for (int dy = -thickness/2; dy <= thickness/2; dy++) {
			for (int dx = -thickness/2; dx <= thickness/2; dx++) {
				int nx1 = x1 + dx;
				int ny1 = y1 + dy;
				int nx2 = x2 + dx;
				int ny2 = y2 + dy;

				// Bounds checking - kreslí jen pokud jsou OBA body v rozsahu
				if (nx1 >= 0 && nx1 < width && ny1 >= 0 && ny1 < height &&
					nx2 >= 0 && nx2 < width && ny2 >= 0 && ny2 < height) {
					Cv05_LinesDrawing.drawLine(vRam, nx1, ny1, nx2, ny2, color);
				}
			}
		}
	}

	/**
	 * Omezení hodnoty na daný rozsah
	 */
	private int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Vyčištění pozadí
	 */
	private void clearBackground(V_RAM vRam, Color color) {
		for (int y = 0; y < vRam.getHeight(); y++) {
			for (int x = 0; x < vRam.getWidth(); x++) {
				vRam.setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue());
			}
		}
	}

	/**
	 * Zavření editoru při přerušení úlohy
	 */
	public void dispose() {
		System.out.println("KU2_EXT1.dispose() - zavírám editor");
		if (editorFrame != null) {
			SwingUtilities.invokeLater(() -> {
				System.out.println("KU2_EXT1.dispose() - volám editorFrame.dispose()");
				editorFrame.dispose();
				editorFrame = null;
			});
		} else {
			System.out.println("KU2_EXT1.dispose() - editorFrame je null");
		}
	}
}
