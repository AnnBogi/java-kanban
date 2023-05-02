package canban.manager;

import canban.adapter.LocalDateTimeAdapter;
import canban.server.HttpTaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

/**
 * Утилитарный класс управления.
 */
public final class Managers {

    private static final InMemoryTaskManager IN_MEMORY_TASK_MANAGER = new InMemoryTaskManager();
    private static final InMemoryHistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();
    private static final FileBackedTasksManager FILE_BACKED_TASKS_MANAGER = new FileBackedTasksManager();

    private static final HttpTaskManager HTTP_TASK_MANAGER = new HttpTaskManager();


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

    public static HttpTaskManager getHttpTaskManager() {
        return HTTP_TASK_MANAGER;
    }

    public static Gson getGson() {
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

        return gsonBuilder.create();
    }

}