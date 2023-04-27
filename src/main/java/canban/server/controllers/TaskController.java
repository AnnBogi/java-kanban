package canban.server.controllers;

import canban.manager.FileBackedTasksManager;
import canban.server.KVServer;
import canban.server.ParamExtractorUtils;
import canban.tasks.Task;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static canban.server.KVServer.DELETE;
import static canban.server.KVServer.GET;
import static canban.server.KVServer.POST;
import static canban.server.KVServer.PUT;

public class TaskController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public TaskController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<Task> handleTask(HttpExchange h, String method) throws IOException {
        Map<String, String> params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        Optional<Integer> id = ParamExtractorUtils.getQueryParamInteger(params, "id");

        if (GET.equals(method)) {
            if (id.isEmpty()) {
                throw new IllegalArgumentException("id not found");
            }
            return fileBackedTasksManager.getTask(id.get());
        } else if (DELETE.equals(method)) {
            if (id.isEmpty()) {
                throw new IllegalArgumentException("id not found");
            }
            fileBackedTasksManager.removeTaskById(id.get());
            return Optional.empty();
        } else if (POST.equals(method)) {
            final Task task = readTaskFromBody(h);
            fileBackedTasksManager.createTask(task);
            return Optional.empty();
        } else if (PUT.equals(method)) {
            final Task task = readTaskFromBody(h);
            fileBackedTasksManager.updateTask(task);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

    private Task readTaskFromBody(HttpExchange h) throws IOException {
        final String value = KVServer.readText(h);
        final Gson gson = new Gson();
        return gson.fromJson(value, Task.class);
    }

}