package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements  TaskManager{
    private int counter = 0;
    private final HashMap<Integer, Task> tasksHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasksHashMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int increaseCounter() {
        ++counter;
        return counter;
    }

    @Override
    public Task createTask(Task task) {
        int taskId = increaseCounter();
        task.setId(taskId);
        tasksHashMap.put(taskId, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int epicId = increaseCounter();
        epic.setId(epicId);
        epicHashMap.put(epicId, epic);
        return epic;
    }

    @Override
    public SubTask createSubtask(SubTask subTask) {
        int subtaskId = increaseCounter();
        subTask.setId(subtaskId);
        Epic epic = epicHashMap.get(subTask.getEpicId());
        if (epic != null) {
            subTasksHashMap.put(subtaskId,subTask);
            epic.setSubTaskIdList(subtaskId);
            updateEpicStatus(epic);
        } else {
            System.out.println("Эпик не существует, выберите верный эпик и повторите запись.");
            counter--;
        }
        return subTask;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>(tasksHashMap.values());
        return  listOfTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>(epicHashMap.values());
        return listOfEpics;
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        ArrayList<SubTask> listOfSubtasks = new ArrayList<>(subTasksHashMap.values());
        return listOfSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        tasksHashMap.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        epicHashMap.clear();
        subTasksHashMap.clear();
        System.out.println("Все эпики и подзадачи удалены");
    }

    @Override
    public void deleteAllSubTasks() {
        subTasksHashMap.clear();
        for(Epic epic: epicHashMap.values()) {
            epic.getSubTaskIdList().clear();
        }
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasksHashMap.get(taskId));
        return tasksHashMap.get(taskId);
    }

    @Override
    public  Epic getEpicById(int epicId) {
        historyManager.add(epicHashMap.get(epicId));
        return epicHashMap.get(epicId);
    }
    @Override
    public  SubTask getSubtaskById(int subTaskId) {
        historyManager.add(subTasksHashMap.get(subTaskId));
        return subTasksHashMap.get(subTaskId);
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (tasksHashMap.containsKey(taskId)) {
            task.setId(taskId);
            tasksHashMap.put(taskId, task);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        if (epicHashMap.containsKey(epicId)) {
            for (Integer subTaskId: epicHashMap.get(epicId).getSubTaskIdList()) {
                epic.setSubTaskIdList(subTaskId);
            }
            epic.setId(epicId);
            epicHashMap.put(epicId, epic);
            updateEpicStatus(epic);
        } else {
            System.out.println("Отсутствует эпик по выбранному идентификатору");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int subTaskID) {
        if (subTasksHashMap.containsKey(subTaskID)) {
            subTask.setId(subTaskID);
            subTasksHashMap.put(subTaskID, subTask);
            Epic epic = epicHashMap.get(subTask.getEpicId());
            updateEpicStatus(epic);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (tasksHashMap.containsKey(taskId)) {
            tasksHashMap.remove(taskId);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epicHashMap.containsKey(epicId)) {
            for (Integer subTaskId: epicHashMap.get(epicId).getSubTaskIdList()) {
                subTasksHashMap.remove(subTaskId);
            }
            epicHashMap.remove(epicId);
        } else {
            System.out.println("Отсутствует эпик по выбранному идентификатору");
        }
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        if (subTasksHashMap.containsKey(subTaskId)) {
            SubTask subTask = subTasksHashMap.get(subTaskId);
            Epic epic = epicHashMap.get(subTask.getEpicId());
            epic.getSubTaskIdList().remove((Integer) subTask.getEpicId());
            subTasksHashMap.remove(subTaskId);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasksByEpic(int epicID) {
        if (epicHashMap.containsKey(epicID)) {
            Epic epic = epicHashMap.get(epicID);
            ArrayList<SubTask> subTasks = new ArrayList<>();
            for (int i =0; i < epic.getSubTaskIdList().size(); i++) {
                subTasks.add(subTasksHashMap.get(epic.getSubTaskIdList().get(i)));
            }
            return subTasks;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            if (epic.getSubTaskIdList().size() == 0) {
                epic.setTaskStatus(TaskStatus.NEW);
            } else {
                ArrayList<SubTask> subTasks = new ArrayList<>();
                int subTaskCounterNew = 0;
                int subTaskCounterDone = 0;
                for (int i = 0; i < epic.getSubTaskIdList().size(); i++) {
                    subTasks.add(subTasksHashMap.get(epic.getSubTaskIdList().get(i)));
                }
                for (SubTask subTask : subTasks) {
                    if (subTask.getTaskStatus() == TaskStatus.NEW) {
                        subTaskCounterNew++;
                    }
                    if (subTask.getTaskStatus() == TaskStatus.DONE) {
                        subTaskCounterDone++;
                    }
                    if (subTask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                        epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                        return;
                    }
                }
                if (epic.getSubTaskIdList().size() == subTaskCounterNew) {
                    epic.setTaskStatus(TaskStatus.NEW);
                } else if (epic.getSubTaskIdList().size() == subTaskCounterDone) {
                    epic.setTaskStatus(TaskStatus.DONE);
                } else {
                    epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Укажите уже созданный эпик");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
