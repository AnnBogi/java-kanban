package canban.manager.http;

import canban.manager.FileBackedTasksManager;
import canban.manager.Managers;
import canban.tasks.Task;
import canban.utils.ManagerSaveException;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static canban.manager.InMemoryHistoryManager.historyToString;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final String key; //todo:

    @SneakyThrows
    public HttpTaskManager(String storageUrl, String key) {
        this.kvTaskClient = new KVTaskClient(storageUrl);
        this.key = key;
        final SaveDto saveDto = kvTaskClient.load(key);
        addAllTasks(saveDto.taskList);
    }

    @Override
    public void save() {
        try {
            final ArrayList<Task> tasks = new ArrayList<>(getPrioritizedTasks());
            final String history = historyToString(Managers.getDefaultHistory());
            final SaveDto saveDto = new SaveDto();
            saveDto.taskList = tasks;
            saveDto.history = history;

            kvTaskClient.put(key, new Gson().toJson(saveDto));
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Failed to save tasks and history to memory file: " + e.getMessage(), e);
        }
    }


    static class SaveDto {
        List<Task> taskList;
        String history;
    }
}
