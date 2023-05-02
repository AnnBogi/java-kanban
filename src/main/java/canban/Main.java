package canban;

import canban.manager.Managers;
import canban.server.HttpTaskServer;
import canban.server.KVServer;
import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.utils.DateUtils;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
       // RegistryUtils.restoreMemory();

        new HttpTaskServer().start();
        new KVServer().start();
        readTaskFromBody();
        createFileMemory();
    }

    private static Task readTaskFromBody() throws IOException {
        var gson = Managers.getGson();
        var task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        var task2 = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:14:00.002"))
                .withDuration(5)
                .build();
        var list = List.of(task1, task2);
        gson.toJson(list);
        return gson.fromJson("{\"id\":1,\"name\":\"Простая задача 1 обновлённая\",\"description\":\"1\",\"status\":\"IN_PROGRESS\",\"taskType\":\"TASK\",\"duration\":4,\"startTime\":\"Apr 14, 2023, 7:10:00 AM\"}", Task.class);
    }

    private static void createFileMemory() {
        var taskManager = Managers.getFileBackedTasksManager();
        System.out.println("*** Test History ***");
        System.out.println("--- Create ---");
        var task1 = new Task.TaskBuilder().withId(taskManager.generateId())
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        var task2 = new Task.TaskBuilder().withId(taskManager.generateId())
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:14:00.002"))
                .withDuration(5)
                .build();
        var task3 = new Task.TaskBuilder().withId(taskManager.generateId())
                .withName("Простая задача 3")
                .withDescription("3")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:13:00.002"))
                .withDuration(5)
                .build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        var epic3 = new Epic.EpicBuilder().withId(taskManager.generateId())
                .withName("Первый пустой эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        var epic4 = new Epic.EpicBuilder().withId(taskManager.generateId())
                .withName("Второй эпик")
                .withDescription("С задачами")
                .build();
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);

        var subtask5 = new Subtask.SubtaskBuilder().withId(taskManager.generateId())
                .withName("Первая подзадача")
                .withDescription("desc")
                .withEpicId(3)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:24:00.002"))
                .withDuration(8)
                .build();
        var subtask6 = new Subtask.SubtaskBuilder().withId(taskManager.generateId())
                .withName("Вторая подзадача")
                .withDescription("desc")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:34:00.002"))
                .withDuration(9)
                .withEpicId(3)
                .build();
        var subtask7 = new Subtask.SubtaskBuilder().withId(taskManager.generateId())
                .withName("Третья подзадача")
                .withDescription("desc")
                .withEpicId(4)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:44:00.002"))
                .withDuration(10)
                .build();
        taskManager.createSubtask(subtask5);
        taskManager.createSubtask(subtask6);
        taskManager.createSubtask(subtask7);

        System.out.println("--- Get By Id ---");
        taskManager.getTask(1);
        taskManager.getEpic(5);
        taskManager.getEpic(5);
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(7);
        taskManager.getSubtask(6);

        System.out.println("--- Get History ---");
        var history = taskManager.getHistory();
        System.out.println(history);

        System.out.println("--- Remove from history ---");
        taskManager.removeHistoryById(4);
        taskManager.removeEpicById(3);

        var historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);
    }


}