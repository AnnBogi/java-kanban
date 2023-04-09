package manager;

/**
 * Утилитарный класс управления.
 */
public final class Managers {

    private static final InMemoryTaskManager IN_MEMORY_TASK_MANAGER = new InMemoryTaskManager();
    private static final InMemoryHistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();
    private static final FileBackedTasksManager FILE_BACKED_TASKS_MANAGER = new FileBackedTasksManager();

    private Managers() {}

    public static InMemoryTaskManager getInMemoryTaskManager() {
        return IN_MEMORY_TASK_MANAGER;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return HISTORY_MANAGER;
    }

    public static FileBackedTasksManager getFileBackedTasksManager() {
        return FILE_BACKED_TASKS_MANAGER;
    }

}