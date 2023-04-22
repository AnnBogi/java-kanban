package canban.manager;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;

import canban.utils.ManagerSaveException;
import canban.utils.RegistryUtils;

import static canban.manager.InMemoryHistoryManager.historyToString;

public class FileBackedTasksManager extends InMemoryTaskManager {

    @Override
    public List<Task> getAllTasks() {
        var result = super.getAllTasks();
        if (!result.isEmpty()) {
            save();
        }
        return result;
    }

    @Override
    public Optional<Task> getTask(Integer id) {
        var result = super.getTask(id);
        if (result.isPresent()) {
            save();
        }
        return result;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void createTask(Task task) {
        if (validateCrossTaskExecution(task)) {
            return;
        }
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        if (validateCrossTaskExecution(task)) {
            return;
        }
        super.updateTask(task);
        save();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        var result = super.getAllSubtasks();
        if (!result.isEmpty()) {
            save();
        }
        return result;
    }

    @Override
    public Optional<Subtask> getSubtask(Integer id) {
        var result = super.getSubtask(id);
        if (result.isPresent()) {
            save();
        }
        return result;
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (validateCrossTaskExecution(subtask)) {
            return;
        }
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (validateCrossTaskExecution(subtask)) {
            return;
        }
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public List<Epic> getAllEpics() {
        var result = super.getAllEpics();
        if (!result.isEmpty()) {
            save();
        }
        return result;
    }

    @Override
    public Optional<Epic> getEpic(Integer id) {
        var result = super.getEpic(id);
        save();
        return result;
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public List<Subtask> getSubtaskOfEpic(Epic epic) {
        var result = super.getSubtaskOfEpic(epic);
        save();
        return result;
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
