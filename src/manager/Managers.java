package manager;

/**
 * Утилитарный класс управления.
 */
public class Managers {

    public static TaskManager getInMemoryTaskManager(HistoryManager defaultHistory) {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}