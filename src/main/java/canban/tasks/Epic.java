package canban.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

import canban.utils.DateUtils;

/**
 * Сущность эпик.
 */
@Data
public class Epic extends Task {

    /*
     * Список подзадач относящихся к эпику.
     */
    private final List<Integer> subtaskIdsList = new ArrayList<>();

    private Date endTime;

    public void addSubTaskId(Integer subtaskId) {
        subtaskIdsList.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtaskIdsList.remove(subtaskId);
    }

    @Override
    public Epic fromString(String value) {
        if (value.contains("[")) {
            var valueWithoutSubtasks = value.split("\\[")[0];
            var subTaskIds = value.substring(value.indexOf("[") + 1, value.indexOf("]"));
            var values = valueWithoutSubtasks.split(",");
            return new EpicBuilder()
                    .withId(Integer.parseInt(values[0]))
                    .withName(values[2])
                    .withStatus(TaskStatus.valueOf(values[3]))
                    .withDescription(values[4])
                    .withStartDate(DateUtils.dateFromString(values[5]))
                    .withDuration(Long.parseLong(values[6]))
                    .withEndDate(DateUtils.dateFromString(values[7]))
                    .withSubTasksIds(subtasksIdToIntgerList(subTaskIds))
                    .build();
        }
        var values = value.split(",");
        return new EpicBuilder()
                .withId(Integer.parseInt(values[0]))
                .withName(values[2])
                .withStatus(TaskStatus.valueOf(values[3]))
                .withDescription(values[4])
                .build();
    }

    @Override
    public String toString() {
        var endDate = endTime == null ? "" : DateUtils.dateToString(endTime);
        if (!getSubtaskIdsList().isEmpty()) {
            return super.toString() + "," + endDate + "," +  getSubtaskIdsList();
        }
        return super.toString() + "," + endDate;
    }

    public static class EpicBuilder {
        private final Epic newEpic;

        public EpicBuilder() {
            this.newEpic = new Epic();
            this.newEpic.status = TaskStatus.NEW;
            this.newEpic.taskType = TaskType.EPIC;
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

        public EpicBuilder withStatus(TaskStatus status) {
            newEpic.status = status;
            return this;
        }

        public EpicBuilder withStartDate(Date date) {
            newEpic.startTime = date;
            return this;
        }

        public EpicBuilder withDuration(long duration) {
            newEpic.duration = duration;
            return this;
        }

        public EpicBuilder withSubTasksIds(List<Integer> ids) {
            newEpic.subtaskIdsList.addAll(ids);
            return this;
        }

        public EpicBuilder withEndDate(Date date) {
            newEpic.endTime = date;
            return this;
        }

        public Epic build() {
            return newEpic;
        }

    }

    private List<Integer> subtasksIdToIntgerList(String subTaskIds) {
        var result = new ArrayList<Integer>();
        var values = subTaskIds.split(",");
        for (var value : values) {
            result.add(Integer.parseInt(value));
        }
        return result;
    }

}
