package canban.server;

import canban.manager.Managers;
import canban.server.controllers.*;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT"; //добавили, тк не хватило стандартных
    public static final String DELETE = "DELETE";
    private final HttpServer server;

    private final EpicController epicController;
    private final HistoryController historyController;
    private final SubTaskController subTaskController;
    private final TaskController taskController;
    private final AllTasksController allTasksController;

    public HttpTaskServer() throws IOException {
        var httpTaskManager = Managers.getHttpTaskManager();

        epicController = new EpicController(httpTaskManager);
        historyController = new HistoryController(httpTaskManager);
        subTaskController = new SubTaskController(httpTaskManager);
        taskController = new TaskController(httpTaskManager);
        allTasksController = new AllTasksController(httpTaskManager);

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::tasksProcessed);
        server.createContext("/tasks/epic", this::epicsProcessed);
        server.createContext("/tasks/subtask", this::subTasksProcessed);
        server.createContext("/tasks", this::allTasksProcessed);
        server.createContext("/tasks/history", this::historyProcessed);
    }

    public void start() {
        System.out.println("Запускаем сервер HTTP " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void tasksProcessed(HttpExchange httpExchange) throws IOException {
        try {
            var result = taskController.handleTask(httpExchange);
            if (result.isPresent()) {
                convertAndSend(httpExchange, result);
            }
            httpExchange.sendResponseHeaders(200, 0);
        } finally {
            httpExchange.close();
        }
    }

    private void epicsProcessed(HttpExchange httpExchange) {
        try {
            var result = epicController.handleEpics(httpExchange);
            if (result.isPresent()) {
                convertAndSend(httpExchange, result);
            }
            httpExchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    private void subTasksProcessed(HttpExchange httpExchange) {
        try {
            var result = subTaskController.handeSubTask(httpExchange);
            if (result.isPresent()) {
                convertAndSend(httpExchange, result);
            }
            httpExchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    private void historyProcessed(HttpExchange httpExchange) {
        try {
            var result = historyController.handleHistory(httpExchange);
            if (result.isPresent()) {
                convertAndSend(httpExchange, result);
            }
            httpExchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    private void allTasksProcessed(HttpExchange httpExchange) {
        try {
            var result = allTasksController.handleAllTasks(httpExchange);
            if (result.isPresent()) {
                convertAndSend(httpExchange, result);
            }
            httpExchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            httpExchange.close();
        }
    }

    private void convertAndSend(HttpExchange h, Optional allTasks) throws IOException {
        var gson = new GsonBuilder().create();
        var response = gson.toJson(allTasks.get());

        sendText(h, response);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        var resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

}