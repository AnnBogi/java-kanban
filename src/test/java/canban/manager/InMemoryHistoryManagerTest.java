package canban.manager;

import canban.tasks.Task;
import canban.utils.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager inMemoryHistoryManager;
    private Task firstTask;
    private Task secondTask;
    private Task thirdTask;

    @BeforeEach
    void setUp() {
        inMemoryHistoryManager = Mockito.spy(new InMemoryHistoryManager());
        detectVariable();
    }

    @Test
    void addHistoryTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        // Act.
        inMemoryHistoryManager.add(Optional.of(firstTask));

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(firstTask, ((Node) headField.get(inMemoryHistoryManager)).getTask()),
                () -> Assertions.assertNull(((Node) tailField.get(inMemoryHistoryManager)).getPrev()),
                () -> Assertions.assertNull(((Node) tailField.get(inMemoryHistoryManager)).getNext()),
                () -> Assertions.assertEquals(firstTask, ((Node) ((Map) tableField.get(inMemoryHistoryManager)).get(1)).getTask())
        );
    }

    @Test
    void addHistoryExistedTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);

        // Act.
        inMemoryHistoryManager.add(Optional.of(firstTask));
        inMemoryHistoryManager.add(Optional.of(firstTask));

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(firstTask, ((Node) headField.get(inMemoryHistoryManager)).getTask()),
                () -> Assertions.assertEquals(1, ((Map) tableField.get(inMemoryHistoryManager)).size())
        );
    }

    @Test
    void addHistorySecondaryTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        // Act.
        inMemoryHistoryManager.add(Optional.of(firstTask));
        inMemoryHistoryManager.add(Optional.of(secondTask));

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(firstTask, ((Node) headField.get(inMemoryHistoryManager)).getTask()),
                () -> Assertions.assertEquals(secondTask, ((Node) tailField.get(inMemoryHistoryManager)).getTask()),
                () -> Assertions.assertEquals(2, ((Map) tableField.get(inMemoryHistoryManager)).size())
        );
    }

    @Test
    void addHistoryThirdTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        // Act.
        inMemoryHistoryManager.add(Optional.of(firstTask));
        inMemoryHistoryManager.add(Optional.of(secondTask));
        inMemoryHistoryManager.add(Optional.of(thirdTask));

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(firstTask, ((Node) headField.get(inMemoryHistoryManager)).getTask()),
                () -> Assertions.assertEquals(thirdTask, ((Node) tailField.get(inMemoryHistoryManager)).getTask()),
                () -> Assertions.assertEquals(3, ((Map) tableField.get(inMemoryHistoryManager)).size())
        );
    }

    @Test
    void removeFromMiddleTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(firstTask));
        inMemoryHistoryManager.add(Optional.of(secondTask));
        inMemoryHistoryManager.add(Optional.of(thirdTask));

        // Act.
        inMemoryHistoryManager.remove(2);

        // Asserts.
        var resultCheckHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, resultCheckHistory.size()),
                () -> Assertions.assertEquals(firstTask, ((Node) resultCheckHistory.get(1)).getTask()),
                () -> Assertions.assertEquals(thirdTask, ((Node) resultCheckHistory.get(3)).getTask())
        );
    }

    @Test
    void removeStartHistoryTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(firstTask));
        inMemoryHistoryManager.add(Optional.of(secondTask));
        inMemoryHistoryManager.add(Optional.of(thirdTask));

        // Act.
        inMemoryHistoryManager.remove(1);

        // Asserts.
        var resultCheckHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, resultCheckHistory.size()),
                () -> Assertions.assertEquals(secondTask, ((Node) resultCheckHistory.get(2)).getTask()),
                () -> Assertions.assertEquals(thirdTask, ((Node) resultCheckHistory.get(3)).getTask())
        );
    }

    @Test
    void removeTailHistoryTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange.
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(firstTask));
        inMemoryHistoryManager.add(Optional.of(secondTask));
        inMemoryHistoryManager.add(Optional.of(thirdTask));

        // Act.
        inMemoryHistoryManager.remove(3);

        // Asserts.
        var resultCheckHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, resultCheckHistory.size()),
                () -> Assertions.assertEquals(firstTask, ((Node) resultCheckHistory.get(1)).getTask()),
                () -> Assertions.assertEquals(secondTask, ((Node) resultCheckHistory.get(2)).getTask())
        );
    }

    @Test
    void getHistoryIsEmptyTest() {
        // Asserts.
        Assertions.assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    void getHistoryTest() {
        // Arrange.
        inMemoryHistoryManager.add(Optional.of(firstTask));

        // Act.
        var result = inMemoryHistoryManager.getHistory();

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(firstTask, result.get(0))
        );
    }

    @Test
    void historyToStringEmptyTest() {
        // Arrange.
        var historyManager = Managers.getDefaultHistory();

        // Act.
        var result = InMemoryHistoryManager.historyToString(historyManager);

        // Asserts.
        Assertions.assertEquals("", result);
    }

    @Test
    void historyToStringTest() {
        // Arrange.
        var historyManagerMock = Mockito.mock(InMemoryHistoryManager.class);
        var task1 = new Task.TaskBuilder().withId(1).build();
        Mockito.doReturn(List.of(task1)).when(historyManagerMock).getHistory();

        // Act.
        var result = InMemoryHistoryManager.historyToString(historyManagerMock);

        // Asserts.
        Assertions.assertEquals("1", result);
    }

    @Test
    void historyFromStringTest() {
        // Arrange.
        var historyString = "2,1";

        // Act.
        var result = InMemoryHistoryManager.historyFromString(historyString);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(2, result.get(0)),
                () -> Assertions.assertEquals(1, result.get(1))
        );
    }

    @Test
    void historyFromStringOnlyHeaderTest() {
        // Arrange.
        var historyString = "id,type,name,status,description,epic";

        // Act.
        var result = InMemoryHistoryManager.historyFromString(historyString);

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void historyFromStringOnlyHeaderAndNTest() {
        // Arrange.
        var historyString = "id,type,name,status,description,epic\n";

        // Act.
        var result = InMemoryHistoryManager.historyFromString(historyString);

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void historyFromEmptyStringTest() {
        // Arrange.
        var historyString = "";

        // Act.
        var result = InMemoryHistoryManager.historyFromString(historyString);

        // Asserts.
        Assertions.assertTrue(result.isEmpty());
    }

    private void detectVariable() {
        firstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        secondTask = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        thirdTask = new Task.TaskBuilder().withId(3)
                .withName("Простая задача 3")
                .withDescription("3")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
    }

}