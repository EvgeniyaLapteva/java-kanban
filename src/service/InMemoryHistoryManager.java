package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> listOfViewedTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (listOfViewedTasks.size() == 10) {
            listOfViewedTasks.remove(0);
        }
        listOfViewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("Список просмотренных задач:");
        System.out.println(listOfViewedTasks);
        return listOfViewedTasks;
    }


}
