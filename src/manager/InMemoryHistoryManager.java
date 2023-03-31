package manager;

import java.util.*;

import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> table = new HashMap<>();
    private Node head;
    private Node tail;

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

    @Override
    public void add(Task task) {
        if (table.containsKey(task.getId())) {
            removeNode(table.get(task.getId()));
        }
        Node element = new Node();
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
}

class Node {
    private Task task;
    private Node prev;
    private Node next;

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }

    public Task getTask() {
        return task;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}