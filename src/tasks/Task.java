package tasks;

/**
 * Сущность задачи.
 */
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


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return id + "," + taskType + "," + name + "," + description + ",";
    }

    public Task fromString(String value) {
        var values = value.split(",");
        return new TaskBuilder()
                .withId(Integer.parseInt(values[0]))
                .withName(values[2])
                .withDescription(values[3])
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

        public Task build() {
            return newTask;
        }

    }

}