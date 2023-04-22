package canban.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.utils.DateUtils;

class SubtaskTest {

    private Subtask subtask;

    @BeforeEach
    void setUp() {
        subtask = new Subtask.SubtaskBuilder().withId(2)
                .withName("Вторая подзадача")
                .withDescription("desc")
                .withStartDate(DateUtils.dateFromString("2023-04-14 07:34:00.002"))
                .withDuration(9)
                .withEpicId(3)
                .build();
    }

    @Test
    void toStringTest() {
        var expected = "2,SUBTASK,Вторая подзадача,NEW,desc,2023-04-14 07:34:00.002,9,3";
        Assertions.assertEquals(expected, subtask.toString());
    }

    @Test
    void fromStringTest() {
        var string = "2,SUBTASK,Вторая подзадача,NEW,desc,2023-04-14 07:34:00.002,9,3";
        var result = (Subtask) new Subtask().fromString(string);

        Assertions.assertEquals(2, result.getId());
        Assertions.assertEquals(TaskType.SUBTASK, result.getTaskType());
        Assertions.assertEquals("Вторая подзадача", result.getName());
        Assertions.assertEquals(TaskStatus.NEW, result.getStatus());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:34:00.002"), result.getStartTime());
        Assertions.assertEquals(9, result.getDuration());
        Assertions.assertEquals(3, result.getEpicId());
    }

}
