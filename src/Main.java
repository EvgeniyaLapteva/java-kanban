import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
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
        System.out.println(taskManager.getAllTasks());
        System.out.println("----------");
        System.out.println(taskManager.getAllEpics());
        System.out.println("----------");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("----------");
        task = new Task("Обновление задачи", "Проверка обновления", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task, 1);
        epic = new Epic("Обновление Эпика", "Проверяем эпик", TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic, 3);
        subTask = new SubTask("Подзадача 1", "Проверка обновления", TaskStatus.DONE, 3);
        taskManager.updateSubTask(subTask, 4);
        subTask1 = new SubTask("Подзадача 2", "Проверка обновления", TaskStatus.DONE, 3);
        taskManager.updateSubTask(subTask1, 5);
        System.out.println(taskManager.getTaskById(1));
        System.out.println("----------");
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println("----------");
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println("----------");
        System.out.println(taskManager.getEpicById(3));
        System.out.println("----------");
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getAllTasks());
        System.out.println("----------");
        System.out.println(taskManager.getAllEpics());

    }
}
