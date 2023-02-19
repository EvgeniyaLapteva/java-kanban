package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.exception.TimeIntersectionException;

import java.util.*;

public class InMemoryTaskManager implements  TaskManager{
    protected int counter = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.equals(task2)) {
            return 0;
        } else if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return 1;
        } else if (task1.getStartTime() == null && task2.getStartTime() != null) {
            return 1;
        } else if (task1.getStartTime() != null && task2.getStartTime() == null) {
            return -1;
        } else {
            return task1.getStartTime().compareTo(task2.getStartTime());
        }
    });

    @Override
    public int increaseCounter() {
        ++counter;
        return counter;
    }

    @Override
    public void createTask(Task task) {
        if (!checkIntersections(task)) {
            int taskId = increaseCounter();
            task.setId(taskId);
            tasks.put(taskId, task);
            setPrioritizedTasks(task);
        } else {
            throw new TimeIntersectionException("Невозможно создать задачу с выбранным периодом выполнения - " +
                    "пересечение по времени с уже существующей задачей/подзадачей");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = increaseCounter();
        epic.setId(epicId);
        epics.put(epicId, epic);
        updateEpic(epic, epicId);
    }

    @Override
    public void createSubtask(SubTask subTask) {
        if (!checkIntersections(subTask)) {
            int subtaskId = increaseCounter();
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                subTask.setId(subtaskId);
                subTasks.put(subtaskId, subTask);
                epic.setSubTaskIdList(subtaskId);
                epic.setSubTasksOfEpic(subTask);
                updateEpicStatus(epic);
                setPrioritizedTasks(subTask);
            } else {
                System.out.println("Эпик не существует, выберите верный эпик и повторите запись.");
                counter--;
            }
        } else {
            throw new TimeIntersectionException("Невозможно создать задачу с выбранным периодом выполнения - " +
                    "пересечение по времени с уже существующей задачей/подзадачей");
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>(tasks.values());
        for (Task task: listOfTasks) {
            historyManager.add(task);
        }
        return  listOfTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>(epics.values());
        for (Epic epic: listOfEpics) {
            historyManager.add(epic);
        }
        return listOfEpics;
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        ArrayList<SubTask> listOfSubtasks = new ArrayList<>(subTasks.values());
        for (SubTask subTask: listOfSubtasks) {
            historyManager.add(subTask);
        }
        return listOfSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task: tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic: epics.values()) {
            historyManager.remove(epic.getId());
            for (Integer subTaskID: epics.get(epic.getId()).getSubTaskIdList()) {
                historyManager.remove(subTaskID);
            }
        }
        epics.clear();
        subTasks.clear();
        System.out.println("Все эпики и подзадачи удалены");
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask: subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
        for(Epic epic: epics.values()) {
            epic.getSubTaskIdList().clear();
        }
        System.out.println("Все подзадачи удалены");
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.get(taskId) != null) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            throw new IllegalArgumentException("Отсутствует задача по выбранному идентификатору");
        }
    }

    @Override
    public  Epic getEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        } else {
            throw new IllegalArgumentException("Отсутствует эпик по выбранному идентификатору");
        }

    }

    @Override
    public  SubTask getSubtaskById(int subTaskId) {
        if (subTasks.get(subTaskId) != null) {
            historyManager.add(subTasks.get(subTaskId));
            return subTasks.get(subTaskId);
        } else {
            throw new IllegalArgumentException("Отсутствует подзадача по выбранному идентификатору");
        }
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (tasks.containsKey(taskId)) {
            task.setId(taskId);
            tasks.put(taskId, task);
            setPrioritizedTasks(task);
        } else {
            throw new IllegalArgumentException("Отсутствует задача по выбранному идентификатору");
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
            throw new IllegalArgumentException("Отсутствует эпик по выбранному идентификатору");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask, int subTaskID) {
        if (subTasks.containsKey(subTaskID)) {
            subTask.setId(subTaskID);
            subTasks.put(subTaskID, subTask);
            Epic epic = epics.get(subTask.getEpicId());
            updateEpicStatus(epic);
            setPrioritizedTasks(subTask);
        } else {
            throw new IllegalArgumentException("Отсутствует подзадача по выбранному идентификатору");
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            historyManager.remove(taskId);
        } else {
            throw new IllegalArgumentException("Отсутствует задача по выбранному идентификатору");
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
            throw new IllegalArgumentException("Отсутствует эпик по выбранному идентификатору");
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
            throw new IllegalArgumentException("Отсутствует подзадача по выбранному идентификатору");
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
            throw new IllegalArgumentException("Отсутствует эпик по выбранному идентификатору");
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
            epic.setEpicStartTime(epic);
            epic.setDurationTime(epic);
            epic.setEndTimeForEpic(epic);
        } else {
            System.out.println("Укажите уже созданный эпик");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public void setPrioritizedTasks(Task task) {
        if (task.getClass().equals(Task.class)) {
            prioritizedTasks.addAll(tasks.values());
        } else if (task.getClass().equals(SubTask.class)) {
            prioritizedTasks.addAll(subTasks.values());
        }
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return this.prioritizedTasks;
    }

    @Override
    public boolean checkIntersections(Task task) {
        boolean intersection = false;
        if (!prioritizedTasks.isEmpty()) {
            for (Task takInSet: prioritizedTasks) {
                if (takInSet.getStartTime() != null && takInSet.getEndTime() !=null) {
                    if (task.getStartTime().isAfter(takInSet.getEndTime()) ||
                        task.getEndTime().isBefore(takInSet.getStartTime())) {
                        intersection = false;
                    } else {
                        intersection = true;
                    }
                }
            }
        }
        return intersection;
    }

}
