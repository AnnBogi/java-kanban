package canban.application.client.controllers;

import canban.helpers.HttpMethods;
import canban.helpers.ParamExtractorUtils;
import canban.manager.FileBackedTasksManager;
import canban.tasks.Epic;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EpicController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public EpicController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handleEpics(HttpExchange h, String method) throws IOException {
        Map<String, String> params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        Optional<Integer> id = ParamExtractorUtils.getQueryParamInteger(params, "id");

        if (HttpMethods.GET.name().equals(method)) {
            return handleGET(h, id);
        } else if (HttpMethods.DELETE.name().equals(method)) {
            return handleDELETE(id);
        } else if (HttpMethods.POST.name().equals(method)) {
            return handlePOST(h);
        } else if (HttpMethods.PUT.name().equals(method)) {
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
        final String value = readText(h);
        System.out.println("value = " + value); //todo: EMPTY IO?
        final Gson gson = new Gson();
        return Optional.ofNullable(gson.fromJson(value, Epic.class));
    }

    public static String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}