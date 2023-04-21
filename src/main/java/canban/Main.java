package canban;

import canban.manager.Managers;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskStatus;

import canban.utils.DateUtils;
import canban.utils.RegistryUtils;

public class Main {

    public static void main(String[] args) {
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
        taskManager.getEpic(3);
        taskManager.getEpic(3);
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

        //RegistryUtils.restoreMemory();
//        var taskManager = Managers.getInMemoryTaskManager();
//        taskManager.getAllTasks().forEach(e ->
//            System.out.println(e.toString())
//        );
//       taskManager.getAllEpics().forEach(e ->
//               System.out.println(e.toString())
//       );
//        taskManager.getAllSubtasks().forEach(e ->
//                System.out.println(e.toString())
//        );

//        System.out.println("Hist");
//        Managers.getDefaultHistory().getHistory().forEach(e -> System.out.println(e.toString()));

//        var subtask7_1 = new Subtask.SubtaskBuilder().withId(subtask7.getId())
//                .withName("Третья подзадача")
//                .withDescription("desc")
//                .withStatus(TaskStatus.IN_PROGRESS)
//                .withEpicId(4)
//                .withStartDate(subtask7.getStartTime())
//                .withDuration(10)
//                .build();
//        taskManager.updateSubtask(subtask7_1);
//
//        var task1_1 = new Task.TaskBuilder().withId(task1.getId())
//                .withName("Простая задача 1 обновлённая")
//                .withDescription("1")
//                .withStatus(TaskStatus.IN_PROGRESS)
//                .withStartDate(task1.getStartTime())
//                .withDuration(4)
//                .build();
//        taskManager.updateTask(task1_1);
//
//        var task2_1 = new Task.TaskBuilder().withId(task2.getId())
//                .withName("Простая задача 2 обновлённая")
//                .withDescription("2")
//                .withStatus(TaskStatus.IN_PROGRESS)
//                .withStartDate(task2.getStartTime())
//                .withDuration(5)
//                .build();
//        taskManager.updateTask(task2_1);
//
//        System.out.println("Вывод отсортированных по дате и id значений"); // даты можно выставить другие в объектах выше.
//        taskManager.getPrioritizedTasks().forEach(e -> System.out.println(e.toString()));
    }

}
