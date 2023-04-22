package canban.tasks;

public enum TaskType {
    EPIC("EPIC"),
    TASK("TASK"),
    SUBTASK("SUBTASK");

    private final String name;

    TaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
