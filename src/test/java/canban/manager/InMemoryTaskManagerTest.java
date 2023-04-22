package canban.manager;

import java.util.Optional;
import java.util.Map;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;

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

    private Task task1;
    private Task task1_1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Epic epic1_1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask1_2;
    private Subtask subtask1_1;
    private Subtask subtask2;

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
    void generateIdTest() throws NoSuchFieldException, IllegalAccessException {
        var modifiersField = InMemoryTaskManager.class.getDeclaredField("idCounter");
        modifiersField.setAccessible(true);

        var startValue = (AtomicInteger) modifiersField.get(inMemoryTaskManager);
        Assertions.assertEquals(0, startValue.get());

        var resultsValue = inMemoryTaskManager.generateId();
        Assertions.assertEquals(1, resultsValue);
    }


    // Тесты для задач.
    @Test
    void getAllTasksTest() {
        var task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        inMemoryTaskManager.createTask(task1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllTasks().size());
    }

    @Test
    void getAllTasks_EmptyTest() {
        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
    }

    @Test
    void getTask_isExist() throws NoSuchFieldException, IllegalAccessException {
        var modifiersField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        modifiersField.setAccessible(true);
        modifiersField.set(inMemoryTaskManager, Map.of(1, task1));

        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var result = inMemoryTaskManager.getTask(1);
        Assertions.assertEquals(task1, result.get());

        Mockito.verify(inMemoryHistoryManagerMock).add(Mockito.any());
    }

    @Test
    void getTask_isNotExist() {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var result = inMemoryTaskManager.getTask(1);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(inMemoryHistoryManagerMock).add(Optional.empty());
    }

    @Test
    void removeAllTasksTest() throws NoSuchFieldException, IllegalAccessException {
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        Assertions.assertFalse(inMemoryTaskManager.getAllTasks().isEmpty());
        Assertions.assertFalse(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertFalse(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.removeAllTasks();

        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());
    }

    @Test
    void removeTaskByIdTest() throws NoSuchFieldException, IllegalAccessException {
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        Assertions.assertEquals(3, inMemoryTaskManager.getAllTasks().size());
        Assertions.assertEquals(3, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(3, ((Map) taskMapField.get(inMemoryTaskManager)).size());

        inMemoryTaskManager.removeTaskById(2);

        Assertions.assertEquals(2, inMemoryTaskManager.getAllTasks().size());
        Assertions.assertFalse(inMemoryTaskManager.getAllTasks().contains(task2));

        Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(2, ((Map) taskMapField.get(inMemoryTaskManager)).size());
    }

    @Test
    void createTaskTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.createTask(task1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllTasks().size());
        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().contains(task1));

        Assertions.assertEquals(1, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size());
    }

    @Test
    void updateTask_notExistedTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("taskMap");
        taskMapField.setAccessible(true);

        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.updateTask(task1);

        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());
    }

    @Test
    void updateTaskTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.updateTask(task1_1);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getTask(1).get().getStatus());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(task1_1));
    }

    // Тесты для подзадач.
    @Test
    void getAllSubTasksTest() {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(subtask1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size());
    }

    @Test
    void getAllSubTasks_emptyMapTest() {
        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void getSubTaskTest() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(subtask1);

        var modifiersField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        modifiersField.setAccessible(true);
        modifiersField.set(inMemoryTaskManager, Map.of(2, subtask1));

        var result = inMemoryTaskManager.getSubtask(2);
        Assertions.assertEquals(subtask1, result.get());

        Mockito.verify(inMemoryHistoryManagerMock).add(Mockito.any());
    }

    @Test
    void getSubTask_isNotExist() {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);
        var result = inMemoryTaskManager.getSubtask(1);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(inMemoryHistoryManagerMock).add(Optional.empty());
    }

    @Test
    void removeAllSubTasksTest() throws NoSuchFieldException, IllegalAccessException {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        Assertions.assertFalse(inMemoryTaskManager.getAllSubtasks().isEmpty());
        Assertions.assertFalse(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertFalse(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.removeAllSubtasks();

        Assertions.assertTrue(inMemoryTaskManager.getAllTasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(epic1));
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());
    }

    @Test
    void removeSubTaskByIdTest() throws NoSuchFieldException, IllegalAccessException {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask1_2);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        Assertions.assertEquals(2, inMemoryTaskManager.getAllSubtasks().size());
        Assertions.assertEquals(3, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(2, ((Map) taskMapField.get(inMemoryTaskManager)).size());

        inMemoryTaskManager.removeSubtaskById(2);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size());
        Assertions.assertFalse(inMemoryTaskManager.getAllSubtasks().contains(subtask1));

        Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size());
    }

    @Test
    void createSubTaskTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.createEpic(epic1);
        Assertions.assertEquals(TaskStatus.NEW, inMemoryTaskManager.getEpic(1).get().getStatus());
        Assertions.assertNull(inMemoryTaskManager.getEpic(1).get().getStartTime());
        Assertions.assertNull(inMemoryTaskManager.getEpic(1).get().getDuration());
        Assertions.assertNull(inMemoryTaskManager.getEpic(1).get().getEndTime());

        inMemoryTaskManager.createSubtask(subtask1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size());
        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().contains(subtask1));

        Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(subtask1.getStartTime() ,inMemoryTaskManager.getEpic(1).get().getStartTime());
        Assertions.assertEquals(subtask1.getDuration(), inMemoryTaskManager.getEpic(1).get().getDuration());
        Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:14:00.001"), inMemoryTaskManager.getEpic(1).get().getEndTime());
    }

    @Test
    void updateSubTask_notExistedTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("subtaskMap");
        taskMapField.setAccessible(true);

        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.updateSubtask(subtask1);

        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());
    }

    @Test
    void updateSubTaskTest() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(subtask1);

        inMemoryTaskManager.updateSubtask(subtask1_1);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getSubtask(2).get().getStatus());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(subtask1_1));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getEpic(1).get().getStatus());
    }

    // Тесты эпиков.
    @Test
    void getAllEpics_MapTest() {
        inMemoryTaskManager.createEpic(epic1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllEpics().size());
    }

    @Test
    void getAllEpics_emptyMapTest() {
        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
    }

    @Test
    void getEpicTest() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var modifiersField = InMemoryTaskManager.class.getDeclaredField("epicMap");
        modifiersField.setAccessible(true);
        modifiersField.set(inMemoryTaskManager, Map.of(1, epic1));

        var result = inMemoryTaskManager.getEpic(1);
        Assertions.assertEquals(epic1, result.get());

        Mockito.verify(inMemoryHistoryManagerMock).add(Mockito.any());
    }

    @Test
    void getEpic_isNotExist() {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        var result = inMemoryTaskManager.getTask(1);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(inMemoryHistoryManagerMock).add(Optional.empty());
    }

    @Test
    void removeAllEpicsTest() throws NoSuchFieldException, IllegalAccessException {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createEpic(epic2);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        Assertions.assertFalse(inMemoryTaskManager.getAllEpics().isEmpty());
        Assertions.assertFalse(inMemoryTaskManager.getAllSubtasks().isEmpty());
        Assertions.assertFalse(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.removeAllEpics();

        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(inMemoryTaskManager.getAllSubtasks().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
    }

    @Test
    void removeEpicByIdTest() throws NoSuchFieldException, IllegalAccessException {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createEpic(epic2);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        Assertions.assertEquals(2, inMemoryTaskManager.getAllEpics().size());
        Assertions.assertEquals(2, inMemoryTaskManager.getAllSubtasks().size());
        Assertions.assertEquals(4, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());

        inMemoryTaskManager.removeEpicById(1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllEpics().size());
        Assertions.assertEquals(1, inMemoryTaskManager.getAllSubtasks().size());
        Assertions.assertEquals(2, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
    }

    @Test
    void removeNotExistedEpic() {
        var epicMock = Mockito.mock(Epic.class);

        inMemoryTaskManager.removeEpicById(1);

        Mockito.verifyNoInteractions(epicMock);
    }

    @Test
    void createEpicTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("epicMap");
        taskMapField.setAccessible(true);

        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.createEpic(epic1);

        Assertions.assertEquals(1, inMemoryTaskManager.getAllEpics().size());
        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().contains(epic1));

        Assertions.assertEquals(1, ((TreeSet) allTasksField.get(inMemoryTaskManager)).size());
        Assertions.assertEquals(1, ((Map) taskMapField.get(inMemoryTaskManager)).size());
    }

    @Test
    void updateEpic_notExistedTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);
        var taskMapField = InMemoryTaskManager.class.getDeclaredField("epicMap");
        taskMapField.setAccessible(true);

        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());

        inMemoryTaskManager.updateEpic(epic1);

        Assertions.assertTrue(inMemoryTaskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).isEmpty());
        Assertions.assertTrue(((Map) taskMapField.get(inMemoryTaskManager)).isEmpty());
    }

    @Test
    void updateEpicTest() throws NoSuchFieldException, IllegalAccessException {
        var allTasksField = InMemoryTaskManager.class.getDeclaredField("allTasks");
        allTasksField.setAccessible(true);

        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.updateEpic(epic1_1);

        Assertions.assertEquals(TaskStatus.NEW, inMemoryTaskManager.getEpic(1).get().getStatus());
        Assertions.assertTrue(((TreeSet) allTasksField.get(inMemoryTaskManager)).contains(epic1_1));
    }

    @Test
    void getSubtaskOfEpic_empty_test() {
        inMemoryTaskManager.createEpic(epic1);

        Assertions.assertTrue(inMemoryTaskManager.getSubtaskOfEpic(epic1).isEmpty());
    }

    @Test
    void getSubtaskOfEpic_test() {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(subtask1);

        Assertions.assertEquals(subtask1, inMemoryTaskManager.getSubtaskOfEpic(epic1).get(0));
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

        Assertions.assertEquals(task2, result.get(0));
        Assertions.assertEquals(task1, result.get(1));
        Assertions.assertEquals(task3, result.get(2));
    }

    @Test
    void getHistoryTest() {
        var historyList = List.of(task1, task1, task2, task3, task2);

        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);
        Mockito.when(inMemoryHistoryManagerMock.getHistory()).thenReturn(historyList);

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getTask(3);
        inMemoryTaskManager.getTask(2);

        var result = inMemoryTaskManager.getHistory();

        Assertions.assertEquals(historyList, result);
    }

    @Test
    void removeHistoryByIdTest() {
        Mockito.when(Managers.getDefaultHistory()).thenReturn(inMemoryHistoryManagerMock);

        inMemoryTaskManager.removeHistoryById(0);

        Mockito.verify(inMemoryHistoryManagerMock).remove(Mockito.anyInt());
    }

    @Test
    void crossTask_onlyEpic_test() {
        inMemoryTaskManager.createEpic(epic1);

        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(epic2));
    }

    @Test
    void crossTask_EpicAndSubTask_test() {
        inMemoryTaskManager.createEpic(epic1);

        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(subtask1));
    }

    @Test
    void crossTask_EpicAndTask_test() {
        var epic1 = new Epic.EpicBuilder().withId(1)
                .withName("Первый эпик")
                .withDescription("Сюда ничего не добавится")
                .build();

        inMemoryTaskManager.createEpic(epic1);

        var task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-10 07:10:00.001"))
                .withDuration(4)
                .build();

        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(task1));
    }

    @Test
    void crossTask_WithUpdate_test() {
        inMemoryTaskManager.createEpic(epic1);

        Assertions.assertFalse(inMemoryTaskManager.validateCrossTaskExecution(epic1_1));
    }

    @Test
    void crossTask_crossed_test() {
        var task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        var task2 = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:13:00.002"))
                .withDuration(5)
                .build();
        inMemoryTaskManager.createTask(task1);

        Assertions.assertTrue(inMemoryTaskManager.validateCrossTaskExecution(task2));
    }

    private void detectVariable() {
        task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        task2 = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:14:00.002"))
                .withDuration(5)
                .build();
        task3 = new Task.TaskBuilder().withId(3)
                .withName("Простая задача 3")
                .withDescription("3")
                .withStartDate(DateUtils.dateFromString("2023-04-14 08:13:00.002"))
                .withDuration(5)
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
        subtask1_2 = new Subtask.SubtaskBuilder().withId(3)
                .withName("Подзадача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-15 07:10:00.001"))
                .withEpicId(1)
                .withDuration(4)
                .build();
        subtask2 = new Subtask.SubtaskBuilder().withId(4)
                .withName("Подзадача 2")
                .withDescription("2")
                .withStartDate(DateUtils.dateFromString("2023-04-15 07:10:00.001"))
                .withEpicId(3)
                .withDuration(4)
                .build();
    }

}
