package canban.server;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TaskManagerControllerTest {
//    @Test
//    @SneakyThrows
//    void test(){
//        HttpClient client = HttpClient.newHttpClient();
//        URI url = URI.create("http://localhost:8080/tasks/task/");
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(url)
//                .GET()
//                .build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//    }
//
//    @Test
//    @SneakyThrows
//    void createTask(){
//        HttpClient client = HttpClient.newHttpClient();
//        var newTask = new Task();
//        URI url = URI.create("http://localhost:8080/tasks/task/");
//        Gson gson = new Gson();
//        String json = gson.toJson(newTask);
//        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
//        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//    }
//
//    @Test
//    @SneakyThrows
//    void getTaskById(){
//        HttpClient client = HttpClient.newHttpClient();
//        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
//        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
//        HttpResponse<String> resloadponse = client.send(request, HttpResponse.BodyHandlers.ofString());
//    }
//
//    @Test
//    @SneakyThrows
//    void testLoad(){
//        HttpResponse<String> response = getStringHttpResponse("http://localhost:8080/load");
//        System.out.println("response = " + response.body());
//    }


    @Test
    @SneakyThrows
    void testGetTaskById(){
        var response = getStringHttpResponse("tasks/task");
        System.out.println("response = " + response.body());
    }

    @Test
    @SneakyThrows
    void testGet(){
        var response = getStringHttpResponse("tasks/subtask");
        System.out.println("response = " + response.body());
    }

    @Test
    @SneakyThrows
    void testGetEpic(){
        var response = getStringHttpResponse("tasks/epic");
        System.out.println("response = " + response.body());
    }

    @Test
    @SneakyThrows
    void testGetHistory(){
        var response = getStringHttpResponse("tasks/history");
        System.out.println("response = " + response.body());
    }

    @Test
    @SneakyThrows
    void testGetTasks(){
        var response = getStringHttpResponse("tasks");
        System.out.println("response = " + response.body());
    }

    private static HttpResponse<String> getStringHttpResponse(String URL) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + URL);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }


}
