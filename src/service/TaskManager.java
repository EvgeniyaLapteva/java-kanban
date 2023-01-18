package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int increaseCounter();

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubtask(SubTask subTask);

    ArrayList <Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubtaskById(int subTaskId);

    void updateTask(Task task, int taskId);

    void updateEpic(Epic epic, int epicId);

    void updateSubTask(SubTask subTask, int subTaskID);

    void deleteTaskById(int taskId);

    void deleteEpicById(int epicId);

    void deleteSubTaskById(int subTaskId);

    ArrayList<SubTask> getListOfSubTasksByEpic(int epicID);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}
