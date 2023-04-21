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

    // Тест избыточен т.к. проверяется в других необходимых тестах, но это описано в задании.
    @Test
    void getSubtaskIdsList_empty_test() {
        var result = epic.getSubtaskIdsList();
        Assertions.assertTrue(result.isEmpty());
    }

    // Тест избыточен т.к. проверяется в других необходимых тестах, но это описано в задании.
    @Test
    void getSubtaskIdsList_non_empty_test() {
        epic.addSubTaskId(1);
        epic.addSubTaskId(2);

        var result = epic.getSubtaskIdsList();
        Assertions.assertEquals(result.size(), 2);
        Assertions.assertEquals(1, result.get(0));
        Assertions.assertEquals(2, result.get(1));
    }

    @Test
    void removeSubtaskId_test() {
        epic.addSubTaskId(1);
        epic.addSubTaskId(2);

        epic.removeSubtaskId(3);
        epic.removeSubtaskId(2);

        var result = epic.getSubtaskIdsList();
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(1, result.get(0));
    }

    @Test
    void emptyEpicFromStringTest() {
        var string = "7,EPIC,эпик,NEW,desc,,,";

        var result = epic.fromString(string);

        Assertions.assertEquals(7, result.getId());
        Assertions.assertEquals(TaskType.EPIC, result.getTaskType());
        Assertions.assertEquals("эпик", result.getName());
        Assertions.assertEquals(TaskStatus.NEW, result.getStatus());
        Assertions.assertEquals("desc", result.getDescription());
        Assertions.assertNull(result.getStartTime());
        Assertions.assertNull(result.getDuration());
        Assertions.assertNull(result.getEndTime());
        Assertions.assertTrue(result.getSubtaskIdsList().isEmpty());
    }

    @Test
    void fromStringTest() {
        var string = "4,EPIC,Первый пустой эпик,IN_PROGRESS,Сюда ничего не добавится,2023-04-14 07:44:00.002,10,2023-04-14 07:54:00.002,[8]";

        var result = epic.fromString(string);

        Assertions.assertEquals(4, result.getId());
        Assertions.assertEquals(TaskType.EPIC, result.getTaskType());
        Assertions.assertEquals("Первый пустой эпик", result.getName());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        Assertions.assertEquals("Сюда ничего не добавится", result.getDescription());
        Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:44:00.002"), result.getStartTime());
        Assertions.assertEquals(10, result.getDuration());
        Assertions.assertEquals(1, result.getSubtaskIdsList().size());
        Assertions.assertEquals(8, result.getSubtaskIdsList().get(0));
        Assertions.assertEquals(DateUtils.dateFromString("2023-04-14 07:54:00.002"), result.getEndTime());
    }

    @Test
    void toString_emptySubtasksTest() {
        var epic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда ничего не добавится")
                .build();
        var expeted = "1,EPIC,Первый пустой эпик,NEW,Сюда ничего не добавится,,,";
        Assertions.assertEquals(expeted, epic.toString());
    }

    @Test
    void toStringTest() {
        var epic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда добавится")
                .build();
        var expeted = "1,EPIC,Первый пустой эпик,NEW,Сюда добавится,,,,[2, 3]";
        epic.addSubTaskId(2);
        epic.addSubTaskId(3);
        Assertions.assertEquals(expeted, epic.toString());
    }

    @Test
    void endTimeTest() {
        var epic = new Epic.EpicBuilder().withId(1)
                .withName("Первый пустой эпик")
                .withDescription("Сюда добавится")
                .build();
        var date = DateUtils.dateFromString("2023-04-14 07:24:00.002");
        epic.setEndTime(date);

        Assertions.assertEquals(date, epic.getEndTime());
    }

}