package canban.server.controllers;

import java.util.Optional;

import com.sun.net.httpserver.HttpExchange;

import canban.utils.ParamExtractorUtils;
import canban.manager.FileBackedTasksManager;

import static canban.server.HttpTaskServer.DELETE;
import static canban.server.HttpTaskServer.GET;

public class HistoryController {
    private final FileBackedTasksManager fileBackedTasksManager;

    public HistoryController(FileBackedTasksManager fileBackedTasksManager) {
        this.fileBackedTasksManager = fileBackedTasksManager;
    }

    public Optional<?> handleHistory(HttpExchange h) {
        var params = ParamExtractorUtils.queryToMap(h.getRequestURI().getQuery());
        var id = ParamExtractorUtils.getQueryParamInteger(params, "id");
        var method = h.getRequestMethod();

        if (GET.equals(method)) {
            return Optional.of(fileBackedTasksManager.getHistory());
        } else if (DELETE.equals(method)) {
            id.ifPresent(fileBackedTasksManager::removeHistoryById);
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("unknown");
        }
    }

}
