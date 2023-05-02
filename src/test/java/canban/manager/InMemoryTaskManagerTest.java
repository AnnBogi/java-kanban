package canban.manager;

import java.util.Optional;
import java.util.Map;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskStatus;
import canban.utils.DateUtils;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager inMemoryTaskManager;
    private InMemoryHistoryManager inMemoryHistoryManagerMock;

    private Task firstTask;
    private Task updateFirstTask;
    private Task secondTask;
    private Task thirdTask;
    private Epic firstEpic;
    private Epic updateFirstEpic;
    private Epic secondEpic;
    private Subtask subtaskOfFirstEpic;
    private Subtask secondSubtaskOfFirstEpic;
    private Subtask updateSubtaskOfFirstEpic;
    private Subtask subtaskOfSecondEpic;

    private static MockedStatic<Managers> managersMockedStatic;

    @BeforeAll
    static void mainSetup() {
        managersMockedStatic = Mockito.mockStatic(Managers.class);
    }

    @AfterAll
    static void tearDown() {
        managersMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Mockito.spy(new InMemoryTaskManager());
        inMemoryHistoryManagerMock = Mockito.mock(InMemoryHistoryManager.class);
        detectVariable();
    }

    @Test
    void generateIdTest() throws NoSuchFieldException {
        // Arrange.
        var modifiersField = InMemoryTaskManager.class.getDeclaredField("idCounter");
        modifiersField.setAccessible(true);

        // Act.
        var resultsValue = inMemoryTaskManager.generateId();

        // Asserts.
        Assertions.assertEquals(1, resultsValue);
    }


    // Тесты для задач.
    @Test
    void getAllTasksTest() {
        // Arrange.
        inMemoryTaskManager.createTask(firstTask);

        // Asserts.
        Assertions.assertEquals(1, inMemoryTaskManager.getAllTasks().size());
    }

    @Test
    void getAllTasks_EmptyTest() {
        // Asserts.
        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
    }

    @Test
    void getTask_isExist() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var modifiersField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        modifiersField.setAccessible(true);
        modifiersField.set(inMemoryTaskManager, Map.of(1, firstTask));

        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        // Act.
        var result = inMemoryTaskManager.getTask(1);

        // Asserts.
        Assertions.assertEquals(firstTask, result.get());
        Mockito.verify(inMemoryHistoryManagerMock).add(Mockito.any());
    }

    @Test
    void getTask_isNotExist() {
        // Arrange.
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        // Act.
        var result = inMemoryTaskManager.getTask(1);

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(inMemoryHistoryManagerMock).add(Optional.empty());
    }

    @Test
    void removeAllTasksTest() throws NoSuchFieldException {
        // Arrange.
        inMemoryTaskManager.createTask(firstTask);
        inMemoryTaskManager.createTask(secondTask);
        inMemoryTaskManager.createTask(thirdTask);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.removeAllTasks();

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty()),
                () -> Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty())
        );
    }

    @Test
    void removeTaskByIdTest() throws NoSuchFieldException {
        // Arrange.
        inMemoryTaskManager.createTask(firstTask);
        inMemoryTaskManager.createTask(secondTask);
        inMemoryTaskManager.createTask(thirdTask);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.removeTaskById(2);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, inMemoryTaskManager.getAllTasks().size()),
                () -> Assertions.assertFalse(inMemoryTaskManager.getAllTasks().contains(secondTask)),
                () -> Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size()),
                () -> Assertions.assertEquals(2, ((Map) taskMapField.get(inMemoryTaskManager)).size())
        );
    }

    @Test
    void createTaskTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.createTask(firstTask);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, inMemoryTaskManager.getAllTasks().size()),
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllTasks().contains(firstTask)),
                () -> Assertions.assertEquals(1, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size()),
                () -> Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size())
        );
    }

    @Test
    void updateTask_notExistedTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.updateTask(firstTask);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty()),
                () -> Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty())
        );
    }

    @Test
    void updateTaskTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        inMemoryTaskManager.createTask(firstTask);

        // Act.
        inMemoryTaskManager.updateTask(updateFirstTask);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getTask(1).get().getStatus()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(updateFirstTask))
        );
    }

    // Тесты для подзадач.
    @Test
    void getAllSubTasksTest() {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);

        // Asserts.
        Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size());
    }

    @Test
    void getAllSubTasks_emptyMapTest() {
        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void getSubTaskTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);

        var modifiersField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        modifiersField.setAccessible(true);
        modifiersField.set(inMemoryTaskManager, Map.of(2, subtaskOfFirstEpic));

        // Act.
        var result = inMemoryTaskManager.getSubtask(2);

        // Asserts.
        Assertions.assertEquals(subtaskOfFirstEpic, result.get());
        Mockito.verify(inMemoryHistoryManagerMock).add(Mockito.any());
    }

    @Test
    void getSubTask_isNotExist() {
        // Arrange.
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        // Act.
        var result = inMemoryTaskManager.getSubtask(1);

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(inMemoryHistoryManagerMock).add(Optional.empty());
    }

    @Test
    void removeAllSubTasksTest() throws NoSuchFieldException {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfSecondEpic);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.removeAllSubtasks();

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(firstEpic)),
                () -> Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty())
        );
    }

    @Test
    void removeSubTaskByIdTest() throws NoSuchFieldException {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);
        inMemoryTaskManager.createSubtask(secondSubtaskOfFirstEpic);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.removeSubtaskById(2);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size()),
                () -> Assertions.assertFalse(inMemoryTaskManager.getAllSubtasks().contains(subtaskOfFirstEpic)),
                () -> Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size()),
                () -> Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size())
        );
    }

    @Test
    void createSubTaskTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        inMemoryTaskManager.createEpic(firstEpic);

        // Act.
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size()),
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().contains(subtaskOfFirstEpic)),
                () -> Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size()),
                () -> Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size()),
                () -> Assertions.assertEquals(subtaskOfFirstEpic.getStartTime() ,inMemoryTaskManager.getEpic(1).get().getStartTime()),
                () -> Assertions.assertEquals(subtaskOfFirstEpic.getDuration(), inMemoryTaskManager.getEpic(1).get().getDuration()),
                () -> Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:14:00.001"), inMemoryTaskManager.getEpic(1).get().getEndTime())
        );
    }

    @Test
    void updateSubTask_notExistedTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.updateSubtask(subtaskOfFirstEpic);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty()),
                () -> Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty())
        );
    }

    @Test
    void updateSubTaskTest() throws NoSuchFieldException {
        // Arrange.
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);

        // Act.
        inMemoryTaskManager.updateSubtask(updateSubtaskOfFirstEpic);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getSubtask(2).get().getStatus()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(updateSubtaskOfFirstEpic)),
                () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getEpic(1).get().getStatus())
        );
    }

    // Тесты эпиков.
    @Test
    void getAllEpics_MapTest() {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertEquals(1, inMemoryTaskManager.getAllEpics().size());
    }

    @Test
    void getAllEpics_emptyMapTest() {
        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
    }

    @Test
    void getEpicTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var modifiersField = InMemoryTaskManager.class.getDeclaredField("epicMap");
        modifiersField.setAccessible(true);
        modifiersField.set(inMemoryTaskManager, Map.of(1, firstEpic));

        // Act.
        var result = inMemoryTaskManager.getEpic(1);

        // Asserts.
        Assertions.assertEquals(firstEpic, result.get());

        Mockito.verify(inMemoryHistoryManagerMock).add(Mockito.any());
    }

    @Test
    void getEpic_isNotExist() {
        // Arrange.
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        // Act.
        var result = inMemoryTaskManager.getTask(1);

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(inMemoryHistoryManagerMock).add(Optional.empty());
    }

    @Test
    void removeAllEpicsTest() throws NoSuchFieldException {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createEpic(secondEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfSecondEpic);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        // Act.
        inMemoryTaskManager.removeAllEpics();

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty()),
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty())
        );

    }

    @Test
    void removeEpicByIdTest() throws NoSuchFieldException {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createEpic(secondEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfSecondEpic);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        // Act.
        inMemoryTaskManager.removeEpicById(1);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, inMemoryTaskManager.getAllEpics().size()),
                () -> Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size()),
                () -> Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size())
        );
    }

    @Test
    void removeNotExistedEpic() {
        // Arrange.
        var epicMock = Mockito.mock(Epic.class);

        // Act.
        inMemoryTaskManager.removeEpicById(1);

        // Asserts.
        Mockito.verifyNoInteractions(epicMock);
    }

    @Test
    void createEpicTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("epicMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, inMemoryTaskManager.getAllEpics().size()),
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllEpics().contains(firstEpic)),
                () -> Assertions.assertEquals(1, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size()),
                () -> Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size())
        );
    }

    @Test
    void updateEpic_notExistedTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("epicMap");
        taskMapField.setAccessible(true);

        // Act.
        inMemoryTaskManager.updateEpic(firstEpic);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty()),
                () -> Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty())
        );
    }

    @Test
    void updateEpicTest() throws NoSuchFieldException {
        // Arrange.
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        inMemoryTaskManager.createEpic(firstEpic);

        // Act.
        inMemoryTaskManager.updateEpic(updateFirstEpic);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(TaskStatus.NEW, inMemoryTaskManager.getEpic(1).get().getStatus()),
                () -> Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(updateFirstEpic))
        );
    }

    @Test
    void getSubtaskOfEpic_empty_test() {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertTrue(inMemoryTaskManager.getSubtaskOfEpic(firstEpic).isEmpty());
    }

    @Test
    void getSubtaskOfEpic_test() {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);
        inMemoryTaskManager.createSubtask(subtaskOfFirstEpic);

        // Asserts.
        Assertions.assertEquals(subtaskOfFirstEpic, inMemoryTaskManager.getSubtaskOfEpic(firstEpic).get(0));
    }

    @Test
    void getPrioritizedTasksTest() {
        var task1 = new Task.TaskBuilder().withId(3)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-10 07:10:00.001"))
                .withDuration(4)
                .build();
        var task2 = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-03-14 07:14:00.002"))
                .withDuration(5)
                .build();
        var task3 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 3")
                .withDescription("3")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:14:00.002"))
                .withDuration(5)
                .build();
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);

        var result = new ArrayList<>(inMemoryTaskManager.getPrioritizedTasks());

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(task2, result.get(0)),
                () -> Assertions.assertEquals(task1, result.get(1)),
                () -> Assertions.assertEquals(task3, result.get(2))
        );
    }

    @Test
    void getHistoryTest() {
        var historyList = List.of(firstTask, firstTask, secondTask, thirdTask, secondTask);

        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);
        Mockito.when(inMemoryHistoryManagerMock.getHistory()).thenReturn(historyList);

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(3);
        inMemoryTaskManager.getTask(2);

        var result = inMemoryTaskManager.getHistory();

        // Asserts.
        Assertions.assertEquals(historyList, result);
    }

    @Test
    void removeHistoryByIdTest() {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        inMemoryTaskManager.removeHistoryById(0);

        // Asserts.
        Mockito.verify(inMemoryHistoryManagerMock).remove(Mockito.anyInt());
    }

    @Test
    void crossTask_onlyEpic_test() {
        inMemoryTaskManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(secondEpic));
    }

    @Test
    void crossTask_EpicAndSubTask_test() {
        inMemoryTaskManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(subtaskOfFirstEpic));
    }

    @Test
    void crossTask_EpicAndTask_test() {
        // Arrange.

        var epic = new Epic.EpicBuilder().withId(1)
                .withName("Первый эпик")
                .withDescription("Сюда ничего не добавится")
                .build();

        inMemoryTaskManager.createEpic(epic);

        var task = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-10 07:10:00.001"))
                .withDuration(4)
                .build();

        // Asserts.
        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(task));
    }

    @Test
    void crossTask_WithUpdate_test() {
        // Arrange.
        inMemoryTaskManager.createEpic(firstEpic);

        // Asserts.
        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(updateFirstEpic));
    }

    @Test
    void crossTask_crossed_test() {
        // Arrange.
        var firstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        var secondTask = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:13:00.002"))
                .withDuration(5)
                .build();
        inMemoryTaskManager.createTask(firstTask);

        // Asserts.
        Assertions.assertTrue(inMemoryTaskManager.validateCrossTaskExecution(secondTask));
    }

    private void detectVariable() {
        firstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        secondTask = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:14:00.002"))
                .withDuration(5)
                .build();
        thirdTask = new Task.TaskBuilder().withId(3)
                .withName("Простая задача 3")
                .withDescription("3")
                .withStartDate(DateUtils.dateFromString("2023-04-14 08:13:00.002"))
                .withDuration(5)
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
        subtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(2)
                .withName("Подзадача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        updateSubtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(2)
                .withName("Подзадача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withEpicId(1)
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDuration(4)
                .build();
        secondSubtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(3)
                .withName("Подзадача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-15 07:10:00.001"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        subtaskOfSecondEpic = new Subtask.SubtaskBuilder().withId(4)
                .withName("Подзадача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-15 07:10:00.001"))
                .withEpicId(3)
                .withDuration(4)
                .build();
    }

}