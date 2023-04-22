package canban.manager;

import java.util.List;
import java.util.Optional;

import canban.tasks.Task;

public interface HistoryManager {

    void add(Optional<? extends Task> task);

    void remove(int id);

    List<Task> getHistory();

}
