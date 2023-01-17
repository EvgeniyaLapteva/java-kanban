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
        Epic epic = new Epic("Эпик 1", "Эпик с 3 подзадачами", TaskStatus.NEW);
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Подзадача 1", "Для эпика 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask);
        SubTask subTask1 = new SubTask("Подзадача 2", "Еще для 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 3", "Для эпика 1", TaskStatus.NEW, 3);
        taskManager.createSubtask(subTask2);
        Epic epic1 =new Epic("Эпик 2", "Без подзадач", TaskStatus.NEW);
        taskManager.createEpic(epic1);
        taskManager.getEpicById(3);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getTaskById(1);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getSubtaskById(4);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getEpicById(7);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getSubtaskById(5);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getTaskById(2);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getSubtaskById(6);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getTaskById(1);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.getEpicById(3);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.deleteTaskById(1);
        taskManager.getHistory();
        System.out.println("----------");
        taskManager.deleteEpicById(3);
        taskManager.getHistory();
        System.out.println("----------");
    }
}
