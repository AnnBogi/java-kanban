package canban.manager;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.utils.ManagerSaveException;
import canban.utils.RegistryUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static canban.manager.InMemoryHistoryManager.historyToString;

public class FileBackedTasksManager extends InMemoryTaskManager {

    protected final InMemoryTaskManager inMemoryTaskManager = Managers.getInMemoryTaskManager();

    @Override
    public List<Task> getAllTasks() {
        var result = inMemoryTaskManager.getAllTasks();
        if (!result.isEmpty()) {
            save();
        }
        return result;
    }

    @Override
    public Optional<Task> getTask(Integer id) {
        var result = inMemoryTaskManager.getTask(id);
        if (result.isPresent()) {
            save();
        }
        return result;
    }

    @Override
    public void removeAllTasks() {
        inMemoryTaskManager.removeAllTasks();
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        inMemoryTaskManager.removeTaskById(id);
        save();
    }

    @Override
    public void createTask(Task task) {
        if (validateCrossTaskExecution(task)) {
            return;
        }
        inMemoryTaskManager.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        if (validateCrossTaskExecution(task)) {
            return;
        }
        inMemoryTaskManager.updateTask(task);
        save();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        var result = inMemoryTaskManager.getAllSubtasks();
        if (!result.isEmpty()) {
            save();
        }
        return result;
    }

    @Override
    public Optional<Subtask> getSubtask(Integer id) {
        var result = inMemoryTaskManager.getSubtask(id);
        if (result.isPresent()) {
            save();
        }
        return result;
    }

    @Override
    public void removeAllSubtasks() {
        inMemoryTaskManager.removeAllSubtasks();
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        inMemoryTaskManager.removeSubtaskById(id);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (validateCrossTaskExecution(subtask)) {
            return;
        }
        inMemoryTaskManager.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (validateCrossTaskExecution(subtask)) {
            return;
        }
        inMemoryTaskManager.updateSubtask(subtask);
        save();
    }

    @Override
    public List<Epic> getAllEpics() {
        var result = inMemoryTaskManager.getAllEpics();
        if (!result.isEmpty()) {
            save();
        }
        return result;
    }

    @Override
    public Optional<Epic> getEpic(Integer id) {
        var result = inMemoryTaskManager.getEpic(id);
        save();
        return result;
    }

    @Override
    public void removeAllEpics() {
        inMemoryTaskManager.removeAllEpics();
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        inMemoryTaskManager.removeEpicById(id);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        inMemoryTaskManager.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        inMemoryTaskManager.updateEpic(epic);
        save();
    }

    @Override
    public List<Subtask> getSubtaskOfEpic(Epic epic) {
        var result = inMemoryTaskManager.getSubtaskOfEpic(epic);
        save();
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryTaskManager.getHistory();
    }

    @Override
    public void removeHistoryById(int id) {
        inMemoryTaskManager.removeHistoryById(id);
        save();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return inMemoryTaskManager.getPrioritizedTasks();
    }

    public void save() {
        try {
            RegistryUtils.writeToMemoryFile(
                    new ArrayList<>(getPrioritizedTasks()),
                    historyToString(Managers.getDefaultHistory())
            );
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save tasks and history to memory file: " + e.getMessage(), e);
        }
    }

}
