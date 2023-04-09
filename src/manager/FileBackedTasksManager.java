package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utils.ManagerSaveException;
import utils.RegistryUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static manager.InMemoryHistoryManager.historyToString;

public class FileBackedTasksManager extends InMemoryTaskManager {

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

//    @Override
//    public List<Task> getAllTasks() {
//        var result = super.getAllTasks();
//        save();
//        return result;
//    }

    @Override
    public Optional<Task> getTask(Integer id) {
        var result = super.getTask(id);
        save();
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
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        var result = super.getAllSubtasks();
        save();
        return result;
    }

    @Override
    public Optional<Subtask> getSubtask(Integer id) {
        var result = super.getSubtask(id);
        save();
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
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public List<Epic> getAllEpics() {
        var result = super.getAllEpics();
        save();
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

    public void save() throws ManagerSaveException {
        var infoOfAllTasks = new ArrayList<Task>();
        infoOfAllTasks.addAll(super.getAllTasks());
        infoOfAllTasks.addAll(super.getAllEpics());
        infoOfAllTasks.addAll(super.getAllSubtasks());
        try {
            RegistryUtils.writeToMemoryFile(
                    infoOfAllTasks,
                    historyToString(Managers.getDefaultHistory())
            );
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }


}