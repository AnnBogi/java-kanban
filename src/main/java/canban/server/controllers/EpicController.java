package canban.server.controllers;

import java.io.IOException;
import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import canban.manager.FileBackedTasksManager;
import canban.server.HttpTaskServer;
import canban.utils.ParamExtractorUtils;
import canban.tasks.Epic;
import canban.manager.Managers;

import static canban.server.HttpTaskServer.GET;
import static canban.server.HttpTaskServer.DELETE;
import static canban.server.HttpTaskServer.POST;
import static canban.server.HttpTaskServer.PUT;

public class EpicController {

    private final FileBackedTasksManager fileBackedTasksManager;

    public EpicController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handleEpics(HttpExchange h) throws IOException {
        var params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        var id = ParamExtractorUtils.getQueryParamInteger(params, "id");
        var method = h.getRequestMethod();

        if (GET.equals(method)) {
            if (id.isEmpty()) {
                return Optional.of(fileBackedTasksManager.getAllEpics());
            }
            return fileBackedTasksManager.getEpic(id.get());
        } else if (DELETE.equals(method)) {
            if (id.isEmpty()) {
                fileBackedTasksManager.removeAllEpics();
            } else {
                fileBackedTasksManager.removeEpicById(id.get());
            }
            return Optional.empty();
        } else if (POST.equals(method)) {
            var epic = readEpicFromBody(h);
            fileBackedTasksManager.createEpic(epic);
            return Optional.empty();
        } else if (PUT.equals(method)) {
            var epic = readEpicFromBody(h);
            fileBackedTasksManager.updateEpic(epic);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

    private Epic readEpicFromBody(HttpExchange h) throws IOException {
        var value = HttpTaskServer.readText(h);
        System.out.println("value = " + value);
        var gson = Managers.getGson();
        return gson.fromJson(value, Epic.class);
    }

}