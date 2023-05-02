package canban.server;

import canban.tasks.Task;

import canban.manager.FileBackedTasksManager;

import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient = new KVTaskClient();

    @Override
    public void save() {
        try {
            var orderedTask = getPrioritizedTasks();
            var token = kvTaskClient.register().body();

            orderedTask.forEach(task ->
                    saveInRepo(String.valueOf(task.getId()), (String) token, task)
            );

            var histIds = getHistory().stream().map(Task::getId).collect(Collectors.toList());
            saveInRepo("history", (String) token, histIds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveInRepo(String key, String token, Object body) {
        try {
            kvTaskClient.save(
                    key,
                    token,
                    body
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
