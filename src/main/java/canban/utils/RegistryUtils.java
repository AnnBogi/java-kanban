package canban.utils;

import canban.manager.InMemoryHistoryManager;
import canban.manager.Managers;
import canban.tasks.Epic;
import canban.tasks.Subtask;
import canban.tasks.Task;
import canban.tasks.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class RegistryUtils {

    private RegistryUtils() {}

    public static void restoreMemory() {
        var records = new ArrayList<String>();
        try (var scanner = new Scanner(new File(loadRegistryProperty()))) {
            while (scanner.hasNextLine()) {
                records.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        records.forEach(value -> {
            if (value.contains(TaskType.SUBTASK.getName())) {
                Managers.getInMemoryTaskManager().createSubtask(new Subtask().fromString(value));
            } else if (value.contains(TaskType.EPIC.getName())) {
                Managers.getInMemoryTaskManager().createEpic(new Epic().fromString(value));
            } else if (value.contains(TaskType.TASK.getName())) {
                Managers.getInMemoryTaskManager().createTask(new Task().fromString(value));
            }
            if (records.indexOf(value) == records.size() - 1) {
                restoreHistory(value);
            }
        });
    }

    public static void writeToMemoryFile(List<Task> taskList, String history) throws IOException {
        try (var outputStreamWriter = new OutputStreamWriter(new FileOutputStream(loadRegistryProperty()), StandardCharsets.UTF_8);
             var writer = new PrintWriter(outputStreamWriter, false)) {
            var sb = "id,type,name,status,description,epic\n" +
                    getTaskInfo(taskList) +
                    history;
            writer.write(sb);
        } catch (IOException e) {
            throw e;
        }
    }

    static String getTaskInfo(List<Task> taskList) {
        var body = new StringBuilder();
        taskList.forEach(task -> {
            body.append(task.toString());
            body.append('\n');
        });
        return body.toString();
    }

    static String loadRegistryProperty() {
        var prop = new Properties();
        var loader = RegistryUtils.class.getClassLoader();

        try (var stream = loader.getResourceAsStream("config.properties")) {
            if (stream == null) {
                throw new FileNotFoundException();
            }
            prop.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty("registry.path");
    }

    static void restoreHistory(String value) {
        InMemoryHistoryManager.historyFromString(value).forEach(id -> {
            var task = Managers.getInMemoryTaskManager().getTask(id);
            if (task.isPresent()) {
                Managers.getDefaultHistory().add(task);
            }
            var epic = Optional.of(Managers.getInMemoryTaskManager().getEpic(id));
            epic.ifPresent(Managers.getDefaultHistory()::add);
            var subtask = Optional.of(Managers.getInMemoryTaskManager().getSubtask(id));
            subtask.ifPresent(Managers.getDefaultHistory()::add);
        });
    }

}