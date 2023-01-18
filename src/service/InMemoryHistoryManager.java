package service;

import model.Task;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList listOfViewedTasks = new CustomLinkedList();

    @Override
    public void add(Task task) {
        listOfViewedTasks.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("Список просмотренных задач:");
        System.out.println(listOfViewedTasks.getTasks());
        return listOfViewedTasks.getTasks();
    }

    @Override
    public void remove(int id) {
        listOfViewedTasks.removeNode(listOfViewedTasks.getNodeByTaskId(id));
    }

    private class CustomLinkedList {
        private final Map<Integer, Node> memoryMap = new HashMap<>();

        private Node head;
        private Node tail;

        public void linkLast(Task task) {
            if (memoryMap.put(task.getId(), memoryMap.get(task.getId())) != null) {
                removeNode(memoryMap.get(task.getId()));
            }
            Node node = new Node(task, null, null);
            if (head == null) {
                head = node;
                tail = node;
                node.setNext(null);
                node.setPrev(null);
            } else {
                node.setPrev(tail);
                node.setNext(null);
                tail.setNext(node);
                tail = node;
            }
            memoryMap.put(task.getId(), node);
        }

        public List<Task> getTasks() {
            List<Task> listOfTasks = new ArrayList<>();
            Node newNode = head;
            while (newNode != null) {
                listOfTasks.add(newNode.getTask());
                newNode = newNode.getNext();
            }
            return listOfTasks;
        }

        public void removeNode(Node node) {
            if (node == head) {
                head = node.getNext();
            } else {
                node.getPrev().setNext(node.getNext());
            }
            if (node == tail) {
                tail = node.getPrev();
            } else {
                node.getNext().setPrev(node.getPrev());
            }
            memoryMap.remove(node.getTask().getId());
        }

        public Node getNodeByTaskId(int id) {
            return memoryMap.get(id);
        }
    }
}
