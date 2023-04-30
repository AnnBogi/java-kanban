package canban.manager.http;

import canban.helpers.HttpMethods;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 9090;
    private final HttpServer server;
    private final String apiToken;
    private final Map<String, String> data = new HashMap<>();

    public HttpTaskServer() throws IOException {
        apiToken = generateApiToken();

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken); //todo:
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            if (checkAuth(h)) {
                return;
            }

            if (!HttpMethods.GET.name().equals(h.getRequestMethod())) {
                System.out.println("/save ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                return;
            }

            String key = h.getRequestURI().getPath().substring("/load/".length());
            if (key.isEmpty()) {
                System.out.println("Key для чтения пустой. key указывается в пути: /load/{key}");
                h.sendResponseHeaders(400, 0);
                return;
            }

            final String value = data.get(key);
            if (value == null) {
                System.out.println("отсутствует значение в бд");
                h.sendResponseHeaders(404, 0);
                return;
            }

            sendText(h, value);
            h.sendResponseHeaders(200, 0);

        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");

            if (checkAuth(h)) {
                return;
            }
            if (HttpMethods.POST.name().equals(h.getRequestMethod())) {
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

    private boolean checkAuth(HttpExchange h) throws IOException {
        if (!hasAuth(h)) {
            System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
            h.sendResponseHeaders(403, 0);
            return true;
        }
        return false;
    }


    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "canban/application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    public static String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}