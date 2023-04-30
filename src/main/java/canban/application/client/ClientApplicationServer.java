package canban.application.client;

import canban.application.client.controllers.EpicController;
import canban.application.client.controllers.HistoryController;
import canban.application.client.controllers.SubTaskController;
import canban.application.client.controllers.TaskController;
import canban.application.client.controllers.TasksController;
import canban.manager.http.HttpTaskManager;
import canban.utils.IOUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public class ClientApplicationServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final EpicController epicController;
    private final HistoryController historyController;
    private final SubTaskController subTaskController;
    private final TaskController taskController;
    private final TasksController tasksController;


    @SneakyThrows
    public ClientApplicationServer(final HttpTaskManager httpTaskManager) {
        epicController = new EpicController(httpTaskManager);
        historyController = new HistoryController(httpTaskManager);
        subTaskController = new SubTaskController(httpTaskManager);
        taskController = new TaskController(httpTaskManager);
        tasksController = new TasksController(httpTaskManager);


        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::load);

        server.start();
    }

    private void load(HttpExchange h) throws IOException {
        try {
            Optional<?> response = Optional.empty();
            String path = h.getRequestURI().getPath();
            path = path.replaceFirst("/tasks/", "");

            final String method = h.getRequestMethod();

            switch (path) {
                case "":
                    response = tasksController.handleTasks(h, method);
                    break;
                case "task":
                    response = taskController.handleTask(h, method);
                    break;
                case "subtask":
                    response = subTaskController.handeSubTask(h, method);
                    break;
                case "epic":
                    response = epicController.handleEpics(h, method);
                    break;
                case "history":
                    response = historyController.handleHistory(h, method);
                    break;
                default:
                    throw new RuntimeException("unknown path");
            }


            if (response.isPresent()) {
                IOUtils.convertAndSend(h, response);
            }
            h.sendResponseHeaders(200, 0);
        } finally {
            h.close();
        }
    }

}