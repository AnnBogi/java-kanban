package canban.manager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;

public interface TaskManager {

    List<Task> getAllTasks();

    Optional<Task> getTask(Integer id);

    void removeAllTasks();

    void removeTaskById(Integer id);

    void createTask(Task task);

    void updateTask(Task task);

    List<Subtask> getAllSubtasks();

    Optional<Subtask> getSubtask(Integer id);

    void removeAllSubtasks();

    void removeSubtaskById(Integer id);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    List<Epic> getAllEpics();

    Optional<Epic> getEpic(Integer id);

    void removeAllEpics();

    void removeEpicById(Integer id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    List<Subtask> getSubtaskOfEpic(Epic epic);

    Integer generateId();

    List<Task> getHistory();

    void removeHistoryById(int id);

    Set<Task> getPrioritizedTasks();

}