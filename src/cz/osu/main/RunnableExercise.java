package cz.osu.main;

/**
 * Interface pro všechny exercises a tasks
 * Umožňuje jednotnou strukturu pro spouštění úloh
 */
public interface RunnableExercise {

    /**
     * Název úlohy zobrazený v menu
     * @return Název pro zobrazení (např. "CV01 - RGB Model")
     */
    String getDisplayName();

    /**
     * Spuštění úlohy
     * @param mainWindow Reference na hlavní okno pro přístup k ImagePanel
     */
    void execute(MainWindow mainWindow);

    /**
     * Určuje, zda se má tato úloha spustit automaticky při startu
     * @return true pokud se má spustit jako výchozí
     */
    default boolean isDefault() {
        return false;
    }

    /**
     * Zpracování přerušení úlohy
     * Volá se když uživatel spustí jinou úlohu
     */
    default void onInterrupt() {
        // Prázdná defaultní implementace
    }
}
