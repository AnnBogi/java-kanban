package manager;

import java.util.LinkedList;
import java.util.List;

import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private static final List<Task> taskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        /*
         * Так как список изначально пуст, перед вставкой нового элемента проверяется
         * размерность списка. И если размерность списка равна 10 (ТЗ4 ст.3 п. "Обновление истории просмотров"),
         * то происходит удаление первого элемента.
         */
        if (taskHistory.size() > 10) {
            taskHistory.remove(0);
        }
        taskHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }

}