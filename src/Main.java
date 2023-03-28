import java.util.List;

import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
        System.out.println("*** Test History ***");
        System.out.println("--- Create ---");
        var task1 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 1").withDescription("1").build();
        var task2 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 2").withDescription("2").build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        var epic6 = new Epic.EpicBuilder().withId(taskManager.generateId()).withName("Первый пустой эпик").withDescription("Сюда ничего не добавится").build();
        var epic7 = new Epic.EpicBuilder().withId(taskManager.generateId()).withName("Второй эпик").withDescription("С задачами").build();
        taskManager.createEpic(epic6);
        taskManager.createEpic(epic7);

        var subtask8 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Первая подзадача").withDescription("desc").withEpicId(3).build();
        var subtask9 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Вторая подзадача").withDescription("desc").withEpicId(3).build();
        var subtask10 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Третья подзадача").withDescription("desc").withEpicId(3).build();
        taskManager.createSubtask(subtask8);
        taskManager.createSubtask(subtask9);
        taskManager.createSubtask(subtask10);

        System.out.println("--- Get By Id ---");
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getEpic(3);
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);

        System.out.println("--- Get History ---");
        List<Task> history = taskManager.getHistory();
        System.out.println(history);

        System.out.println("--- Remove from history ---");
        taskManager.remove(1);
        taskManager.removeEpicById(3);

        List<Task> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);

    }
}