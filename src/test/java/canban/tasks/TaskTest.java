package canban.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.utils.DateUtils;

class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task.TaskBuilder().withId(1)
                .withName("Простая задача 1")
                .withDescription("1")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:10:00.001"))
                .withDuration(4)
                .build();
    }

    @Test
    void toStringTest() {
        var expected = "1,TASK,Простая задача 1,NEW,1,2023-04-14 07:10:00.001,4";
        Assertions.assertEquals(expected, task.toString());
    }

    @Test
    void fromStringTest() {
        var string = "1,TASK,Простая задача 1,NEW,1,2023-04-14 07:10:00.001,4";
        var result = new Task().fromString(string);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals(TaskType.TASK, result.getTaskType());
        Assertions.assertEquals("Простая задача 1", result.getName());
        Assertions.assertEquals(TaskStatus.NEW, result.getStatus());
        Assertions.assertEquals("1", result.getDescription());
        Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:10:00.001"), result.getStartTime());
        Assertions.assertEquals(4, result.getDuration());
    }

}
