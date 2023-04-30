package canban.application.client.controllers;

import canban.helpers.HttpMethods;
import canban.manager.FileBackedTasksManager;
import canban.tasks.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class TasksController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public TasksController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<Object> handleTasks(HttpExchange h, String method) throws IOException {
        if (HttpMethods.GET.name().equals(method)) {
            final List<Task> allTasks = fileBackedTasksManager.getAllTasks();
            return Optional.of(allTasks);
        } else if (HttpMethods.DELETE.name().equals(method)) {
            fileBackedTasksManager.removeAllTasks();
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }
}