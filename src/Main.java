import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Задача 1", "Проверка", TaskStatus.NEW);
        taskManager.createTask(task);
        Task task1 = new Task("Задача 2", "Все еще проверяем", TaskStatus.NEW);
        taskManager.createTask(task1);
        Epic epic = new Epic("Эпик 1", "Эпик с 3 подзадачами");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Для эпика 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask);
        SubTask subTask1 = new SubTask("Подзадача 2", "Еще для 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 3", "Для эпика 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask2);
        Epic epic1 =new Epic("Эпик 2", "Без подзадач");
        taskManager.createEpic(epic1);
        Task task2 = new Task("title", "description", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2023, 2, 20, 8, 0));
        Task task3 = new Task("title2,", "time", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2023, 3, 15, 14, 0));
        Task task5 = new Task("subtask", "for time", TaskStatus.NEW, Duration.ofMinutes(25),
                LocalDateTime.of(2023, 3, 8, 10, 0));
        Task task14 = new Task("for time", "check", TaskStatus.NEW, Duration.ofMinutes(180),
                LocalDateTime.of(2023, 2, 23, 10, 0));
        Task task4 = new Task("", "intersection", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2023, 2, 23, 10, 10));
        Task task6 = new Task("1", "1", TaskStatus.NEW);
        taskManager.createTask(task6);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task5);
        taskManager.createTask(task14);

        System.out.println(epic.getStartTime());
        System.out.println(epic1.getStartTime());
        System.out.println(epic.getDuration());
        System.out.println(epic1.getDuration()); //0s
        System.out.println(epic.getEndTime());
        System.out.println(epic1.getEndTime()); //null
        taskManager.createTask(task4);
        System.out.println(taskManager.getPrioritizedTasks());
        
    }
}
