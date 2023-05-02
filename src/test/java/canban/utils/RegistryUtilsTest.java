package canban.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskStatus;
import canban.manager.InMemoryTaskManager;
import canban.manager.Managers;

public class RegistryUtilsTest {

    private static final String TEST_RESOURCES = "src/test/resources/";

    private Task firstTask;
    private Task secondTask;
    private Epic firstEpic;
    private Epic secondEpic;
    private Subtask subtaskOfFirstEpic;

    @BeforeEach
    void startUp() {
        detectVariable();
    }

    @Test
    void restoreTaskMemoryTest() {
        try (var registryUtilsMockedStatic = Mockito.mockStatic(RegistryUtils.class);
             // Arrange.
             var managersMockedStatic = Mockito.mockStatic(Managers.class)) {
            registryUtilsMockedStatic.when(RegistryUtils::loadRegistryProperty).thenReturn(TEST_RESOURCES + "restore_task_memory_test/memory.csv");
            registryUtilsMockedStatic.when(RegistryUtils::restoreMemory).thenCallRealMethod();
            var inMemoryTaskManagerMock = Mockito.mock(InMemoryTaskManager.class);
            managersMockedStatic.when(Managers::getInMemoryTaskManager).thenReturn(inMemoryTaskManagerMock);

            // Act.
            RegistryUtils.restoreMemory();

            // Asserts.
            managersMockedStatic.verify(Managers::getInMemoryTaskManager, Mockito.times(5));
            Mockito.verify(inMemoryTaskManagerMock, Mockito.times(2)).createTask(Mockito.any());
            Mockito.verify(inMemoryTaskManagerMock, Mockito.times(2)).createEpic(Mockito.any());
            Mockito.verify(inMemoryTaskManagerMock).createSubtask(Mockito.any());
        }
    }

    @Test
    void restoreEmptyTaskMemoryTest() {
        try (var registryUtilsMockedStatic = Mockito.mockStatic(RegistryUtils.class);
             var managersMockedStatic = Mockito.mockStatic(Managers.class)) {
            // Arrange.
            registryUtilsMockedStatic.when(RegistryUtils::loadRegistryProperty).thenReturn(TEST_RESOURCES + "restore_empty_task_memory_test/memory.csv");
            registryUtilsMockedStatic.when(RegistryUtils::restoreMemory).thenCallRealMethod();
            var inMemoryTaskManagerMock = Mockito.mock(InMemoryTaskManager.class);
            managersMockedStatic.when(Managers::getInMemoryTaskManager).thenReturn(inMemoryTaskManagerMock);

            // Act.
            RegistryUtils.restoreMemory();

            // Asserts.
            managersMockedStatic.verify(Managers::getInMemoryTaskManager, Mockito.times(0));
            Mockito.verify(inMemoryTaskManagerMock, Mockito.times(0)).createTask(Mockito.any());
            Mockito.verify(inMemoryTaskManagerMock, Mockito.times(0)).createEpic(Mockito.any());
            Mockito.verify(inMemoryTaskManagerMock, Mockito.times(0)).createSubtask(Mockito.any());
        }
    }

    @Test
    void writeToMemoryFileTest() {
        try (var mock = Mockito.mockStatic(RegistryUtils.class)) {
            // Arrange.
            var registriesPath = TEST_RESOURCES + "write_to_memory_file_test/";
            var resultRegistryPath = registriesPath + "memory.csv";
            mock.when(RegistryUtils::loadRegistryProperty).thenReturn(resultRegistryPath);
            mock.when(() -> RegistryUtils.getTaskInfo(Mockito.anyList())).thenCallRealMethod();
            mock.when(() -> RegistryUtils.writeToMemoryFile(Mockito.anyList(), Mockito.anyString())).thenCallRealMethod();

            // Act.
            RegistryUtils.writeToMemoryFile(List.of(firstTask, secondTask, firstEpic, subtaskOfFirstEpic, secondEpic), "1,9,1,4");

            // Asserts.
            Assertions.assertEquals(-1, filesCompare(registriesPath + "memoryRef.csv", resultRegistryPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void writeEmptyToMemoryFileTest() {
        try (var mock = Mockito.mockStatic(RegistryUtils.class)) {
            // Arrange.
            var registriesPath = TEST_RESOURCES + "write_empty_to_memory_file_test/";
            var resultRegistryPath = registriesPath + "memory.csv";
            mock.when(RegistryUtils::loadRegistryProperty).thenReturn(resultRegistryPath);
            mock.when(() -> RegistryUtils.getTaskInfo(Mockito.anyList())).thenCallRealMethod();
            mock.when(() -> RegistryUtils.writeToMemoryFile(Mockito.anyList(), Mockito.anyString())).thenCallRealMethod();

            // Act.
            RegistryUtils.writeToMemoryFile(Collections.emptyList(), "");

            // Asserts.
            Assertions.assertEquals(-1, filesCompare(registriesPath + "memoryRef.csv", resultRegistryPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void detectVariable() {
        firstTask = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1 обновлённая")
                .withDescription("1")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
        secondTask = new Task.TaskBuilder().withId(2)
                .withName("Простая задача 2 обновлённая")
                .withDescription("2")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:14:00.002"))
                .withDuration(5)
                .build();
        firstEpic = new Epic.EpicBuilder().withId(4)
                .withName("Первый пустой эпик")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withDescription("Сюда ничего не добавится")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:44:00.002"))
                .withDuration(10)
                .withSubTasksIds(List.of(8))
                .withEndDate(DateUtils.dateFromString("2023-04-14 07:54:00.002"))
                .build();
        secondEpic = new Epic.EpicBuilder().withId(5)
                .withName("Второй эпик")
                .withDescription("С задачами")
                .build();
        subtaskOfFirstEpic = new Subtask.SubtaskBuilder().withId(8)
                .withName("Третья подзадача")
                .withDescription("desc")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:44:00.002"))
                .withEpicId(4)
                .withDuration(10)
                .build();
    }

    private long filesCompare(String ref, String actual) {
        try (var fis1 = new BufferedInputStream(new FileInputStream(ref));
             var fis2 = new BufferedInputStream(new FileInputStream(actual))) {
            var ch = 0;
            var pos = 1L;
            while ((ch = fis1.read()) != -1) {
                if (ch != fis2.read()) {
                    return pos;
                }
                pos++;
            }
            if (fis2.read() == -1) {
                return -1;
            } else {
                return pos;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
