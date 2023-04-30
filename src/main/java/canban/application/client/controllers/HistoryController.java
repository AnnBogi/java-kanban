package canban.application.client.controllers;

import canban.manager.FileBackedTasksManager;
import canban.tasks.Task;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Optional;

public class HistoryController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public HistoryController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<List<Task>> handleHistory(HttpExchange h, String method) {
        return Optional.of(fileBackedTasksManager.getHistory());
    }
}
