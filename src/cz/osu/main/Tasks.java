package cz.osu.main;

/**
 * Registry všech tasks - každý jako inner class
 */
public class Tasks {

    /**
     * KU1_ONE - Odstranění červených očí
     */
    public static class KU1_ONE implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "KU1_ONE - Odstranění červených očí";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            cz.osu.tasks.KU1_ONE KU1ONE = new cz.osu.tasks.KU1_ONE(mainWindow);
            KU1ONE.run();
        }
    }

    /**
     * KU1_TWO - Kontextové zpracování šedotónového obrazu
     */
    public static class KU1_TWO implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "KU1_TWO - Kontextové zpracování šedotónového obrazu";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            cz.osu.tasks.KU1_TWO KU1TWO = new cz.osu.tasks.KU1_TWO(mainWindow);
            KU1TWO.run();
        }
    }

    /**
     * KU2 - Kreslení spline pomocí kubických Bézierových křivek
     */
    public static class KU2 implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "KU2 - Kreslení spline (kubické Bézierovy křivky)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            cz.osu.tasks.KU2 ku2 = new cz.osu.tasks.KU2(mainWindow);
            ku2.run();
        }
    }

    /**
     * KU2_EXT1 - Písmenový font pomocí Bézierových křivek
     */
    public static class KU2_EXT1 implements RunnableExercise {
        private cz.osu.tasks.KU2_EXT1 currentInstance;

        @Override
        public String getDisplayName() {
            return "KU2_EXT1 - Písmenový font (Bézierovy křivky)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("Tasks.KU2_EXT1.execute() - this wrapper instance = " + this);
            currentInstance = new cz.osu.tasks.KU2_EXT1(mainWindow);
            System.out.println("Tasks.KU2_EXT1.execute() - created task instance = " + currentInstance);
            currentInstance.run();
        }

		@Override
		public void onInterrupt() {
			System.out.println("Tasks.KU2_EXT1.onInterrupt() - this wrapper instance = " + this);
			System.out.println("Tasks.KU2_EXT1.onInterrupt() - currentInstance = " + currentInstance);
			if (currentInstance != null) {
				currentInstance.dispose();
				currentInstance = null;
			} else {
				System.out.println("Tasks.KU2_EXT1.onInterrupt() - currentInstance je NULL!");
			}
		}

        @Override
        public boolean isDefault() {
            return true;  // Tato úloha se spustí automaticky při startu
        }
    }

    /**
     * KU3 - TODO: Zatím neimplementováno
     */
    public static class KU3 implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "KU3 (TODO)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("KU3 zatím není implementováno");
            // TODO: Implementace KU3
        }
    }
}
