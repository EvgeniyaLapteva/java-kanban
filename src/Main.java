import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Задача 1", "Проверка", TaskStatus.NEW);
        taskManager.createTask(task);
        Task task1 = new Task("Задача 2", "Все еще проверяем", TaskStatus.NEW);
        taskManager.createTask(task1);
        Epic epic = new Epic("Эпик 1", "Проверочный эпик", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Для эпика 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask);
        SubTask subTask1 = new SubTask("Подзадача 2", "Еще для 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask1);
        Epic epic1 =new Epic("Эпик 2", "С 1 подзадачей", TaskStatus.NEW);
        taskManager.createEpic(epic1);
        SubTask subTask2 = new SubTask("Подзадача 3", "Для эпика 2", TaskStatus.NEW, 6);
        taskManager.createSubtask(subTask2);
        System.out.println("----------");
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);
        taskManager.getHistory();
        System.out.println("----------");
        System.out.println("----------");
        taskManager.getTaskById(2);
        taskManager.getEpicById(6);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getTaskById(2);
        taskManager.getHistory();
    }
}
