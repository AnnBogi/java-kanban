package canban.server.controllers;

import canban.manager.FileBackedTasksManager;
import canban.server.KVServer;
import canban.server.ParamExtractorUtils;
import canban.tasks.Subtask;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static canban.server.KVServer.DELETE;
import static canban.server.KVServer.GET;
import static canban.server.KVServer.POST;
import static canban.server.KVServer.PUT;

public class SubTaskController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public SubTaskController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }


    public Optional<?> handeSubTask(HttpExchange h, String method) throws IOException {
        Map<String, String> params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        Optional<Integer> id = ParamExtractorUtils.getQueryParamInteger(params, "id");

        if (GET.equals(method)) {
            if (id.isPresent()) {
                return Optional.of(fileBackedTasksManager.getSubtask(id.get()));
            } else {
                return Optional.of(fileBackedTasksManager.getAllSubtasks());
            }

        } else if (DELETE.equals(method)) {
            if (id.isPresent()) {
                fileBackedTasksManager.removeSubtaskById(id.get());
                return Optional.empty();
            } else {
                fileBackedTasksManager.removeAllSubtasks();
                return Optional.empty();
            }

        } else if (POST.equals(method)) {
            final Subtask subtask = readSubTaskFromBody(h);
            fileBackedTasksManager.createSubtask(subtask);
            return Optional.empty();
        } else if (PUT.equals(method)) {
            final Subtask subtask = readSubTaskFromBody(h);
            fileBackedTasksManager.updateSubtask(subtask);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }


    private Subtask readSubTaskFromBody(HttpExchange h) throws IOException {
        final String value = KVServer.readText(h);
        final Gson gson = new Gson();
        return gson.fromJson(value, Subtask.class);
    }
}