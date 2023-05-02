package canban.server;

import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.tasks.Task;
import canban.utils.DateUtils;
import canban.manager.Managers;

class KVTaskClientTest {
    private KVTaskClient kvTaskClient;
    private KVServer kvServer;
    private Task task;

    @BeforeEach
    void setUp() throws IOException {
        kvTaskClient = new KVTaskClient();
        kvServer = new KVServer();
        kvServer.start();

        task = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @Test
    void registerTest() throws IOException, InterruptedException {
        var currentTime = new Date();

        var result = kvTaskClient.register();

        Assertions.assertEquals(200, result.statusCode());
        Assertions.assertTrue(currentTime.after(new Date(Long.parseLong((String) result.body()))));
    }

    @Test
    void saveTest() throws IOException, InterruptedException {
        var token = kvTaskClient.register();

        var result = kvTaskClient.save("1", token.body().toString(), task);

        Assertions.assertEquals(200, result.statusCode());
        Assertions.assertTrue( result.body().toString().isEmpty());
    }

    @Test
    void saveDebugTest() throws IOException, InterruptedException {
        var result = kvTaskClient.save("1", "DEBUG", task);

        Assertions.assertEquals(200, result.statusCode());
        Assertions.assertTrue( result.body().toString().isEmpty());
    }

    @Test
    void saveIncorrectTest() throws IOException, InterruptedException {
        var result = kvTaskClient.save("12", "test", task);

        Assertions.assertEquals(403, result.statusCode());
        Assertions.assertTrue(result.body().toString().isEmpty());
    }

    @Test
    void loadTest() throws IOException, InterruptedException {
        var token = kvTaskClient.register();
        kvTaskClient.save("1", token.body().toString(), task);

        var result = kvTaskClient.load("1", token.body().toString());

        Assertions.assertEquals(200, result.statusCode());
        Assertions.assertEquals(Managers.getGson().toJson(task), result.body().toString());
    }

    @Test
    void loadIncorrectTest() throws IOException, InterruptedException {
        var result = kvTaskClient.load("1", "test");

        Assertions.assertEquals(403, result.statusCode());
        Assertions.assertTrue(result.body().toString().isEmpty());
    }

    @Test
    void loadDebugTest() throws IOException, InterruptedException {
        kvTaskClient.save("1", "DEBUG", task);

        var result = kvTaskClient.load("1", "DEBUG");

        Assertions.assertEquals(200, result.statusCode());
        Assertions.assertEquals(Managers.getGson().toJson(task), result.body().toString());
    }

    @Test
    void loadNotExistedTest() throws IOException, InterruptedException {
        kvTaskClient.save("1", "DEBUG", task);

        var result = kvTaskClient.load("2", "DEBUG");

        Assertions.assertEquals(404, result.statusCode());
        Assertions.assertTrue(result.body().toString().isEmpty());
    }

}