package canban.server.controllers;

import java.io.IOException;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import canban.manager.FileBackedTasksManager;
import canban.utils.ParamExtractorUtils;
import canban.server.HttpTaskServer;
import canban.tasks.Task;
import canban.manager.Managers;

import static canban.server.HttpTaskServer.GET;
import static canban.server.HttpTaskServer.DELETE;
import static canban.server.HttpTaskServer.POST;
import static canban.server.HttpTaskServer.PUT;

public class TaskController {

    private final FileBackedTasksManager fileBackedTasksManager;

    public TaskController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handleTask(HttpExchange h) throws IOException {
        var params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        var id = ParamExtractorUtils.getQueryParamInteger(params, "id");
        var method = h.getRequestMethod();

        if (GET.equals(method)) {
            if (id.isEmpty()) {
                return Optional.of(fileBackedTasksManager.getAllTasks());
            }
            return fileBackedTasksManager.getTask(id.get());
        } else if (DELETE.equals(method)) {
            if (id.isEmpty()) {
                fileBackedTasksManager.removeAllTasks();
            } else {
                fileBackedTasksManager.removeTaskById(id.get());
            }
            return Optional.empty();
        } else if (POST.equals(method)) {
            var task = readTaskFromBody(h);
            fileBackedTasksManager.createTask(task);
            return Optional.empty();
        } else if (PUT.equals(method)) {
            var task = readTaskFromBody(h);
            fileBackedTasksManager.updateTask(task);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

    private Task readTaskFromBody(HttpExchange h) throws IOException {
        var value = HttpTaskServer.readText(h);
        var gson = Managers.getGson();
        return gson.fromJson(value, Task.class);
    }

}