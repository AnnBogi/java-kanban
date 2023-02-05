import java.util.List;

import entity.Task;
import entity.TaskStatus;
import service.TaskService;

public class Main {

    public static void main(String[] args) {

        var taskService = new TaskService();

        System.out.println("\nСписок вначале:");
        printList(taskService.getAllTask());

        /*
         * Создание простых задач.
         */
        taskService.createTask("Простая задача 1", "1");
        taskService.createTask("Простая задача 2", "2");
        taskService.createTask("Простая задача 3", "3");
        System.out.println("\nСписок после добавления задач:");
        printList(taskService.getAllTask());
        /*
         * Обновление простых задач.
         */
        var task3 = taskService.getTaskById(3).get();
        task3.setName("Обновлённая задача 3");
        task3.setStatus(TaskStatus.DONE);
        taskService.updateTask(task3);
        System.out.println("\nСписок после обновления задач:");
        printList(taskService.getAllTask());
        /*
         * Предоставление задачи по идентификатору.
         */
        System.out.println("\nПредоставление простой задачи:");
        System.out.println(taskService.getTaskById(2).toString());
        /*
         * Удаление простой задачи.
         */
        taskService.removeById(0);
        taskService.removeById(1);
        System.out.println("\nСписок после удаления задачи:");
        printList(taskService.getAllTask());
        /*
         * Создание подзадачи без эпика.
         */
        taskService.createSubtask("Несуществующий эпик", "", 4);

        /*
         * Создание эпиков.
         */
        taskService.createEpic("Первый пустой эпик", "Сюда ничего не добавится");
        taskService.createEpic("Второй эпик", "С задачами");
        System.out.println("\nСписок после добавления эпиков:");
        printList(taskService.getAllTask());

        /*
         * Добавление подзадач.
         */
        taskService.createSubtask("Первая подзадача", "", 5);
        taskService.createSubtask("Вторая подзадача", "", 5);
        taskService.createSubtask("Третья подзадача", "", 5);

        System.out.println("\nСписок после добавления подзадач:");
        printList(taskService.getAllTask());

        /*
         * Обновление подзадачи.
         */
        var task8 = taskService.getTaskById(8).get();
        task8.setName("Обновлённая задача 8");
        task8.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTask(task8);
        System.out.println("\nСписок после обновления подзадач:");
        printList(taskService.getAllTask());

        /**
         * Обновление эпика без статуса
         */
        var task5 = taskService.getTaskById(5).get();
        task5.setName("Обновлённая задача 5");
        task5.setStatus(TaskStatus.DONE); // НЕ ОБНОВИТСЯ.
        taskService.updateTask(task5);
        System.out.println("\nСписок после обновления эпика:");
        printList(taskService.getAllTask());

        /**
         * Обновление статуса эпика как выполненного.
         */
        var task6 = taskService.getTaskById(6).get();
        task6.setName("Обновлённая задача 6");
        task6.setStatus(TaskStatus.DONE); // НЕ ОБНОВИТСЯ.
        taskService.updateTask(task6);
        var task7 = taskService.getTaskById(7).get();
        task7.setName("Обновлённая задача 7");
        task7.setStatus(TaskStatus.DONE); // НЕ ОБНОВИТСЯ.
        taskService.updateTask(task7);
        var task8_1 = taskService.getTaskById(8).get();
        task8_1.setName("Обновлённая задача 8");
        task8_1.setStatus(TaskStatus.DONE); // НЕ ОБНОВИТСЯ.
        taskService.updateTask(task8_1);
        System.out.println("\nСписок после обновления эпика:");
        printList(taskService.getAllTask());

        /*
         * Получение списка подзадач эпика.
         */
        taskService.getSubTasksOfEpic(0);
        taskService.getSubTasksOfEpic(1);
        var subTasksOfEpic = taskService.getSubTasksOfEpic(5);
        System.out.println("\nСписок подзадач:");
        printList(subTasksOfEpic);

        /*
         * Удалдение подзадач.
         */
        taskService.removeById(8);
        taskService.removeById(7);
        taskService.removeById(6);
        System.out.println("\nСписок после удаления подзадач:");
        printList(taskService.getAllTask());

        /*
         * Добавление подзадач.
         */
        taskService.createSubtask("3", "3", 4);
        taskService.createSubtask("4", "4", 5);
        System.out.println("\nСписок после добавления подзадач:");
        printList(taskService.getAllTask());

        /*
         * Удаление эпика.
         */
        taskService.removeById(4);
        System.out.println("\nСписок после удаления эпика:");
        printList(taskService.getAllTask());

        /*
         * Удаление всех задач.
         */
        taskService.getAllTask();
        System.out.println("\nСписок после удаления всех задач:");
        printList(taskService.getAllTask());
    }

    private static void printList(List<? extends Task> taskList) {
        taskList.forEach(task ->
                System.out.println(task.toString())
        );
    }

}