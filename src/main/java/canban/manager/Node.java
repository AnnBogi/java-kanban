package canban.manager;

import lombok.Data;

import canban.tasks.Task;

@Data
public class Node {

    private Task task;
    private Node prev;
    private Node next;

}
