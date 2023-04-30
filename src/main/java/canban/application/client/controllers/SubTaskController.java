package canban.application.client.controllers;

import canban.helpers.HttpMethods;
import canban.helpers.ParamExtractorUtils;
import canban.manager.FileBackedTasksManager;
import canban.tasks.Subtask;
import canban.utils.IOUtils;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class SubTaskController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public SubTaskController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }


    public Optional<?> handeSubTask(HttpExchange h, String method) throws IOException {
        Map<String, String> params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        Optional<Integer> id = ParamExtractorUtils.getQueryParamInteger(params, "id");

        if (HttpMethods.GET.name().equals(method)) {
            if (id.isPresent()) {
                return Optional.of(fileBackedTasksManager.getSubtask(id.get()));
            } else {
                return Optional.of(fileBackedTasksManager.getAllSubtasks());
            }

        } else if (HttpMethods.DELETE.name().equals(method)) {
            if (id.isPresent()) {
                fileBackedTasksManager.removeSubtaskById(id.get());
                return Optional.empty();
            } else {
                fileBackedTasksManager.removeAllSubtasks();
                return Optional.empty();
            }

        } else if (HttpMethods.POST.name().equals(method)) {
            final Subtask subtask = readSubTaskFromBody(h);
            fileBackedTasksManager.createSubtask(subtask);
            return Optional.empty();
        } else if (HttpMethods.PUT.name().equals(method)) {
            final Subtask subtask = readSubTaskFromBody(h);
            fileBackedTasksManager.updateSubtask(subtask);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }


    private Subtask readSubTaskFromBody(HttpExchange h) throws IOException {
        final String value = IOUtils.readText(h);
        final Gson gson = new Gson();
        return gson.fromJson(value, Subtask.class);
    }
}