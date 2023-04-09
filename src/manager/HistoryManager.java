package manager;

import tasks.Task;

import java.util.List;
import java.util.Optional;

public interface HistoryManager {

    void add(Optional<? extends Task> task);

    void remove(int id);

    List<Task> getHistory();

}
