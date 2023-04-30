package canban.application.client.controllers;

import canban.helpers.HttpMethods;
import canban.helpers.ParamExtractorUtils;
import canban.manager.FileBackedTasksManager;
import canban.tasks.Task;
import canban.utils.IOUtils;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class TaskController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public TaskController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<Task> handleTask(HttpExchange h, String method) throws IOException {
        Map<String, String> params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        Optional<Integer> id = ParamExtractorUtils.getQueryParamInteger(params, "id");

        if (HttpMethods.GET.name().equals(method)) {
            if (id.isEmpty()) {
                throw new IllegalArgumentException("id not found");
            }
            return fileBackedTasksManager.getTask(id.get());
        } else if (HttpMethods.DELETE.name().equals(method)) {
            if (id.isEmpty()) {
                throw new IllegalArgumentException("id not found");
            }
            fileBackedTasksManager.removeTaskById(id.get());
            return Optional.empty();
        } else if (HttpMethods.POST.name().equals(method)) {
            final Task task = readTaskFromBody(h);
            fileBackedTasksManager.createTask(task);
            return Optional.empty();
        } else if (HttpMethods.PUT.name().equals(method)) {
            final Task task = readTaskFromBody(h);
            fileBackedTasksManager.updateTask(task);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

    private Task readTaskFromBody(HttpExchange h) throws IOException {
        final String value = IOUtils.readText(h);
        final Gson gson = new Gson();
        return gson.fromJson(value, Task.class);
    }

}