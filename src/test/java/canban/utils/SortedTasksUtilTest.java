package canban.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.tasks.Task;

class SortedTasksUtilTest {

    private SortedTasksUtil sortedTasksUtil;

    private Task taskEmpty1;
    private Task taskEmpty2;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        sortedTasksUtil = new SortedTasksUtil();
        detectVariable();
    }

    @Test
    void compareFirstEmptyTest() {
        Assertions.assertEquals(1, sortedTasksUtil.compare(taskEmpty1, task2));
    }

    @Test
    void compareSecondEmptyTest() {
        Assertions.assertEquals(-1, sortedTasksUtil.compare(task1, taskEmpty2));
    }

    @Test
    void compareFirstMoreSecondTest() {
        Assertions.assertEquals(-1, sortedTasksUtil.compare(taskEmpty1, taskEmpty2));
    }

    @Test
    void compareSecondMoreFirstTest() {
        Assertions.assertEquals(1, sortedTasksUtil.compare(taskEmpty2, taskEmpty1));
    }

    @Test
    void compareSecondEqualFirstTest() {
        Assertions.assertEquals(0, sortedTasksUtil.compare(taskEmpty2, taskEmpty2));
    }

    @Test
    void compareFirstStartAfterSecondTest() {
        Assertions.assertEquals(1, sortedTasksUtil.compare(task2, task1));
    }

    @Test
    void compareSecondStartAfterFirstTest() {
        Assertions.assertEquals(-1, sortedTasksUtil.compare(task1, task2));
    }

    @Test
    void compareSecondStartEqualFirstTest() {
        Assertions.assertEquals(0, sortedTasksUtil.compare(task1, task1));
    }

    private void detectVariable() {
        taskEmpty1 = new Task.TaskBuilder().withId(1).build();
        taskEmpty2 = new Task.TaskBuilder().withId(2).build();

        task1 = new Task.TaskBuilder().withId(1)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .build();
        task2 = new Task.TaskBuilder().withId(2)
                .withStartDate(DateUtils.dateFromString("2023-04-14 08:10:00.001"))
                .build();
    }

}
