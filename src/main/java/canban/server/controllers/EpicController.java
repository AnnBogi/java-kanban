package canban.server.controllers;

import canban.manager.FileBackedTasksManager;
import canban.server.KVServer;
import canban.server.ParamExtractorUtils;
import canban.tasks.Epic;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static canban.server.KVServer.DELETE;
import static canban.server.KVServer.GET;
import static canban.server.KVServer.POST;
import static canban.server.KVServer.PUT;

public class EpicController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public EpicController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handleEpics(HttpExchange h, String method) throws IOException {
        Map<String, String> params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        Optional<Integer> id = ParamExtractorUtils.getQueryParamInteger(params, "id");

        if (GET.equals(method)) {
            return handleGET(h, id);
        } else if (DELETE.equals(method)) {
            return handleDELETE(id);
        } else if (POST.equals(method)) {
            return handlePOST(h);
        } else if (PUT.equals(method)) {
            return handlePUT(h);
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

    private Optional<Object> handlePUT(HttpExchange h) throws IOException {
        final Epic epic = readEpicFromBody(h).orElseThrow(() -> new IllegalArgumentException("body is empty"));
        fileBackedTasksManager.updateEpic(epic);
        return Optional.empty();
    }

    private Optional<Object> handlePOST(HttpExchange h) throws IOException {
        final Epic epic = readEpicFromBody(h).orElseThrow(() -> new IllegalArgumentException("body is empty"));
        fileBackedTasksManager.createEpic(epic);
        return Optional.empty();
    }

    private Optional<Object> handleDELETE(Optional<Integer> id) {
        if (id.isPresent()) {
            fileBackedTasksManager.removeEpicById(id.get());
            return Optional.empty();
        } else {
            fileBackedTasksManager.removeAllEpics();
            return Optional.empty();
        }
    }

    private Optional<?> handleGET(HttpExchange h, Optional<Integer> id) throws IOException {
        if (id.isPresent()) {
            return Optional.of(fileBackedTasksManager.getEpic(id.get()));
        } else {
            final Optional<Epic> epic = readEpicFromBody(h);
            if (epic.isPresent()) {
                return Optional.of(fileBackedTasksManager.getSubtaskOfEpic(epic.get()));
            } else {
                return Optional.of(fileBackedTasksManager.getAllEpics());
            }
        }
    }

    private Optional<Epic> readEpicFromBody(HttpExchange h) throws IOException {
        final String value = KVServer.readText(h);
        System.out.println("value = " + value); //todo: EMPTY IO?
        final Gson gson = new Gson();
        return Optional.ofNullable(gson.fromJson(value, Epic.class));
    }

}
