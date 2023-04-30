package canban.manager;

import canban.manager.http.HttpTaskManager;

/**
 * Утилитарный класс управления.
 */
public final class Managers {

    private static final InMemoryTaskManager IN_MEMORY_TASK_MANAGER = new InMemoryTaskManager();
    private static final InMemoryHistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();
    private static final FileBackedTasksManager FILE_BACKED_TASKS_MANAGER = new FileBackedTasksManager();
    public static final String HTTP_LOCALHOST_8080 = "http://localhost:9090";
    public static final String SOME_RANDOM_KEY = "some-random-key";
    private static final HttpTaskManager HTTP_TASK_MANAGER = new HttpTaskManager(HTTP_LOCALHOST_8080, SOME_RANDOM_KEY);
    //  я не знаю как для статической переменной вызвать конструктор

    public static InMemoryTaskManager getInMemoryTaskManager() {
        return IN_MEMORY_TASK_MANAGER;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return HISTORY_MANAGER;
    }

    public static FileBackedTasksManager getFileBackedTasksManager() {
        return FILE_BACKED_TASKS_MANAGER;
    }
    public static HttpTaskManager getHttpTaskManager() {
        return HTTP_TASK_MANAGER;
    }

    private Managers() {
    }

}