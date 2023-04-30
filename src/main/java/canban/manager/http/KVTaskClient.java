package canban.manager.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String storageUrl;
    private final String apiToken;

    public KVTaskClient(String storageUrl) throws IOException, InterruptedException {
        this.storageUrl = storageUrl;
        this.apiToken = getApiToken();
    }

    private String getApiToken() throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(storageUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    void put(String key, String json) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(storageUrl + "/save/" + key + apikey());
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        final HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("load send.statusCode() = " + send.statusCode());
    }

    HttpTaskManager.SaveDto load(String key) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(storageUrl + "/load/" + key + apikey());
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        final HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("load send.statusCode() = " + send.statusCode());
        System.out.println("load send.body() = " + send.body());
        final String body = send.body();
        final Gson gson = new Gson();
        return gson.fromJson(body, HttpTaskManager.SaveDto.class);
    }

    private String apikey() {
        return String.format("?API_TOKEN=%s", apiToken);
    }
}