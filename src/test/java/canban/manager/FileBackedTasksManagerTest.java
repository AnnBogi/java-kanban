package canban.manager;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskStatus;
import canban.utils.DateUtils;
import canban.utils.ManagerSaveException;
import canban.utils.RegistryUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

class FileBackedTasksManagerTest {

    private FileBackedTasksManager fileBackedTasksManager;

    private Task firstTask;
    private Task updateFirstTask;
    private Epic firstEpic;
    private Epic updateFirstEpic;
    private Epic secondEpic;
    private Subtask firstSubTask;
    private Subtask updateFirstSubtask;

    @BeforeEach
    void setUp() {
        fileBackedTasksManager = Mockito.spy(new FileBackedTasksManager());
        detectVariable();
    }

    // Тестирование задачи.
    @Test
    void getAllTasksTest() {
        // Arrange.
        fileBackedTasksManager.createTask(firstTask);

        // Act.
        var result = fileBackedTasksManager.getAllTasks();

        // Asserts.
        Mockito.verify((InMemoryTaskManager) fileBackedTasksManager).getAllTasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
        Assertions.assertEquals(firstTask, result.get(0));
    }

    @Test
    void getAllTasks_EmptyTest() {
        // Act.
        var result = fileBackedTasksManager.getAllTasks();

        // Asserts.
        Mockito.verify((InMemoryTaskManager) fileBackedTasksManager).getAllTasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getTask_isExist() {
        // Arrange.
        fileBackedTasksManager.createTask(firstTask);

        // Act.
        var result = fileBackedTasksManager.getTask(1);

        // Asserts.
        Mockito.verify((InMemoryTaskManager) fileBackedTasksManager).getTask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
        Assertions.assertEquals(Optional.of(firstTask), result);
    }

    @Test
    void getTask_isNotExist() {
        // Act.
        var result = fileBackedTasksManager.getTask(1);

        // Asserts.
        Mockito.verify((InMemoryTaskManager) fileBackedTasksManager).getTask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void removeAllTasksTest() {
        // Arrange.
        fileBackedTasksManager.createTask(firstTask);

        // Act.
        fileBackedTasksManager.removeAllTasks();

        // Asserts.
        Assertions.assertTrue(fileBackedTasksManager.getAllTasks().isEmpty());
        Mockito.verify((InMemoryTaskManager) fileBackedTasksManager).removeAllTasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void removeTaskByIdTest() {
        // Arrange.
        fileBackedTasksManager.createTask(firstTask);

        // Act.
        fileBackedTasksManager.removeTaskById(1);

        // Asserts.
        Assertions.assertTrue(fileBackedTasksManager.getAllTasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeTaskById(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void createTaskTest() {
        // Act.
        fileBackedTasksManager.createTask(firstTask);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).createTask(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(1)).save();
        Assertions.assertEquals(firstTask, fileBackedTasksManager.getTask(1).get());
    }

    @Test
    void updateTaskTest() {
        // Arrange.
        fileBackedTasksManager.createTask(firstTask);

        // Act.
        fileBackedTasksManager.updateTask(updateFirstTask);

        // Asserts.
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
        Assertions.assertEquals(updateFirstTask, fileBackedTasksManager.getTask(1).get());
    }

    // Тестирование подзадач.
    @Test
    void getAllSubTasksTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Act.
        var result = fileBackedTasksManager.getAllSubtasks();

        // Asserts.
        Mockito.verify(fileBackedTasksManager).getAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
        Assertions.assertEquals(firstSubTask, result.get(0));
    }

    @Test
    void getAllSubTasks_emptyMapTest() {
        // Act.
        var result = fileBackedTasksManager.getAllSubtasks();

        // Asserts.
        Mockito.verify(fileBackedTasksManager).getAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getSubTaskTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Act.
        var result = fileBackedTasksManager.getSubtask(2);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).getSubtask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
        Assertions.assertEquals(Optional.of(firstSubTask), result);
    }

    @Test
    void getSubTask_isNotExist() {
        // Act.
        var result = fileBackedTasksManager.getSubtask(2);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).getSubtask(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void removeAllSubTasksTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Act.
        fileBackedTasksManager.removeAllSubtasks();

        // Asserts.
        Assertions.assertTrue(fileBackedTasksManager.getAllSubtasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
    }

    @Test
    void removeSubTaskByIdTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Act.
        fileBackedTasksManager.removeSubtaskById(2);

        // Asserts.
        Assertions.assertTrue(fileBackedTasksManager.getAllSubtasks().isEmpty());
        Mockito.verify(fileBackedTasksManager).removeSubtaskById(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
    }

    @Test
    void createSubTaskTest() {
        // Arrange.
        Mockito.when(fileBackedTasksManager.getEpic(Mockito.any())).thenReturn(Optional.of(firstEpic));

        // Act.
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).createSubtask(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void updateSubTaskTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Act.
        fileBackedTasksManager.updateSubtask(updateFirstSubtask);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).updateSubtask(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(5)).save();
        Assertions.assertEquals(updateFirstSubtask, fileBackedTasksManager.getSubtask(2).get());
    }

    // Тесты эпиков.
    @Test
    void getAllEpics_MapTest() {
        // Act.
        fileBackedTasksManager.createEpic(firstEpic);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).createEpic(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(1)).save();
        Assertions.assertEquals(1, fileBackedTasksManager.getAllEpics().size());
    }

    @Test
    void getAllEpics_emptyMapTest() {
        // Act.
        var result = fileBackedTasksManager.getAllEpics();

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(fileBackedTasksManager, Mockito.times(0)).save();
    }

    @Test
    void getEpicTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);

        // Act.
        var result = fileBackedTasksManager.getEpic(1);

        // Asserts.
        Mockito.verify(fileBackedTasksManager).getEpic(Mockito.any());
        Assertions.assertEquals(firstEpic, result.get());
        Mockito.verify(fileBackedTasksManager, Mockito.times(2)).save();
    }

    @Test
    void removeAllEpicsTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createEpic(secondEpic);

        // Act.
        fileBackedTasksManager.removeAllEpics();

        // Asserts.
        Mockito.verify(fileBackedTasksManager).removeAllEpics();
        Assertions.assertTrue(fileBackedTasksManager.getAllEpics().isEmpty());
        Mockito.verify(fileBackedTasksManager, Mockito.times(3)).save();
    }

    @Test
    void removeEpicByIdTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createEpic(secondEpic);

        // Act.
        fileBackedTasksManager.removeEpicById(1);

        // Asserts.
        Assertions.assertEquals(1, fileBackedTasksManager.getAllEpics().size());
        Mockito.verify(fileBackedTasksManager).removeEpicById(Mockito.anyInt());
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
    }

    @Test
    void createEpicTest() {
        // Act.
        fileBackedTasksManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertEquals(1, fileBackedTasksManager.getAllEpics().size());
        Assertions.assertEquals(firstEpic, fileBackedTasksManager.getEpic(1).get());
        Mockito.verify(fileBackedTasksManager).createEpic(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(3)).save();
    }

    @Test
    void updateEpicTest() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);

        // Act.
        fileBackedTasksManager.updateEpic(updateFirstEpic);

        // Asserts.
        Assertions.assertEquals(TaskStatus.NEW, fileBackedTasksManager.getEpic(1).get().getStatus());
        Mockito.verify(fileBackedTasksManager).updateEpic(Mockito.any());
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
    }

    @Test
    void getSubtaskOfEpic_test() {
        // Arrange.
        fileBackedTasksManager.createEpic(firstEpic);
        fileBackedTasksManager.createSubtask(firstSubTask);

        // Act.
        var result = fileBackedTasksManager.getAllSubtasks();

        // Asserts.
        Assertions.assertEquals(firstSubTask, result.get(0));
        Mockito.verify(fileBackedTasksManager).getAllSubtasks();
        Mockito.verify(fileBackedTasksManager, Mockito.times(4)).save();
    }

    @Test
    void saveTest() {
        try (var mock = Mockito.mockStatic(RegistryUtils.class)){
            // Act.
            fileBackedTasksManager.save();

            // Asserts.
            mock.verify(() -> RegistryUtils.writeToMemoryFile(Mockito.any(), Mockito.any()));
        }
    }

    @Test()
    void saveThrowsTest() {
        try (var mock = Mockito.mockStatic(RegistryUtils.class)) {
            // Arrange.
            mock.when(() -> {
                RegistryUtils.writeToMemoryFile(Mockito.any(), Mockito.any());
            }).thenThrow(IOException.class);

            // Act.
            var result  = Assertions.assertThrows(ManagerSaveException.class, () -> fileBackedTasksManager.save()) ;

            // Asserts.
            Assertions.assertTrue(result.getMessage().startsWith("Failed to save tasks and history to memory file: "));
            mock.verify(() -> RegistryUtils.writeToMemoryFile(Mockito.any(), Mockito.any()));
        }
    }

    private void detectVariable() {
        firstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        updateFirstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        firstEpic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        updateFirstEpic = new Epic.EpicBuilder().withId(1)
                .withName("Первый эпик")
                .withStatus(TaskStatus.DONE)
                .withDescription("Сюда ничего не добавится")
                .build();
        secondEpic = new Epic.EpicBuilder().withId(3)
                .withName("второй эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        firstSubTask = new Subtask.SubtaskBuilder().withId(2)
                .withName("Подзадача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        updateFirstSubtask = new Subtask.SubtaskBuilder().withId(2)
                .withName("Подзадача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withEpicId(1)
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDuration(4)
                .build();
    }

}