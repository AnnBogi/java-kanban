package canban.server.controllers;

import java.io.IOException;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import canban.manager.FileBackedTasksManager;
import canban.utils.ParamExtractorUtils;
import canban.server.HttpTaskServer;
import canban.tasks.Subtask;
import canban.manager.Managers;

import static canban.server.HttpTaskServer.GET;
import static canban.server.HttpTaskServer.DELETE;
import static canban.server.HttpTaskServer.POST;
import static canban.server.HttpTaskServer.PUT;

public class SubTaskController {

    private final FileBackedTasksManager fileBackedTasksManager;

    public SubTaskController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handeSubTask(HttpExchange h) throws IOException {
        var params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        var id = ParamExtractorUtils.getQueryParamInteger(params, "id");
        var method = h.getRequestMethod();

        if (GET.equals(method)) {
            if (id.isEmpty()) {
                return Optional.of(fileBackedTasksManager.getAllSubtasks());
            }
            return fileBackedTasksManager.getSubtask(id.get());
        } else if (DELETE.equals(method)) {
            if (id.isEmpty()) {
                fileBackedTasksManager.removeAllSubtasks();
            } else {
                fileBackedTasksManager.removeSubtaskById(id.get());
            }
            return Optional.empty();
        } else if (POST.equals(method)) {
            var subTask = readSubTaskFromBody(h);
            fileBackedTasksManager.createSubtask(subTask);
            return Optional.empty();
        } else if (PUT.equals(method)) {
            var subTask = readSubTaskFromBody(h);
            fileBackedTasksManager.updateSubtask(subTask);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

    private Subtask readSubTaskFromBody(HttpExchange h) throws IOException {
        var value = HttpTaskServer.readText(h);
        var gson = Managers.getGson();
        return gson.fromJson(value, Subtask.class);
    }

}