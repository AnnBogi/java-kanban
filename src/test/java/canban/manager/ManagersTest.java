package canban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void shouldReturnInMemoryTaskManagerInstanceWhenCalled() {
        var result = Managers.getInMemoryTaskManager();
        Assertions.assertTrue(result instanceof InMemoryTaskManager);
    }

    @Test
    void shouldReturnInMemoryHistoryManagerInstanceWhenCalled() {
        var result = Managers.getDefaultHistory();
        Assertions.assertTrue(result instanceof InMemoryHistoryManager);
    }

    @Test
    void shouldReturnFileBackedTasksManagerInstanceWhenCalled() {
        var result = Managers.getFileBackedTasksManager();
        Assertions.assertTrue(result instanceof FileBackedTasksManager);
    }

}


