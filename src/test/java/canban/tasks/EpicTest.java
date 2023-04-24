package canban.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import canban.utils.DateUtils;

class EpicTest {

    private Epic epic;

    @BeforeEach
    void setup() {
        epic = new Epic();
    }

    @Test
    void removeSubtaskId_test() {
        // Arrange.
        epic.addSubTaskId(1);
        epic.addSubTaskId(2);

        epic.removeSubtaskId(3);
        epic.removeSubtaskId(2);

        // Act.
        var result = epic.getSubtaskIdsList();

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.size(), 1),
                () -> Assertions.assertEquals(1, result.get(0))
        );
    }

    @Test
    void emptyEpicFromStringTest() {
        // Arrange
        var string = "7,EPIC,эпик,NEW,desc,,,";

        // Act.
        var result = epic.fromString(string);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(7, result.getId()),
                () -> Assertions.assertEquals(TaskType.EPIC, result.getTaskType()),
                () -> Assertions.assertEquals("эпик", result.getName()),
                () -> Assertions.assertEquals(TaskStatus.NEW, result.getStatus()),
                () -> Assertions.assertEquals("desc", result.getDescription()),
                () -> Assertions.assertNull(result.getStartTime()),
                () -> Assertions.assertNull(result.getDuration()),
                () -> Assertions.assertNull(result.getEndTime()),
                () -> Assertions.assertTrue(result.getSubtaskIdsList().isEmpty())
        );
    }

    @Test
    void fromStringTest() {
        // Arrange
        var string = "4,EPIC,Первый пустой эпик,IN_PROGRESS,Сюда ничего не добавится,2023-04-14 07:44:00.002,10,2023-04-14 07:54:00.002,[8]";

        // Act.
        var result = epic.fromString(string);

        // Asserts.
        Assertions.assertAll(
                () -> Assertions.assertEquals(4, result.getId()),
                () -> Assertions.assertEquals(TaskType.EPIC, result.getTaskType()),
                () -> Assertions.assertEquals("Первый пустой эпик", result.getName()),
                () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS, result.getStatus()),
                () -> Assertions.assertEquals("Сюда ничего не добавится", result.getDescription()),
                () -> Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:44:00.002"), result.getStartTime()),
                () -> Assertions.assertEquals(10, result.getDuration()),
                () -> Assertions.assertEquals(1, result.getSubtaskIdsList().size()),
                () -> Assertions.assertEquals(8, result.getSubtaskIdsList().get(0)),
                () -> Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:54:00.002"), result.getEndTime())
        );
    }

    @Test
    void toString_emptySubtasksTest() {
        // Arrange
        var epic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        var expected = "1,EPIC,Первый пустой эпик,NEW,Сюда ничего не добавится,,,";

        // Asserts.
        Assertions.assertEquals(expected, epic.toString());
    }

    @Test
    void toStringTest() {
        // Arrange
        var epic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда добавится")
                .build();
        var expected = "1,EPIC,Первый пустой эпик,NEW,Сюда добавится,,,,[2, 3]";
        epic.addSubTaskId(2);
        epic.addSubTaskId(3);

        // Asserts.
        Assertions.assertEquals(expected, epic.toString());
    }

}