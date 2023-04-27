package canban.server;

import canban.manager.FileBackedTasksManager;
import canban.manager.Managers;
import canban.server.controllers.EpicController;
import canban.server.controllers.HistoryController;
import canban.server.controllers.SubTaskController;
import canban.server.controllers.TaskController;
import canban.server.controllers.TasksController;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8080;
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT"; //добавили, тк не хватило стандартных
    public static final String DELETE = "DELETE";
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    private final EpicController epicController;
    private final HistoryController historyController;
    private final SubTaskController subTaskController;
    private final TaskController taskController;
    private final TasksController tasksController;

    private final FileBackedTasksManager fileBackedTasksManager;

    public KVServer() throws IOException {
        fileBackedTasksManager = Managers.getFileBackedTasksManager();

        epicController = new EpicController(fileBackedTasksManager);
        historyController = new HistoryController(fileBackedTasksManager);
        subTaskController = new SubTaskController(fileBackedTasksManager);
        taskController = new TaskController(fileBackedTasksManager);
        tasksController = new TasksController(fileBackedTasksManager);

        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/tasks", this::load);

        //todo: обработка исключений

//    server.createContext("/tasks/task", (HttpExchange h) -> {
//      System.out.println("/tasks/task");
//      h.close();
//    });
//    server.createContext("/tasks/subtask", (HttpExchange h) -> {
//      System.out.println("/tasks/task");
//      h.close();
//    });
//    server.createContext("/tasks/epic", (HttpExchange h) -> {
//      System.out.println("/tasks/epic");
//      h.close();
//    });
//    server.createContext("/tasks/history", (HttpExchange h) -> {
//      System.out.println("/tasks/history");
//      List<Task> history = httpTaskServer.getHistory();
//
//
//      sendText(h, new Gson().toJson(history));
//      h.close();
//    });
//
//    server.createContext("/tasks", (HttpExchange h) -> {
//      h.close();
//    });

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


            if(response.isPresent())convertAndSend(h, response);
            h.sendResponseHeaders(200, 0);
        } finally {
            h.close();
        }

    }


    private void convertAndSend(HttpExchange h, Object allTasks) throws IOException {
        final Gson gson = new Gson();
        final String response = gson.toJson(allTasks);
        sendText(h, response);
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if (POST.equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if (GET.equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    public static String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}