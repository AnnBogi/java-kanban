package canban.utils;

import java.util.Comparator;

import canban.tasks.Task;

public class SortedTasksUtil implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            if (task1.getStartTime() == null && task2.getStartTime() != null) {
                return 1;
            } else if (task1.getStartTime() != null && task2.getStartTime() == null) {
                return -1;
            } else {
                return extracted(task1, task2);
            }
        }
        if (task1.getStartTime().after(task2.getStartTime())) {
            return 1;
        } else if (task1.getStartTime().before(task2.getStartTime())) {
            return -1;
        } else {
            return extracted(task1, task2);
        }
    }

    private static int extracted(Task task1, Task task2) {
        if (task1.getId() > task2.getId()) {
            return 1;
        } else if (task1.getId().equals(task2.getId())) {
            return 0;
        }
        return -1;
    }

}
