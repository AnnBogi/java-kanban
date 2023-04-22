package canban.tasks;

import java.util.Date;

import lombok.Getter;

import canban.utils.DateUtils;

/**
 * Сущность подзадачи.
 */
@Getter
public class Subtask extends Task {

    /*
     * Идентификатор связного эпика.
     */
    private Integer epicId;

    @Override
    public String toString() {
        return super.toString() + "," + epicId;
    }

    @Override
    public Subtask fromString(String value) {
        var values = value.split(",");
        return new SubtaskBuilder()
                .withId(Integer.parseInt(values[0]))
                .withName(values[2])
                .withStatus(TaskStatus.valueOf(values[3]))
                .withDescription(values[4])
                .withStartDate(DateUtils.dateFromString(values[5]))
                .withDuration(Long.parseLong(values[6]))
                .withEpicId(Integer.parseInt(values[7]))
                .build();
    }

    public static class SubtaskBuilder {
        private final Subtask newSubtask;

        public SubtaskBuilder() {
            this.newSubtask = new Subtask();
            this.newSubtask.status = TaskStatus.NEW;
            this.newSubtask.taskType = TaskType.SUBTASK;
        }

        public SubtaskBuilder withId(Integer id) {
            newSubtask.id = id;
            return this;
        }

        public SubtaskBuilder withName(String name) {
            newSubtask.name = name;
            return this;
        }
        public SubtaskBuilder withDescription(String description) {
            newSubtask.description = description;
            return this;
        }

        public SubtaskBuilder withEpicId(Integer epicId) {
            newSubtask.epicId = epicId;
            return this;
        }

        public SubtaskBuilder withStatus(TaskStatus status) {
            newSubtask.status = status;
            return this;
        }

        public SubtaskBuilder withStartDate(Date date) {
            newSubtask.startTime = date;
            return this;
        }

        public SubtaskBuilder withDuration(long duration) {
            newSubtask.duration = duration;
            return this;
        }

        public Subtask build() {
            return newSubtask;
        }

    }

}