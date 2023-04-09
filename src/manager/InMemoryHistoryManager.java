package manager;

import tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> table = new HashMap<>();
    private Node head;
    private Node tail;

    public static String historyToString(HistoryManager manager) {
        var historyIds = manager.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        if (!historyIds.isEmpty()) {
            var result = new StringBuilder();
            for (int i = 0; i < historyIds.size(); i++) {
                result.append(historyIds.get(i));
                if (i < historyIds.size() - 1) {
                    result.append(",");
                }
            }
            return result.toString();
        }
        return "";
    }

    public static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Optional<? extends Task> optionalTask) {
        if (optionalTask.isEmpty()) {
            return;
        }
        var task = optionalTask.get();
        if (table.containsKey(task.getId())) {
            removeNode(table.get(task.getId()));
        }
        var element = new Node();
        element.setTask(task);
        linkLast(element.getTask());
        table.put(task.getId(), element);
    }

    @Override
    public void remove(int id) {
        removeNode(getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node element = new Node();
        element.setTask(task);
        if (table.containsKey(task.getId())) {
            removeNode(table.get(task.getId()));
        }
        if (head == null) {
            tail = element;
            head = element;
            element.setNext(null);
            element.setPrev(null);
        } else {
            element.setPrev(tail);
            element.setNext(null);
            tail.setNext(element);
            tail = element;
        }
        table.put(task.getId(), element);
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node element = head;
        while (element != null) {
            result.add(element.getTask());
            element = element.getNext();
        }
        return result;
    }

    private void removeNode(Node node) {
        if (node != null) {
            table.remove(node.getTask().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();
            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrev();
            }
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                next.setPrev(prev);
            }
        }
    }

    private Node getNode(int id) {
        return table.get(id);
    }

}
