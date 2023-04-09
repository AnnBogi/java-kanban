package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import tasks.Task;
import tasks.TaskType;

public final class RegistryUtils {

    private RegistryUtils() {}

    private static final String HEADER = "id,type,name,status,description,epic\n";

    private static final String REGISTRY_NAME = "memory.csv";

    public static void restoreMemory() {
        var records = new ArrayList<String>();
        try (var scanner = new Scanner(new File(REGISTRY_NAME))) {
            while (scanner.hasNextLine()) {
                records.add(scanner.nextLine());
            }
            restoreCalls(records);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeToMemoryFile(List<Task> taskList, String history) throws IOException {
        try (var writer = new PrintWriter(new FileOutputStream(REGISTRY_NAME, false))) {
            String sb = HEADER +
                    getTaskInfo(taskList) +
                    history;
            writer.write(sb);
        } catch (IOException e) {
            throw e;
        }
    }

    private static String getTaskInfo(List<Task> taskList) {
        var body = new StringBuilder();
        taskList.forEach(task -> {
            body.append(task.toString());
            body.append('\n');
        });
        return body.toString();
    }

    private static void restoreCalls(List<String> values) {
        values.forEach(value -> {
            if (value.contains(TaskType.TASK.getName())) {
                RestoreUtils.restoreTask(value);
            }
            if (value.contains(TaskType.EPIC.getName())) {
                RestoreUtils.restoreEpic(value);
            }
            if (value.contains(TaskType.SUBTASK.getName())) {
                RestoreUtils.restoreSubtask(value);
            }
            if (values.indexOf(value) == values.size() - 1) {
                RestoreUtils.restoreHistory(value);
            }
        });

    }

}
