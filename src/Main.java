import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        var taskManager = Managers.getFileBackedTasksManager();
        System.out.println("*** Test History ***");
        System.out.println("--- Create ---");
        var task1 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 1").withDescription("1").build();
        var task2 = new Task.TaskBuilder().withId(taskManager.generateId()).withName("Простая задача 2").withDescription("2").build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        var epic3 = new Epic.EpicBuilder().withId(taskManager.generateId()).withName("Первый пустой эпик").withDescription("Сюда ничего не добавится").build();
        var epic4 = new Epic.EpicBuilder().withId(taskManager.generateId()).withName("Второй эпик").withDescription("С задачами").build();
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);

        var subtask5 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Первая подзадача").withDescription("desc").withEpicId(3).build();
        var subtask6 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Вторая подзадача").withDescription("desc").withEpicId(3).build();
        var subtask7 = new Subtask.SubtaskBuilder().withId(taskManager.generateId()).withName("Третья подзадача").withDescription("desc").withEpicId(4).build();
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
        taskManager.remove(1);
        taskManager.removeEpicById(3);

        var historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);

//        RegistryUtils.restoreMemory();
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


    }
}