package tasks;

/**
 * Сущность подзадачи.
 */
public class Subtask extends Task {

    /*
     * Идентификатор связного эпика.
     */
    private Integer epicId;

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() + epicId;
    }

    @Override
    public Task fromString(String value) {
        var values = value.split(",");
        return new SubtaskBuilder()
                .withId(Integer.parseInt(values[0]))
                .withName(values[2])
                .withDescription(values[3])
                .withEpicId(Integer.parseInt(values[4]))
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

        public Subtask build() {
            return newSubtask;
        }

    }

}