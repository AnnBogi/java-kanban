package canban.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import canban.manager.Managers;
import canban.tasks.Task;
import com.google.gson.Gson;

public class KVTaskClient {

    private static final String BASE_URL = "http://localhost:" + KVServer.PORT;
    private static final HttpClient client = HttpClient.newHttpClient();

    public HttpResponse register() throws IOException, InterruptedException {
        var url = URI.create(BASE_URL + "/register");
        var request = HttpRequest.newBuilder().uri(url).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public HttpResponse save(String key, String apiToken, Object newTask) throws IOException, InterruptedException {
        var url = URI.create(BASE_URL + "/save/" + key + "/?API_TOKEN=" + apiToken);
        var gson = Managers.getGson();
        var json = gson.toJson(newTask);
        var body = HttpRequest.BodyPublishers.ofString(json);
        var request = HttpRequest.newBuilder().uri(url).POST(body).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public HttpResponse load(String key, String apiToken) throws IOException, InterruptedException {
        var url = URI.create(BASE_URL + "/load/" + key + "/?API_TOKEN=" + apiToken);
        var request = HttpRequest.newBuilder().uri(url).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

}