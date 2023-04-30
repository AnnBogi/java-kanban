package canban.utils;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IOUtils {
    private IOUtils() {

    }

    public static void convertAndSend(HttpExchange h, Object allTasks) throws IOException {
        final Gson gson = new Gson();
        final String response = gson.toJson(allTasks);
        sendText(h, response);
    }

    public static void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "canban/application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}