package canban.manager;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskStatus;
import canban.tasks.TaskType;
import canban.utils.SortedTasksUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    private final Set<Task> allTasks = new TreeSet<>(new SortedTasksUtil());

    public void addAllTasks(List<Task> taskList) {
        for (Task task : taskList) {
            if (task instanceof Epic) {
                epicMap.put(task.getId(), (Epic) task);
            } else if (task instanceof Subtask) {
                subtaskMap.put(task.getId(), (Subtask) task);
            } else {
                taskMap.put(task.getId(), task);
            }

            allTasks.add(task);
        }
    }

    @Override
    public Integer generateId() {
        return idCounter.addAndGet(1);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public Optional<Task> getTask(Integer id) {
        var task = Optional.ofNullable(taskMap.get(id));
        Managers.getDefaultHistory().add(task);
        return task;
    }

    @Override
    public void removeAllTasks() {
        allTasks.removeAll(taskMap.values());
        taskMap.clear();
    }

    @Override
    public void removeTaskById(Integer id) {
        allTasks.remove(taskMap.get(id));
        taskMap.remove(id);
    }

    @Override
    public void createTask(Task task) {
        if (validateCrossTaskExecution(task)) {
            return;
        }
        allTasks.add(task);
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        var updatedTask = Optional.ofNullable(taskMap.get(task.getId()));
        if (validateCrossTaskExecution(task) || updatedTask.isEmpty()) {
            return;
        }
        allTasks.remove(updatedTask.get());
        allTasks.add(task);
        taskMap.replace(task.getId(), task);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<Task> getHistory() {
        return Managers.getDefaultHistory().getHistory();
    }

    @Override
    public void removeHistoryById(int id) {
        Managers.getDefaultHistory().remove(id);
    }

    @Override
    public Optional<Subtask> getSubtask(Integer id) {
        var subtask = Optional.ofNullable(subtaskMap.get(id));
        Managers.getDefaultHistory().add(subtask);
        return subtask;
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

        allTasks.removeAll(subtaskMap.values());
        subtaskMap.clear();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        var removedSubtask = subtaskMap.get(id);
        var epic = epicMap.get(removedSubtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicOfSubtaskChanges(epic);

        allTasks.remove(removedSubtask);
        subtaskMap.remove(id);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (validateCrossTaskExecution(subtask)) {
            return;
        }
        if (subtask.getEpicId() == null) {
            System.out.println("\nПри создании подзадачи не был указан эпик: " + subtask);
        } else {
            var epic = epicMap.get(subtask.getEpicId());
            if (epic == null) {
                System.out.println("\nПри создании подзадачи был указан несуществющий эпик: " + subtask);
            } else {
                subtaskMap.put(subtask.getId(), subtask);
                allTasks.add(subtask);
                epic.addSubTaskId(subtask.getId());
                updateEpicOfSubtaskChanges(epic);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        var updatedSubtask = Optional.ofNullable(subtaskMap.get(subtask.getId()));
        if (validateCrossTaskExecution(subtask) || updatedSubtask.isEmpty()) {
            return;
        }
        subtaskMap.replace(subtask.getId(), subtask);
        allTasks.remove(updatedSubtask.get());
        allTasks.add(subtask);
        var epic = epicMap.get(subtask.getEpicId());
        updateEpicOfSubtaskChanges(epic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public Optional<Epic> getEpic(Integer id) {
        var epic = Optional.ofNullable(epicMap.get(id));
        Managers.getDefaultHistory().add(epic);
        return epic;
    }

    @Override
    public void removeAllEpics() {
        allTasks.removeAll(subtaskMap.values());
        subtaskMap.clear();
        allTasks.removeAll(epicMap.values());
        epicMap.clear();
    }

    @Override
    public void removeEpicById(Integer id) {
        if (!epicMap.containsKey(id)) {
            System.out.println("\nПопытка удаления несуществющего эпик: " + id);
        } else {
            var subtaskIds = new ArrayList<>(epicMap.get(id).getSubtaskIdsList());
            subtaskIds.forEach(this::removeSubtaskById);
            allTasks.remove(epicMap.get(id));
            epicMap.remove(id);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        allTasks.add(epic);
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

    @Override
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return allTasks;
    }

    protected boolean validateCrossTaskExecution(Task changedTask) {
        // Сначала производится поиск всех задач с типом ЗАДАЧА и ПОДЗАДАЧА, а затем происходит отбор пересекающихся
        // значений.
        var allCrossTasks = allTasks.stream()
                .filter(task -> task.getTaskType() != TaskType.EPIC)
                .filter(task -> task.getStartTime().getTime() <= changedTask.getEndTime().getTime() &&
                        task.getEndTime().getTime() >= changedTask.getStartTime().getTime())
                .collect(Collectors.toList());

        if (allCrossTasks.isEmpty()) {
            return false;
        }

        // Если же пересечения были найдены - необходимо проверить процесс обновления статуса.
        var filterCrossCurrentTask = allCrossTasks.stream()
                .filter(task -> Objects.equals(task.getId(), changedTask.getId()) &&
                        task.getTaskType().equals(changedTask.getTaskType()))
                .findAny();

        if (filterCrossCurrentTask.isPresent()) {
            return false;
        }

        System.out.println("Произошло пересечение при добаввлении/обновлении задачи: " + changedTask);

        return true;
    }

    private void updateEpicOfSubtaskChanges(Epic epic) {
        var subtasks = getSubtaskOfEpic(epic);
        epic.setStatus(getEpicUpdatedStatus(subtasks));
        var epicWithUpdatedDurationInfo = updateEpicDurationInfo(subtasks, epic);
        allTasks.remove(epic);
        allTasks.add(epicWithUpdatedDurationInfo);
        epicMap.replace(
                epic.getId(), epicWithUpdatedDurationInfo);
    }

    private TaskStatus getEpicUpdatedStatus(List<Subtask> subtasks) {
        var statuses = subtasks.stream()
                .map(Subtask::getStatus)
                .collect(Collectors.toSet());
        TaskStatus taskStatus;
        if (statuses.size() == 1 && statuses.contains(TaskStatus.NEW) || subtasks.isEmpty()) {
            taskStatus = TaskStatus.NEW;
        } else if (statuses.size() == 1 && statuses.contains(TaskStatus.DONE)) {
            taskStatus = TaskStatus.DONE;
        } else {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
        return taskStatus;
    }

    private Epic updateEpicDurationInfo(List<Subtask> subtasks, Epic epic) {
        if (subtasks.isEmpty()) {
            return epic;
        }
        var minStartDate = subtasks.stream().map(Subtask::getStartTime)
                .min(Comparator.naturalOrder());
        var maxEndDate = subtasks.stream().map(Subtask::getEndTime)
                .max(Comparator.naturalOrder());

        if (minStartDate.isEmpty() || maxEndDate.isEmpty()) {
            return epic;
        }
        var calcCalcDuration = (maxEndDate.get().getTime() - minStartDate.get().getTime()) / 1000 / 60;
        epic.setStartTime(minStartDate.get());
        epic.setDuration(calcCalcDuration);
        epic.setEndTime(maxEndDate.get());
        return epic;
    }

}