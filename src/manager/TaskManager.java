package manager;

import java.util.List;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


public interface TaskManager{

    List<Task> getAllTasks();

    Task getTaskById(Integer id);

    void removeAllTasks();

    void removeTaskById(Integer id);

    void createTask(Task task);

    void updateTask(Task task);


    List<Subtask> getAllSubtasks();

    Subtask getSubtaskById(Integer id);

    void removeAllSubtasks();

    void removeSubtaskById(Integer id);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    List<Epic> getAllEpics();

    Epic getEpicById(Integer id);

    void removeAllEpics();

    void removeEpicById(Integer id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    List<Subtask> getSubtaskOfEpic(Epic epic);

}