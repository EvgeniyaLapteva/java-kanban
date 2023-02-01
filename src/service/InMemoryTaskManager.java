package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements  TaskManager{
    protected int counter = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int increaseCounter() {
        ++counter;
        return counter;
    }

    @Override
    public void createTask(Task task) {
        int taskId = increaseCounter();
        task.setId(taskId);
        tasks.put(taskId, task);

    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = increaseCounter();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void createSubtask(SubTask subTask) {
        int subtaskId = increaseCounter();
        subTask.setId(subtaskId);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            subTasks.put(subtaskId,subTask);
            epic.setSubTaskIdList(subtaskId);
            updateEpicStatus(epic);
        } else {
            System.out.println("Эпик не существует, выберите верный эпик и повторите запись.");
            counter--;
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>(tasks.values());
        return  listOfTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>(epics.values());
        return listOfEpics;
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        ArrayList<SubTask> listOfSubtasks = new ArrayList<>(subTasks.values());
        return listOfSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
        System.out.println("Все эпики и подзадачи удалены");
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for(Epic epic: epics.values()) {
            epic.getSubTaskIdList().clear();
        }
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public  Epic getEpicById(int epicId) {
        if (epics.get(epicId) == null) {
            System.out.println("Эпика с таким id не существует");
        } else {
            historyManager.add(epics.get(epicId));
        }
        return epics.get(epicId);
    }
    @Override
    public  SubTask getSubtaskById(int subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (tasks.containsKey(taskId)) {
            task.setId(taskId);
            tasks.put(taskId, task);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        if (epics.containsKey(epicId)) {
            for (Integer subTaskId: epics.get(epicId).getSubTaskIdList()) {
                epic.setSubTaskIdList(subTaskId);
            }
            epic.setId(epicId);
            epics.put(epicId, epic);
            updateEpicStatus(epic);
        } else {
            System.out.println("Отсутствует эпик по выбранному идентификатору");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int subTaskID) {
        if (subTasks.containsKey(subTaskID)) {
            subTask.setId(subTaskID);
            subTasks.put(subTaskID, subTask);
            Epic epic = epics.get(subTask.getEpicId());
            updateEpicStatus(epic);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            historyManager.remove(taskId);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }

    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            for (Integer subTaskId: epics.get(epicId).getSubTaskIdList()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        } else {
            System.out.println("Отсутствует эпик по выбранному идентификатору");
        }
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            SubTask subTask = subTasks.get(subTaskId);
            Epic epic = epics.get(subTask.getEpicId());
            epic.getSubTaskIdList().remove((Integer) subTask.getEpicId());
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        } else {
            System.out.println("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasksByEpic(int epicID) {
        if (epics.containsKey(epicID)) {
            Epic epic = epics.get(epicID);
            ArrayList<SubTask> subTasks = new ArrayList<>();
            for (int i =0; i < epic.getSubTaskIdList().size(); i++) {
                subTasks.add(this.subTasks.get(epic.getSubTaskIdList().get(i)));
            }
            return subTasks;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubTaskIdList().size() == 0) {
                epic.setTaskStatus(TaskStatus.NEW);
            } else {
                ArrayList<SubTask> subTasks = new ArrayList<>();
                int subTaskCounterNew = 0;
                int subTaskCounterDone = 0;
                for (int i = 0; i < epic.getSubTaskIdList().size(); i++) {
                    subTasks.add(this.subTasks.get(epic.getSubTaskIdList().get(i)));
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
