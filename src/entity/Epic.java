package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {

    private final List<SubTask> subTaskList;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskList = new ArrayList<>();
    }

    public Task addSubTask(String name, String description) {
        var subTask = new SubTask(name, description, getId());
        subTaskList.add(subTask);
        return subTask;
    }

    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void updateSubTask(Task task) {
        var updatedTask = subTaskList.get(subTaskList.indexOf(task));
        updatedTask.updateTask(task);
        updateTask(this);
    }

    public void removeSubTask(SubTask subTask) {
        subTaskList.remove(subTask);
        updateTask(this);
    }

    @Override
    public void updateTask(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = getEpicStatus();
    }

    private TaskStatus getEpicStatus() {
        var subtaskStatuses = subTaskList.stream()
                .map(Task::getStatus)
                .collect(Collectors.toSet());
        if (subtaskStatuses.contains(TaskStatus.IN_PROGRESS)) {
            return TaskStatus.IN_PROGRESS;
        } else if (subtaskStatuses.size() == 1 && subtaskStatuses.contains(TaskStatus.DONE)) {
            return TaskStatus.DONE;
        }

        return TaskStatus.NEW;
    }

    @Override
    public String toString() {
        var subTasksDesc = new StringBuffer();
        subTaskList.forEach(subTask ->
                subTasksDesc.append(subTask.toString())
        );
        return "Epic{" +
                "subTaskList=" + subTasksDesc +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}