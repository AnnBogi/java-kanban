package utils;

import java.util.Optional;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public final class RestoreUtils {

    private RestoreUtils() {}

    private static final InMemoryTaskManager IN_MEMORY_TASK_MANAGER = Managers.getInMemoryTaskManager();
    private static final InMemoryHistoryManager HISTORY = Managers.getDefaultHistory();

    public static void restoreTask(String value) {
        var task = new Task();
        IN_MEMORY_TASK_MANAGER.createTask(task.fromString(value));
    }

    public static void restoreEpic(String value) {
        var epic = new Epic();
        IN_MEMORY_TASK_MANAGER.createTask(epic.fromString(value));
    }

    public static void restoreSubtask(String value) {
        var subtask = new Subtask();
        IN_MEMORY_TASK_MANAGER.createTask(subtask.fromString(value));
    }

    public static void restoreHistory(String value) {
        InMemoryHistoryManager.historyFromString(value).forEach(id -> {
            Optional<Task> task = IN_MEMORY_TASK_MANAGER.getTask(id);
            if (task.isPresent()) {
                HISTORY.add(task);
            }
            var epic = Optional.of(IN_MEMORY_TASK_MANAGER.getEpic(id));
            epic.ifPresent(HISTORY::add);
            var subtask = Optional.of(IN_MEMORY_TASK_MANAGER.getSubtask(id));
            subtask.ifPresent(HISTORY::add);
        });
    }

}