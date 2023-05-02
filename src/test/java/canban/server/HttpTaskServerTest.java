package canban.server;

import canban.manager.Managers;
import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskStatus;
import canban.utils.DateUtils;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;

    private HttpTaskManager httpTaskManager;
    private KVServer kvServer;
    private Task firstTask;
    private Task updateFirstTask;
    private Task secondTask;
    private Epic firstEpic;
    private Epic secondEpic;
    private Subtask firstSubtaskOfFirstEpic;
    private Subtask updateFirstSubtaskOfFirstEpic;
    private Subtask secondSubtaskOfFirstEpic;
    private Subtask firstSubtaskOfSecondEpic;
    private Gson gson;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException {
        httpClient = HttpClient.newHttpClient();
        httpTaskManager = Managers.getHttpTaskManager();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        kvServer = new KVServer();
        kvServer.start();

        gson = Managers.getGson();

        detectVariable();
    }

    @AfterEach
    void tearDown() {
        httpTaskManager.removeAllTasks();
        httpTaskManager.removeAllEpics();
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void addTaskTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var json = gson.toJson(firstTask);
        var body = HttpRequest.BodyPublishers.ofString(json);
        var request = HttpRequest.newBuilder().uri(uri).POST(body).build();

        // Act.
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(httpTaskManager.getAllTasks());
        Assertions.assertEquals(1, httpTaskManager.getAllTasks().size());
        Assertions.assertEquals(firstTask, httpTaskManager.getTask(1).get());
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var json = gson.toJson(firstTask);
        var body = HttpRequest.BodyPublishers.ofString(json);
        var request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var updateJson = gson.toJson(updateFirstTask);
        var updateBody = HttpRequest.BodyPublishers.ofString(updateJson);
        var updateRequest = HttpRequest.newBuilder().uri(uri).PUT(updateBody).build();

        // Act.
        var response = httpClient.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(httpTaskManager.getAllTasks());
        Assertions.assertEquals(1, httpTaskManager.getAllTasks().size());
        Assertions.assertEquals(updateFirstTask, httpTaskManager.getTask(1).get());
    }

    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var json = gson.toJson(firstTask);
        var body = HttpRequest.BodyPublishers.ofString(json);
        var request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var getUri = URI.create("http://localhost:8080/tasks/task/?id=1");
        var getRequest = HttpRequest.newBuilder().uri(getUri).GET().build();

        // Act.
        var response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(json, response.body());
    }

    @Test
    void getTaskByNotExistedIdTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var json = gson.toJson(firstTask);
        var body = HttpRequest.BodyPublishers.ofString(json);
        var request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var getUri = URI.create("http://localhost:8080/tasks/task/?id=2");
        var getRequest = HttpRequest.newBuilder().uri(getUri).GET().build();

        // Act.
        var response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("", response.body());
    }

    @Test
    void getAllTaskTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var firstTaskJson = gson.toJson(firstTask);
        var firstTaskBody = HttpRequest.BodyPublishers.ofString(firstTaskJson);
        var firstTaskRequest = HttpRequest.newBuilder().uri(uri).POST(firstTaskBody).build();
        httpClient.send(firstTaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondTaskJson = gson.toJson(secondTask);
        var secondTaskBody = HttpRequest.BodyPublishers.ofString(secondTaskJson);
        var secondTaskRequest = HttpRequest.newBuilder().uri(uri).POST(secondTaskBody).build();
        httpClient.send(secondTaskRequest, HttpResponse.BodyHandlers.ofString());
        var getAllTaskRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        // Act.
        var response = httpClient.send(getAllTaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[" + firstTaskJson + "," + secondTaskJson + "]", response.body());
    }

    @Test
    void removeTaskByIdTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var firstTaskJson = gson.toJson(firstTask);
        var firstTaskBody = HttpRequest.BodyPublishers.ofString(firstTaskJson);
        var firstTaskRequest = HttpRequest.newBuilder().uri(uri).POST(firstTaskBody).build();
        httpClient.send(firstTaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondTaskJson = gson.toJson(secondTask);
        var secondTaskBody = HttpRequest.BodyPublishers.ofString(secondTaskJson);
        var secondTaskRequest = HttpRequest.newBuilder().uri(uri).POST(secondTaskBody).build();
        httpClient.send(secondTaskRequest, HttpResponse.BodyHandlers.ofString());
        var removeUri = URI.create("http://localhost:8080/tasks/task/?id=2");
        var removeRequest = HttpRequest.newBuilder().uri(removeUri).DELETE().build();

        // Act.
        var response = httpClient.send(removeRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, httpTaskManager.getAllTasks().size());
        Assertions.assertEquals(firstTask, httpTaskManager.getAllTasks().get(0));
    }

    @Test
    void removeTasksTest() throws IOException, InterruptedException {
        // Arrange.
        var uri = URI.create("http://localhost:8080/tasks/task/");
        var firstTaskJson = gson.toJson(firstTask);
        var firstTaskBody = HttpRequest.BodyPublishers.ofString(firstTaskJson);
        var firstTaskRequest = HttpRequest.newBuilder().uri(uri).POST(firstTaskBody).build();
        httpClient.send(firstTaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondTaskJson = gson.toJson(secondTask);
        var secondTaskBody = HttpRequest.BodyPublishers.ofString(secondTaskJson);
        var secondTaskRequest = HttpRequest.newBuilder().uri(uri).POST(secondTaskBody).build();
        httpClient.send(secondTaskRequest, HttpResponse.BodyHandlers.ofString());
        var removeRequest = HttpRequest.newBuilder().uri(uri).DELETE().build();

        // Act.
        var response = httpClient.send(removeRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(httpTaskManager.getAllTasks().isEmpty());
    }

    @Test
    void createSubTaskTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();

        // Act.
        var response = httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(httpTaskManager.getAllSubtasks());
        Assertions.assertEquals(1, httpTaskManager.getAllSubtasks().size());
        Assertions.assertEquals(firstSubtaskOfFirstEpic, httpTaskManager.getSubtask(3).get());
    }


    @Test
    void updateSubTaskTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var updateFirstSubtaskJson = gson.toJson(updateFirstSubtaskOfFirstEpic);
        var updateFirstSubtaskBody = HttpRequest.BodyPublishers.ofString(updateFirstSubtaskJson);
        var updateFirstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).PUT(updateFirstSubtaskBody).build();


        // Act.
        var response = httpClient.send(updateFirstSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(httpTaskManager.getAllSubtasks());
        Assertions.assertEquals(1, httpTaskManager.getAllSubtasks().size());
        Assertions.assertEquals(updateFirstSubtaskOfFirstEpic, httpTaskManager.getSubtask(3).get());
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondSubtaskJson = gson.toJson(secondSubtaskOfFirstEpic);
        var secondSubtaskBody = HttpRequest.BodyPublishers.ofString(secondSubtaskJson);
        var secondSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondSubtaskBody).build();
        httpClient.send(secondSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var getSubtaskUri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        var getSubtaskRequest = HttpRequest.newBuilder().uri(getSubtaskUri).GET().build();

        // Act.
        var response = httpClient.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(firstSubtaskJson, response.body());
    }

    @Test
    void getAllSubTaskTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondSubtaskJson = gson.toJson(secondSubtaskOfFirstEpic);
        var secondSubtaskBody = HttpRequest.BodyPublishers.ofString(secondSubtaskJson);
        var secondSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondSubtaskBody).build();
        httpClient.send(secondSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicJson = gson.toJson(secondEpic);
        var secondEpicBody = HttpRequest.BodyPublishers.ofString(secondEpicJson);
        var secondEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(secondEpicBody).build();
        httpClient.send(secondEpicRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicSubtaskJson = gson.toJson(firstSubtaskOfSecondEpic);
        var secondEpicSubtaskBody = HttpRequest.BodyPublishers.ofString(secondEpicSubtaskJson);
        var secondEpicSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondEpicSubtaskBody).build();
        httpClient.send(secondEpicSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var getSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).GET().build();

        // Act.
        var response = httpClient.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        var expectedResult = "[" + firstSubtaskJson + "," + secondSubtaskJson + "," + secondEpicSubtaskJson + "]";
        Assertions.assertEquals(expectedResult, response.body());
    }

    @Test
    void removeSubtaskByIdTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondSubtaskJson = gson.toJson(secondSubtaskOfFirstEpic);
        var secondSubtaskBody = HttpRequest.BodyPublishers.ofString(secondSubtaskJson);
        var secondSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondSubtaskBody).build();
        httpClient.send(secondSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var getSubtaskUri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        var getSubtaskRequest = HttpRequest.newBuilder().uri(getSubtaskUri).DELETE().build();

        // Act.
        var response = httpClient.send(getSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(secondSubtaskOfFirstEpic, httpTaskManager.getSubtask(4).get());
    }

    @Test
    void removeAllSubtaskTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondSubtaskJson = gson.toJson(secondSubtaskOfFirstEpic);
        var secondSubtaskBody = HttpRequest.BodyPublishers.ofString(secondSubtaskJson);
        var secondSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondSubtaskBody).build();
        httpClient.send(secondSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicJson = gson.toJson(secondEpic);
        var secondEpicBody = HttpRequest.BodyPublishers.ofString(secondEpicJson);
        var secondEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(secondEpicBody).build();
        httpClient.send(secondEpicRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicSubtaskJson = gson.toJson(firstSubtaskOfSecondEpic);
        var secondEpicSubtaskBody = HttpRequest.BodyPublishers.ofString(secondEpicSubtaskJson);
        var secondEpicSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondEpicSubtaskBody).build();
        httpClient.send(secondEpicSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var removeSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).DELETE().build();

        // Act.
        var response = httpClient.send(removeSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(httpTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();

        // Act.
        var response = httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, httpTaskManager.getAllEpics().size());
        Assertions.assertEquals(firstEpic, httpTaskManager.getEpic(1).get());
    }

    @Test
    void getEpicTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicJson = gson.toJson(secondEpic);
        var secondEpicBody = HttpRequest.BodyPublishers.ofString(secondEpicJson);
        var secondEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(secondEpicBody).build();
        httpClient.send(secondEpicRequest, HttpResponse.BodyHandlers.ofString());
        var getEpicUri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        var getEpicRequest = HttpRequest.newBuilder().uri(getEpicUri).GET().build();

        // Act.
        var response = httpClient.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(firstEpicJson, response.body());
    }

    @Test
    void getAllEpicTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicJson = gson.toJson(secondEpic);
        var secondEpicBody = HttpRequest.BodyPublishers.ofString(secondEpicJson);
        var secondEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(secondEpicBody).build();
        httpClient.send(secondEpicRequest, HttpResponse.BodyHandlers.ofString());
        var getEpicRequest = HttpRequest.newBuilder().uri(epicUri).GET().build();

        // Act.
        var response = httpClient.send(getEpicRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[" + firstEpicJson + "," + secondEpicJson + "]", response.body());
    }

    @Test
    void removeByIdTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondSubtaskJson = gson.toJson(secondSubtaskOfFirstEpic);
        var secondSubtaskBody = HttpRequest.BodyPublishers.ofString(secondSubtaskJson);
        var secondSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondSubtaskBody).build();
        httpClient.send(secondSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicJson = gson.toJson(secondEpic);
        var secondEpicBody = HttpRequest.BodyPublishers.ofString(secondEpicJson);
        var secondEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(secondEpicBody).build();
        httpClient.send(secondEpicRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicSubtaskJson = gson.toJson(firstSubtaskOfSecondEpic);
        var secondEpicSubtaskBody = HttpRequest.BodyPublishers.ofString(secondEpicSubtaskJson);
        var secondEpicSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondEpicSubtaskBody).build();
        httpClient.send(secondEpicSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var removeEpicUri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        var removeEpicRequest = HttpRequest.newBuilder().uri(removeEpicUri).DELETE().build();

        // Act.
        var response = httpClient.send(removeEpicRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, httpTaskManager.getAllEpics().size());
        Assertions.assertEquals(1, httpTaskManager.getAllSubtasks().size());
    }

    @Test
    void removeAllEpicsTest() throws IOException, InterruptedException {
        // Arrange.
        var epicUri = URI.create("http://localhost:8080/tasks/epic/");
        var firstEpicJson = gson.toJson(firstEpic);
        var firstEpicBody = HttpRequest.BodyPublishers.ofString(firstEpicJson);
        var firstEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(firstEpicBody).build();
        httpClient.send(firstEpicRequest, HttpResponse.BodyHandlers.ofString());
        var subtaskUri = URI.create("http://localhost:8080/tasks/subtask/");
        var firstSubtaskJson = gson.toJson(firstSubtaskOfFirstEpic);
        var firstSubtaskBody = HttpRequest.BodyPublishers.ofString(firstSubtaskJson);
        var firstSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(firstSubtaskBody).build();
        httpClient.send(firstSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondSubtaskJson = gson.toJson(secondSubtaskOfFirstEpic);
        var secondSubtaskBody = HttpRequest.BodyPublishers.ofString(secondSubtaskJson);
        var secondSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondSubtaskBody).build();
        httpClient.send(secondSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicJson = gson.toJson(secondEpic);
        var secondEpicBody = HttpRequest.BodyPublishers.ofString(secondEpicJson);
        var secondEpicRequest = HttpRequest.newBuilder().uri(epicUri).POST(secondEpicBody).build();
        httpClient.send(secondEpicRequest, HttpResponse.BodyHandlers.ofString());
        var secondEpicSubtaskJson = gson.toJson(firstSubtaskOfSecondEpic);
        var secondEpicSubtaskBody = HttpRequest.BodyPublishers.ofString(secondEpicSubtaskJson);
        var secondEpicSubtaskRequest = HttpRequest.newBuilder().uri(subtaskUri).POST(secondEpicSubtaskBody).build();
        httpClient.send(secondEpicSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        var removeEpicRequest = HttpRequest.newBuilder().uri(epicUri).DELETE().build();

        // Act.
        var response = httpClient.send(removeEpicRequest, HttpResponse.BodyHandlers.ofString());

        // Asserts.
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, httpTaskManager.getAllEpics().size());
        Assertions.assertEquals(0, httpTaskManager.getAllSubtasks().size());
    }

    private void detectVariable() {
        firstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.000"))
                .withDuration(4)
                .build();
        secondTask = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 08:10:00.000"))
                .withDuration(4)
                .build();
        updateFirstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.000"))
                .withDuration(4)
                .build();
        firstEpic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        secondEpic = new Epic.EpicBuilder().withId(2)
                .withName("второй эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        firstSubtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(3)
                .withName("Подзадача 1 первого эпика")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.000"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        secondSubtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(4)
                .withName("Подзадача 2 первого эпика")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:18:00.000"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        updateFirstSubtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(3)
                .withName("Подзадача 1 первого эпика")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.000"))
                .withEpicId(1)
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDuration(4)
                .build();
        firstSubtaskOfSecondEpic = new Subtask.SubtaskBuilder().withId(5)
                .withName("Подзадача 1 второго эпика")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:30:00.000"))
                .withEpicId(2)
                .withDuration(4)
                .build();
    }

}
