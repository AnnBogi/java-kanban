import java.util.List;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import manager.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        var taskManager = new InMemoryTaskManager();

        System.out.println("\nСписок вначале:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Создание простых задач.
         */
        var task1 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 1").withDescription("1").build();
        var task2 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 2").withDescription("2").build();
        var task3 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 3").withDescription("3").build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println("\nСписок после добавления задач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Обновление простых задач.
         */
        task3.setName("Обновлённая задача 3");
        task3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task3);
        System.out.println("\nСписок после обновления задач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Предоставление задачи по идентификатору.
         */
        System.out.println("\nПредоставление простой задачи:");
        System.out.println(taskManager.getTaskById(2));

        /*
         * Удаление простой задачи.
         */
        taskManager.removeTaskById(0);
        taskManager.removeTaskById(1);
        System.out.println("\nСписок после удаления задачи:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Создание подзадачи без эпика.
         */
        var subtaskForNonCreatedEpic = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Несуществующий эпик").withDescription("desc").withEpicId(4).build();
        taskManager.createSubtask(subtaskForNonCreatedEpic);
        var subtaskWithoutEpicId = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Несуществующий эпик").withDescription("desc").build();
        taskManager.createSubtask(subtaskWithoutEpicId);

        /*
         * Создание эпиков.
         */
        var epic6 = new Epic.EpicBuilder().withId(taskManager.generateId()).withName("Первый пустой эпик").withDescription("Сюда ничего не добавится").build();
        var epic7 = new Epic.EpicBuilder().withId(taskManager.generateId()).withName("Второй эпик").withDescription("С задачами").build();
        taskManager.createEpic(epic6);
        taskManager.createEpic(epic7);
        System.out.println("\nСписок после добавления эпиков:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Добавление подзадач.
         */
        var subtask8 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Первая подзадача").withDescription("desc").withEpicId(7).build();
        taskManager.createSubtask(subtaskForNonCreatedEpic);
        var subtask9 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Вторая подзадача").withDescription("desc").withEpicId(7).build();
        var subtask10 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Третья подзадача").withDescription("desc").withEpicId(7).build();
        taskManager.createSubtask(subtask8);
        taskManager.createSubtask(subtask9);
        taskManager.createSubtask(subtask10);
        System.out.println("\nСписок после добавления подзадач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Обновление подзадачи.
         */
        subtask8.setName("Обновлённая задача 8");
        subtask8.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask8);
        System.out.println("\nСписок после обновления подзадач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Обновление эпика без статуса
         */
        epic7.setName("Обновлённый эпик 7");
        epic7.setStatus(TaskStatus.DONE); // НЕ ОБНОВИТСЯ.
        taskManager.updateEpic(epic7);
        System.out.println("\nСписок после обновления эпика:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Обновление статуса эпика как выполненного.
         */
        subtask8.setName("Обновлённая задача 8");
        subtask8.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask8);
        subtask9.setName("Обновлённая задача 9");
        subtask9.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask9);
        subtask10.setName("Обновлённая задача 10");
        subtask10.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask10);
        System.out.println("\nСписок после обновления эпика:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Получение списка подзадач эпика.
         */
        var unknownEpic = new Epic.EpicBuilder().withId(0).build();
        taskManager.getSubtaskOfEpic(unknownEpic);
        var subTasksOfEpic = taskManager.getSubtaskOfEpic(epic7);
        System.out.println("\nСписок подзадач:");
        printList(subTasksOfEpic);

        /*
         * Удаление подзадачи.
         */
        taskManager.removeSubtaskById(10);
        System.out.println("\nСписок после удаления подзадачи:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Добавление подзадачи.
         */
        var subtask11 = new Subtask.SubtaskBuilder()
                .withId(taskManager.generateId())
                .withName("")
                .withDescription("")
                .withEpicId(7)
                .build();
        taskManager.createSubtask(subtask11);
        System.out.println("\nСписок после добавления подзадачи:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Удаление подзадач.
         */
        taskManager.removeAllSubtasks();
        System.out.println("\nСписок после удаления подзадач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Добавление подзадач.
         */
        var subtask12 = new Subtask.SubtaskBuilder()
                .withId(taskManager.generateId())
                .withName("")
                .withDescription("")
                .withEpicId(7)
                .build();
        var subtask13 = new Subtask.SubtaskBuilder()
                .withId(taskManager.generateId())
                .withName("")
                .withDescription("")
                .withEpicId(6)
                .build();
        taskManager.createSubtask(subtask12);
        taskManager.createSubtask(subtask13);
        System.out.println("\nСписок после добавления подзадач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Удаление эпика.
         */
        taskManager.removeEpicById(0);
        System.out.println("\nСписок после удаления эпика:");
        taskManager.removeEpicById(7);
        System.out.println("\nСписок после удаления эпика:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Добавление эпика.
         */
        var epic14 = new Epic.EpicBuilder()
                .withId(taskManager.generateId())
                .withName("")
                .withDescription("")
                .build();
        taskManager.createEpic(epic14);
        System.out.println("\nСписок после добавления эпика:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Удаление эпиков.
         */
        taskManager.removeAllEpics();
        System.out.println("\nСписок после удаления эпиков:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());

        /*
         * Удаление задач.
         */
        taskManager.removeAllTasks();
        System.out.println("\nСписок после удаления всех задач:");
        printList(taskManager.getAllTasks());
        printList(taskManager.getAllEpics());
        printList(taskManager.getAllSubtasks());
    }

    private static void printList(List<? extends Task> taskList) {
        taskList.forEach(task -> System.out.println(task.toString()));
    }

}