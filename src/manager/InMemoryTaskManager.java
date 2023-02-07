package manager;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

/**
 * Менеджер управления задачами.
 */
public class InMemoryTaskManager implements TaskManager {

    /*
     * Счётчик идентификатра, обеспечивающий уникальность.
     */
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();

    public Integer generateId() {
        return idCounter.addAndGet(1);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public Task getTaskById(Integer id) {
        return taskMap.get(id);
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
    }

    @Override
    public void removeTaskById(Integer id) {
        taskMap.remove(id);
    }

    @Override
    public void createTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        taskMap.replace(task.getId(), task);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        return subtaskMap.get(id);
    }

    @Override
    public void removeAllSubtasks() {
        var epicsIds = subtaskMap.values()
                .stream()
                .map(Subtask::getEpicId)
                .collect(Collectors.toList());

        epicsIds.forEach(epicId -> {
            var epic = epicMap.get(epicId);
            epic.getSubtaskIdsList().clear();
            updateEpicOfSubtaskChanges(epic);
        });

        subtaskMap.clear();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        var removedSubtask = getSubtaskById(id);
        var epic = getEpicById(removedSubtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicOfSubtaskChanges(epic);
        subtaskMap.remove(id);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getEpicId() == null) {
            System.out.println("\nПри создании подзадачи не был указан эпик: " + subtask);
        } else {
            var epic = getEpicById(subtask.getEpicId());
            if (epic == null) {
                System.out.println("\nПри создании подзадачи был указан несуществющий эпик: " + subtask);
            } else {
                subtaskMap.put(subtask.getId(), subtask);
                epic.addSubTaskId(subtask.getId());
                updateEpicOfSubtaskChanges(epic);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtaskMap.containsKey(subtask.getId())) {
            System.out.println("\nОбновляется несуществющая задача: " + subtask);
        } else {
            subtaskMap.replace(subtask.getId(), subtask);
            var epic = getEpicById(subtask.getEpicId());
            updateEpicOfSubtaskChanges(epic);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public Epic getEpicById(Integer id) {
        return epicMap.get(id);
    }

    @Override
    public void removeAllEpics() {
        subtaskMap.clear();
        epicMap.clear();
    }

    @Override
    public void removeEpicById(Integer id) {
        if (!epicMap.containsKey(id)) {
            System.out.println("\nПопытка удаления несуществющего эпик: " + id);
        } else {
            var subtaskIds = new ArrayList<>(getEpicById(id).getSubtaskIdsList());
            subtaskIds.forEach(this::removeSubtaskById);
            epicMap.remove(id);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epicMap.containsKey(epic.getId())) {
            System.out.println("\nОбновляется несуществющий эпик: " + epic);
        } else {
            updateEpicOfSubtaskChanges(epic);
        }
    }

    public List<Subtask> getSubtaskOfEpic(Epic epic) {
        if (!epicMap.containsKey(epic.getId())) {
            System.out.println("\nПроизводится поиск подзадач несуществующего эпика: " + epic);
            return Collections.emptyList();
        } else {
            return subtaskMap.values()
                    .stream()
                    .filter(subtask -> subtask.getEpicId() == epic.getId())
                    .collect(Collectors.toList());
        }
    }

    private void updateEpicOfSubtaskChanges(Epic epic) {
        var statuses = getSubtaskOfEpic(epic).stream()
                .map(Subtask::getStatus)
                .collect(Collectors.toSet());
        TaskStatus taskStatus;
        if (statuses.size() == 1 && statuses.contains(TaskStatus.NEW) || epic.getSubtaskIdsList().isEmpty()) {
            taskStatus = TaskStatus.NEW;
        } else if (statuses.size() == 1 && statuses.contains(TaskStatus.DONE)) {
            taskStatus = TaskStatus.DONE;
        } else {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
        epic.setStatus(taskStatus);
        epicMap.replace(epic.getId(), epic);
    }

}