package canban.manager;

import java.util.Optional;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import canban.tasks.Epic;
import canban.tasks.TaskStatus;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.utils.ManagerSaveException;
import canban.utils.RegistryUtils;
import canban.utils.DateUtils;

class FileBackedTasksManagerTest {

    private FileBackedTasksManager fileBackedTasksManager;
    private Task task1;
    private Task task1_1;
    private Epic epic1;
    private Epic epic1_1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask1_1;

    @BeforeEach
    void setUp() {
        fileBackedTasksManager = Mockito.spy(new FileBackedTasksManager());
        detectVariable();
    }

    // Тестирование задачи.
    @Test
    void getAllTasksTest() {
        fileBackedTasksManager.createTask(task1);

        var result = fileBackedTasksManager.getAllTasks();

        Mockito.verify(fileBackedTasksManager).getAllTasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
        Assertions.assertEquals(task1, result.get(0));
    }

    @Test
    void getAllTasks_EmptyTest() {
        var result = fileBackedTasksManager.getAllTasks();

        Mockito.verify(fileBackedTasksManager).getAllTasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getTask_isExist() {
        fileBackedTasksManager.createTask(task1);
        var result = fileBackedTasksManager.getTask(1);

        Mockito.verify(fileBackedTasksManager).getTask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
        Assertions.assertEquals(Optional.of(task1), result);
    }

    @Test
    void getTask_isNotExist() {
        var result = fileBackedTasksManager.getTask(1);

        Mockito.verify(fileBackedTasksManager).getTask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void removeAllTasksTest() {
        fileBackedTasksManager.createTask(task1);

        fileBackedTasksManager.removeAllTasks();

        Assertions.assertTrue(fileBackedTasksManager.getAllTasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeAllTasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void removeTaskByIdTest() {
        fileBackedTasksManager.createTask(task1);

        fileBackedTasksManager.removeTaskById(1);

        Assertions.assertTrue(fileBackedTasksManager.getAllTasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeTaskById(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void createTaskTest() {
        fileBackedTasksManager.createTask(task1);

        Mockito.verify(fileBackedTasksManager).createTask(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(1)).save();
        Assertions.assertEquals(task1, fileBackedTasksManager.getTask(1).get());
    }

    @Test
    void updateTaskTest() {
        fileBackedTasksManager.createTask(task1);

        fileBackedTasksManager.updateTask(task1_1);

        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
        Assertions.assertEquals(task1_1, fileBackedTasksManager.getTask(1).get());
    }

    // Тестирование подзадач.
    @Test
    void getAllSubTasksTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);

        var result = fileBackedTasksManager.getAllSubtasks();

        Mockito.verify(fileBackedTasksManager).getAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
        Assertions.assertEquals(subtask1, result.get(0));
    }

    @Test
    void getAllSubTasks_emptyMapTest() {
        var result = fileBackedTasksManager.getAllSubtasks();

        Mockito.verify(fileBackedTasksManager).getAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getSubTaskTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);

        var result = fileBackedTasksManager.getSubtask(2);

        Mockito.verify(fileBackedTasksManager).getSubtask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
        Assertions.assertEquals(Optional.of(subtask1), result);
    }

    @Test
    void getSubTask_isNotExist() {
        var result = fileBackedTasksManager.getSubtask(2);

        Mockito.verify(fileBackedTasksManager).getSubtask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void removeAllSubTasksTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);

        fileBackedTasksManager.removeAllSubtasks();

        Assertions.assertTrue(fileBackedTasksManager.getAllSubtasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
    }

    @Test
    void removeSubTaskByIdTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);

        fileBackedTasksManager.removeSubtaskById(2);

        Assertions.assertTrue(fileBackedTasksManager.getAllSubtasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeSubtaskById(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
    }

    @Test
    void createSubTaskTest() {
        Mockito.when(fileBackedTasksManager.getEpic(Mockito.any())).thenReturn(Optional.of(epic1));

        fileBackedTasksManager.createSubtask(subtask1);

        Mockito.verify(fileBackedTasksManager).createSubtask(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void updateSubTaskTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);

        fileBackedTasksManager.updateSubtask(subtask1_1);

        Mockito.verify(fileBackedTasksManager).updateSubtask(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
        Assertions.assertEquals(subtask1_1, fileBackedTasksManager.getSubtask(2).get());
    }

    // Тесты эпиков.
    @Test
    void getAllEpics_MapTest() {
        fileBackedTasksManager.createEpic(epic1);

        Mockito.verify(fileBackedTasksManager).createEpic(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(1)).save();
        Assertions.assertEquals(1, fileBackedTasksManager.getAllEpics().size());
    }

    @Test
    void getAllEpics_emptyMapTest() {
        var result = fileBackedTasksManager.getAllEpics();
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
    }

    @Test
    void getEpicTest() {
        fileBackedTasksManager.createEpic(epic1);

        var result = fileBackedTasksManager.getEpic(1);

        Mockito.verify(fileBackedTasksManager).getEpic(Mockito.any());
        Assertions.assertEquals(epic1, result.get());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void removeAllEpicsTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createEpic(epic2);

        fileBackedTasksManager.removeAllEpics();

        Mockito.verify(fileBackedTasksManager).removeAllEpics();
        Assertions.assertTrue(fileBackedTasksManager.getAllEpics().isEmpty());
        Mockito.verify(fileBackedTasksManager, Mockito.times(3)).save();
    }

    @Test
    void removeEpicByIdTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createEpic(epic2);


        Assertions.assertEquals(2, fileBackedTasksManager.getAllEpics().size());

        fileBackedTasksManager.removeEpicById(1);

        Assertions.assertEquals(1, fileBackedTasksManager.getAllEpics().size());
        Mockito.verify(fileBackedTasksManager).removeEpicById(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
    }

    @Test
    void createEpicTest() {
        Assertions.assertTrue(fileBackedTasksManager.getAllEpics().isEmpty());

        fileBackedTasksManager.createEpic(epic1);

        Assertions.assertEquals(1, fileBackedTasksManager.getAllEpics().size());

        Assertions.assertEquals(epic1, fileBackedTasksManager.getEpic(1).get());
        Mockito.verify(fileBackedTasksManager).createEpic(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(3)).save();
    }

    @Test
    void updateEpicTest() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.updateEpic(epic1_1);

        Assertions.assertEquals(TaskStatus.NEW, fileBackedTasksManager.getEpic(1).get().getStatus());
        Mockito.verify(fileBackedTasksManager).updateEpic(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
    }

    @Test
    void getSubtaskOfEpic_test() {
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);

        var result = fileBackedTasksManager.getAllSubtasks();

        Assertions.assertEquals(subtask1, result.get(0));
        Mockito.verify(fileBackedTasksManager).getAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
    }

    @Test
    void saveTest() {
        try (var mock = Mockito.mockStatic(RegistryUtils.class)){
            fileBackedTasksManager.save();

            mock.verify(() -> RegistryUtils.writeToMemoryFile(Mockito.any(), Mockito.any()));
        }
    }

    @Test()
    void saveThrowsTest() {
        try (var mock = Mockito.mockStatic(RegistryUtils.class)) {
            mock.when(() -> {
                RegistryUtils.writeToMemoryFile(Mockito.any(), Mockito.any());
            }).thenThrow(IOException.class);

            var result  = Assertions.assertThrows(ManagerSaveException.class, () -> fileBackedTasksManager.save()) ;

            Assertions.assertTrue(result.getMessage().startsWith("Failed to save tasks and history to memory file: "));
            mock.verify(() -> RegistryUtils.writeToMemoryFile(Mockito.any(), Mockito.any()));
        }
    }

    private void detectVariable() {
        task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        task1_1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        epic1 = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        epic1_1 = new Epic.EpicBuilder().withId(1)
                .withName("Первый эпик")
                .withStatus(TaskStatus.DONE)
                .withDescription("Сюда ничего не добавится")
                .build();
        epic2 = new Epic.EpicBuilder().withId(3)
                .withName("второй эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        subtask1 = new Subtask.SubtaskBuilder().withId(2)
                .withName("Подзадача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        subtask1_1 = new Subtask.SubtaskBuilder().withId(2)
                .withName("Подзадача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withEpicId(1)
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDuration(4)
                .build();
    }

}
