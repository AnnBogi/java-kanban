package canban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getInMemoryTaskManagerTest() {
        // Act.
        var result = Managers.getInMemoryTaskManager();

        // Asserts.
        Assertions.assertTrue(result instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistoryTest() {
        // Act.
        var result = Managers.getDefaultHistory();

        // Asserts.
        Assertions.assertTrue(result instanceof InMemoryHistoryManager);
    }

    @Test
    void getFileBackedTasksManagerTest() {
        // Act.
        var result = Managers.getFileBackedTasksManager();

        // Asserts.
        Assertions.assertTrue(result instanceof FileBackedTasksManager);
    }

}
