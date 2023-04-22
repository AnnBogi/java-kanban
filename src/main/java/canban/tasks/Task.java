package canban.tasks;

import java.util.Date;

import lombok.Data;

import canban.utils.DateUtils;

/**
 * Сущность задачи.
 */
@Data
public class Task {

    /*
     * Идентификатор задачи.
     */
    protected Integer id;

    /*
     * Наименование задачи.
     */
    protected String name;

    /*
     * Описание задачи.
     */
    protected String description;

    /*
     * Статус задачи.
     */
    protected TaskStatus status;

    protected TaskType taskType;

    protected Long duration;

    protected Date startTime;

    public Date getEndTime() {
        return new Date(
                startTime.getTime() + (duration * 60 * 1000)
        );
    }

    @Override
    public String toString() {
        var startDate = startTime == null ? "" : DateUtils.dateToString(startTime);
        var durationValue = duration == null ? "" : duration;
        return id + "," + taskType + "," + name + "," + status + "," + description + "," + startDate + "," + durationValue;
    }

    public Task fromString(String value) {
        var values = value.split(",");
        return new TaskBuilder()
                .withId(Integer.parseInt(values[0]))
                .withName(values[2])
                .withStatus(TaskStatus.valueOf(values[3]))
                .withDescription(values[4])
                .withStartDate(DateUtils.dateFromString(values[5]))
                .withDuration(Long.parseLong(values[6]))
                .build();
    }

    public static class TaskBuilder {
        private final Task newTask;

        public TaskBuilder() {
            this.newTask = new Task();
            this.newTask.status = TaskStatus.NEW;
            this.newTask.taskType = TaskType.TASK;
        }

        public TaskBuilder withId(Integer id) {
            newTask.id = id;
            return this;
        }

        public TaskBuilder withName(String name) {
            newTask.name = name;
            return this;
        }
        public TaskBuilder withDescription(String description) {
            newTask.description = description;
            return this;
        }

        public TaskBuilder withStatus(TaskStatus status) {
            newTask.status = status;
            return this;
        }

        public TaskBuilder withStartDate(Date date) {
            newTask.startTime = date;
            return this;
        }

        public TaskBuilder withDuration(long duration) {
            newTask.duration = duration;
            return this;
        }

        public Task build() {
            return newTask;
        }
    }

}