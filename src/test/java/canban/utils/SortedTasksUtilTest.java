package canban.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.tasks.Task;

class SortedTasksUtilTest {

    private SortedTasksUtil sortedTasksUtil;

    private Task firstEmptyTask;
    private Task secondEmptyTask;
    private Task firstTask;
    private Task secondTask;

    @BeforeEach
    void setUp() {
        sortedTasksUtil = new SortedTasksUtil();
        detectVariable();
    }

    @Test
    void compareFirstEmptyTest() {
        Assertions.assertEquals(1, sortedTasksUtil.compare(firstEmptyTask, secondTask));
    }

    @Test
    void compareSecondEmptyTest() {
        Assertions.assertEquals(-1, sortedTasksUtil.compare(firstTask, secondEmptyTask));
    }

    @Test
    void compareFirstMoreSecondTest() {
        Assertions.assertEquals(-1, sortedTasksUtil.compare(firstEmptyTask, secondEmptyTask));
    }

    @Test
    void compareSecondMoreFirstTest() {
        Assertions.assertEquals(1, sortedTasksUtil.compare(secondEmptyTask, firstEmptyTask));
    }

    @Test
    void compareSecondEqualFirstTest() {
        Assertions.assertEquals(0, sortedTasksUtil.compare(secondEmptyTask, secondEmptyTask));
    }

    @Test
    void compareFirstStartAfterSecondTest() {
        Assertions.assertEquals(1, sortedTasksUtil.compare(secondTask, firstTask));
    }

    @Test
    void compareSecondStartAfterFirstTest() {
        Assertions.assertEquals(-1, sortedTasksUtil.compare(firstTask, secondTask));
    }

    @Test
    void compareSecondStartEqualFirstTest() {
        Assertions.assertEquals(0, sortedTasksUtil.compare(firstTask, firstTask));
    }

    private void detectVariable() {
        firstEmptyTask = new Task.TaskBuilder().withId(1).build();
        secondEmptyTask = new Task.TaskBuilder().withId(2).build();

        firstTask = new Task.TaskBuilder().withId(1)
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .build();
        secondTask = new Task.TaskBuilder().withId(2)
                .withStartDate(DateUtils.dateFromString("2023-04-14 08:10:00.001"))
                .build();
    }

}
