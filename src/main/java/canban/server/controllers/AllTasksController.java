package canban.server.controllers;

import canban.manager.FileBackedTasksManager;
import com.sun.net.httpserver.HttpExchange;

import java.util.Optional;

import static canban.server.HttpTaskServer.GET;

public class AllTasksController {

    private final FileBackedTasksManager fileBackedTasksManager;

    public AllTasksController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handleAllTasks(HttpExchange h) {
        if (GET.equals(h.getRequestMethod())) {
            var allTasks = fileBackedTasksManager.getPrioritizedTasks();
            return Optional.of(allTasks);
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

}