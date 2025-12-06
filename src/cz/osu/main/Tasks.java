package cz.osu.main;

import cz.osu.tasks.KU1_TWO;

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
     * KU2 - TODO: Zatím neimplementováno
     */
    public static class KU2 implements RunnableExercise {
        @Override
        public String getDisplayName() {
            return "KU2 (TODO)";
        }

        @Override
        public void execute(MainWindow mainWindow) {
            System.out.println("KU2 zatím není implementováno");
            // TODO: Implementace KU2
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
