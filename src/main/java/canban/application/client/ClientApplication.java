package canban.application.client;

import canban.manager.Managers;
import canban.manager.http.HttpTaskManager;
import canban.tasks.Task;
import canban.tasks.TaskStatus;
import canban.tasks.TaskType;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Date;

public class ClientApplication {

    private static ClientApplicationServer clientApplicationServer;

    public static void main(String[] args) {
        final HttpTaskManager httpTaskManager = Managers.getHttpTaskManager();
        System.out.println("httpTaskManager.getAllTasks() = " + httpTaskManager.getAllTasks());


        //auto-start
        clientApplicationServer = new ClientApplicationServer(httpTaskManager);

//    addTaskQuery();
        System.out.println("httpTaskManager.getAllTasks() = " + httpTaskManager.getAllTasks());
        httpTaskManager.createTask( createTestTask());
        System.out.println("httpTaskManager.getAllTasks() = " + httpTaskManager.getAllTasks());


    }

    private static Task createTestTask() {
        var task = new Task();
        task.setId(124);
        task.setName("name");
        task.setDescription("desc");
        task.setStatus(TaskStatus.NEW);
        task.setTaskType(TaskType.TASK);
        task.setDuration(10L);
        task.setStartTime(Date.from(Instant.now()));
        return task;
    }

    @SneakyThrows
    private static void addTaskQuery() {
        HttpClient client = HttpClient.newHttpClient();
        var newTask = new Task();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.body() = " + response.body());
    }
}
