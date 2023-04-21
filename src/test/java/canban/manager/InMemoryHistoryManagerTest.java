package canban.manager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mockito.Mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.tasks.Task;
import canban.utils.DateUtils;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager inMemoryHistoryManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        inMemoryHistoryManager = Mockito.spy(new InMemoryHistoryManager());
        detectVariable();
    }

    @Test
    void addHistory() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));

        Assertions.assertEquals(task1, ((Node) headField.get(inMemoryHistoryManager)).getTask());
        Assertions.assertNull(((Node) tailField.get(inMemoryHistoryManager)).getPrev());
        Assertions.assertNull(((Node) tailField.get(inMemoryHistoryManager)).getNext());
        Assertions.assertEquals(task1, ((Node) ((Map) tableField.get(inMemoryHistoryManager)).get(1)).getTask());
    }

    @Test
    void addHistoryExistedTest() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));
        inMemoryHistoryManager.add(Optional.of(task1));

        Assertions.assertEquals(task1, ((Node) headField.get(inMemoryHistoryManager)).getTask());
        Assertions.assertEquals(1, ((Map) tableField.get(inMemoryHistoryManager)).size()) ;
    }

    @Test
    void addHistorySecondaryTest() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));
        inMemoryHistoryManager.add(Optional.of(task2));

        Assertions.assertEquals(task1, ((Node) headField.get(inMemoryHistoryManager)).getTask());
        Assertions.assertEquals(task2, ((Node) tailField.get(inMemoryHistoryManager)).getTask());
        Assertions.assertEquals(2, ((Map) tableField.get(inMemoryHistoryManager)).size()) ;
    }

    @Test
    void addHistoryThirdTest() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));
        inMemoryHistoryManager.add(Optional.of(task2));
        inMemoryHistoryManager.add(Optional.of(task3));

        Assertions.assertEquals(task1, ((Node) headField.get(inMemoryHistoryManager)).getTask());
        Assertions.assertEquals(task3, ((Node) tailField.get(inMemoryHistoryManager)).getTask());
        Assertions.assertEquals(3, ((Map) tableField.get(inMemoryHistoryManager)).size()) ;
    }

    @Test
    void removeFromMiddleTest() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));
        inMemoryHistoryManager.add(Optional.of(task2));
        inMemoryHistoryManager.add(Optional.of(task3));

        var checkHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertEquals(3, checkHistory.size());
        Assertions.assertEquals(task1, ((Node) checkHistory.get(1)).getTask());
        Assertions.assertEquals(task2, ((Node) checkHistory.get(2)).getTask());
        Assertions.assertEquals(task3, ((Node) checkHistory.get(3)).getTask());

        inMemoryHistoryManager.remove(2);

        var resultCheckHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertEquals(2, resultCheckHistory.size());
        Assertions.assertEquals(task1, ((Node) resultCheckHistory.get(1)).getTask());
        Assertions.assertEquals(task3, ((Node) resultCheckHistory.get(3)).getTask());
    }

    @Test
    void removeStartHistoryTest() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));
        inMemoryHistoryManager.add(Optional.of(task2));
        inMemoryHistoryManager.add(Optional.of(task3));

        var checkHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertEquals(3, checkHistory.size());
        Assertions.assertEquals(task1, ((Node) checkHistory.get(1)).getTask());
        Assertions.assertEquals(task2, ((Node) checkHistory.get(2)).getTask());
        Assertions.assertEquals(task3, ((Node) checkHistory.get(3)).getTask());

        inMemoryHistoryManager.remove(1);

        var resultCheckHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertEquals(2, resultCheckHistory.size());
        Assertions.assertEquals(task2, ((Node) resultCheckHistory.get(2)).getTask());
        Assertions.assertEquals(task3, ((Node) resultCheckHistory.get(3)).getTask());
    }

    @Test
    void removeTailHistoryTest() throws NoSuchFieldException, IllegalAccessException {
        var tableField = InMemoryHistoryManager.class.getDeclaredField("table");
        tableField.setAccessible(true);
        var headField = InMemoryHistoryManager.class.getDeclaredField("head");
        headField.setAccessible(true);
        var tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        tailField.setAccessible(true);

        inMemoryHistoryManager.add(Optional.of(task1));
        inMemoryHistoryManager.add(Optional.of(task2));
        inMemoryHistoryManager.add(Optional.of(task3));

        var checkHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertEquals(3, checkHistory.size());
        Assertions.assertEquals(task1, ((Node) checkHistory.get(1)).getTask());
        Assertions.assertEquals(task2, ((Node) checkHistory.get(2)).getTask());
        Assertions.assertEquals(task3, ((Node) checkHistory.get(3)).getTask());

        inMemoryHistoryManager.remove(3);

        var resultCheckHistory = ((Map) tableField.get(inMemoryHistoryManager));
        Assertions.assertEquals(2, resultCheckHistory.size());
        Assertions.assertEquals(task1, ((Node) resultCheckHistory.get(1)).getTask());
        Assertions.assertEquals(task2, ((Node) resultCheckHistory.get(2)).getTask());
    }

    @Test
    void getHistoryIsEmptyTest() {
        Assertions.assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    void getHistoryTest() {
        inMemoryHistoryManager.add(Optional.of(task1));

        var result = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(task1, result.get(0));
    }

    @Test
    void historyToStringEmptyTest() {
        var historyManager = Managers.getDefaultHistory();

        var result = InMemoryHistoryManager.historyToString(historyManager);

        Assertions.assertEquals("", result);
    }

    @Test
    void historyToStringTest() {
        var historyManagerMock = Mockito.mock(InMemoryHistoryManager.class);
        var task1 = new Task.TaskBuilder().withId(1).build();
        Mockito.doReturn(List.of(task1)).when(historyManagerMock).getHistory();

        var result = InMemoryHistoryManager.historyToString(historyManagerMock);

        Assertions.assertEquals("1", result);
    }

    @Test
    void historyFromStringTest() {
        var historyString = "2,1";

        var result = InMemoryHistoryManager.historyFromString(historyString);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(2, result.get(0));
        Assertions.assertEquals(1, result.get(1));
    }

    @Test
    void historyFromStringOnlyHeaderTest() {
        var historyString = "id,type,name,status,description,epic";

        var result = InMemoryHistoryManager.historyFromString(historyString);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void historyFromStringOnlyHeaderAndNTest() {
        var historyString = "id,type,name,status,description,epic\n";

        var result = InMemoryHistoryManager.historyFromString(historyString);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void historyFromEmptyStringTest() {
        var historyString = "";

        var result = InMemoryHistoryManager.historyFromString(historyString);

        Assertions.assertTrue(result.isEmpty());
    }

    private void detectVariable() {
        task1 = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        task2 = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        task3 = new Task.TaskBuilder().withId(3)
                .withName("Простая задача 3")
                .withDescription("3")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
    }

}
