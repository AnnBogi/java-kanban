package service;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;

import entity.Epic;
import entity.SubTask;
import entity.Task;

/**
 * Менеджер управления задачами.
 */
public class TaskService {

    private final Map<Integer, Task> taskMap = new HashMap<>();

    /**
     * Получение списка всех хранящихся задач.
     *
     * @return Возвращает список всех созданных задач.
     */
    public List<Task> getAllTask() {
        return new ArrayList<>(taskMap.values());
    }

    /**
     * Производит удаление задач.
     */
    public void removeAllTasks() {
        taskMap.clear();
    }

    /**
     * Создаёт простую задачу.
     *
     * @param name        Наименование задачи.
     * @param description Описание задачи.
     */
    public void createTask(String name, String description) {
        var newTask = new Task(name, description);
        taskMap.put(newTask.getId(), newTask);
    }

    /**
     * Создаёт эпик.
     *
     * @param name        Наименование задачи.
     * @param description Описание задачи.
     */
    public void createEpic(String name, String description) {
        var newTask = new Epic(name, description);
        taskMap.put(newTask.getId(), newTask);
    }

    /**
     * Создаёт  подзадачу.
     *
     * @param name        Наименование задачи.
     * @param description Описание задачи.
     * @param epicId      Идентификатор эпика, к которому будет прикреплена подзадача.
     */
    public void createSubtask(String name, String description, Integer epicId) {
        var targetEpic = getTaskById(epicId);
        /*
         * Так как подзадача не может существовать отдельно от эпика - предварительно необходимо найти задачу с типом 'Эпик'.
         * Если эпика с нужным идентификатором не было создано - подзадача не создастся.
         */
        if (targetEpic.isEmpty()) {
            System.out.println("Эпик с номером " + epicId + " не существует.");
        } else {
            var epic = (Epic) targetEpic.get();
            var subTask = epic.addSubTask(name, description);
            taskMap.put(subTask.getId(), subTask);
        }
    }

    /**
     * Предоставление задачи по её идентификатору.
     *
     * @param id Идентификатор задачи.
     * @return Возвращает задачу по её идентификатору.
     */
    public Optional<Task> getTaskById(Integer id) {
        return Optional.ofNullable(taskMap.get(id));
    }

    /**
     * Удаляет задачу по идентификатору.
     *
     * @param id Идентификатор задачи.
     */
    public void removeById(Integer id) {
        var optionalTask = getTaskById(id);
        /*
         * Для того что бы удалить задачу она должна была быть создана ранее.
         */
        if (optionalTask.isEmpty()) {
            System.out.println("Задача с номером " + id + " не существует.");
        } else {
            var task = optionalTask.get();
            if (task instanceof SubTask) {
                removeSubTask((SubTask) task);
            } else if (task instanceof Epic) {
                removeEpic((Epic) task);
            } else {
                taskMap.remove(task.getId());
            }
        }
    }

    /**
     * Обновляет задачу.
     *
     * @param updateTask
     *        Задача.
     */
    public void updateTask(Task updateTask) {
        if (updateTask instanceof SubTask) {
            updateSubTask((SubTask) updateTask);
        } else if (updateTask instanceof Epic) {
            updateEpic((Epic) updateTask);
        } else {
            taskMap.replace(updateTask.getId(), updateTask);
        }
    }

    /**
     * Предоставляет список подзадач определённого эпика.
     *
     * @param id
     *        Идентификатор эпика.
     *
     * @return Возвращает список подзадач.
     */
    public List<SubTask> getSubTasksOfEpic(Integer id) {
        var optionalTask = getTaskById(id);
        /*
         * Для того что бы удалить задачу она должна была быть создана ранее.
         */
        if (optionalTask.isEmpty()) {
            System.out.println("Задача с номером " + id + " не существует.");
        } else {
            var task = optionalTask.get();
            if (task instanceof Epic) {
                return ((Epic) task).getSubTaskList();
            } else {
                System.out.println("Задача не является эпиком.");
            }
        }
        return Collections.emptyList();
    }

    private void removeSubTask(SubTask subTask) {
        var epic = (Epic) taskMap.get(subTask.getEpicId());
        epic.removeSubTask(subTask);
        taskMap.remove(subTask.getId());
    }

    private void removeEpic(Epic epic) {
        var subTaskList = new ArrayList<>(epic.getSubTaskList());
        subTaskList.forEach(this::removeSubTask);
        taskMap.remove(epic.getId());
    }

    private void updateSubTask(SubTask subTask) {
        var epic = (Epic) taskMap.get(subTask.getEpicId());
        epic.updateSubTask(subTask);
        updateEpic(epic);
        taskMap.replace(subTask.getId(), subTask);
    }

    private void updateEpic(Epic epic) {
        epic.updateTask(epic);
        taskMap.replace(epic.getId(), epic);
    }

}