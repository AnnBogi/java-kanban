package tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность эпик.
 */
public class Epic extends Task {

    /*
     * Список подзадач относящихся к эпику.
     */
    private final List<Integer> subtaskIdsList = new ArrayList<>();

    public List<Integer> getSubtaskIdsList() {
        return subtaskIdsList;
    }

    public void addSubTaskId(Integer subtaskId) {
        subtaskIdsList.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtaskIdsList.remove(subtaskId);
    }

    public static class EpicBuilder {
        private final Epic newEpic;

        public EpicBuilder() {
            this.newEpic = new Epic();
            this.newEpic.status = TaskStatus.NEW;
        }

        public EpicBuilder withId(Integer id) {
            newEpic.id = id;
            return this;
        }

        public EpicBuilder withName(String name) {
            newEpic.name = name;
            return this;
        }
        public EpicBuilder withDescription(String description) {
            newEpic.description = description;
            return this;
        }

        public Epic build() {
            return newEpic;
        }

    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIdsList=" + subtaskIdsList +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

}