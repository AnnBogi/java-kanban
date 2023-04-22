package canban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getInMemoryTaskManagerTest() {
        var result = Managers.getInMemoryTaskManager();
        Assertions.assertTrue(result instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistoryTest() {
        var result = Managers.getDefaultHistory();
        Assertions.assertTrue(result instanceof InMemoryHistoryManager);
    }

    @Test
    void getFileBackedTasksManagerTest() {
        var result = Managers.getFileBackedTasksManager();
        Assertions.assertTrue(result instanceof FileBackedTasksManager);
    }

}
