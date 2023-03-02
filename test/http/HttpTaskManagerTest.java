package http;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {

    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void startServer() {
        try {
            server = new KVServer();
            server.start();
            manager = Managers.getDefaultHttpManager();
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при запуске сервера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldSaveTasks() {
        Task task = new Task("Task1", "description", TaskStatus.NEW, 30,
                LocalDateTime.of(2023, 5, 10, 15, 5));
        Task task1 = new Task("Task2", "test", TaskStatus.NEW, 10,
                LocalDateTime.of(2023, 10, 10, 10, 10));
        manager.createTask(task);
        manager.createTask(task1);
        manager.getTaskById(task.getId());
        manager.getTaskById(task1.getId());

        List<Task> tasks = manager.getHistory();

        assertEquals(manager.getAllTasks(), tasks, "Задачи не сохраняются");
    }

    @Test
    public void shouldSaveSubtasks() {
        Epic epic = new Epic("epic", "test");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("subtask", "for test", TaskStatus.NEW, 5,
                LocalDateTime.of(2023, 3, 1, 10, 0), epic.getId());
        SubTask subTask2 = new SubTask("subtask2", "for test2", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 1, 10, 0), epic.getId());
        manager.createSubtask(subTask);
        manager.createSubtask(subTask2);
        manager.getSubtaskById(subTask.getId());
        manager.getSubtaskById(subTask2.getId());

        List<Task> subtasks = manager.getHistory();

        assertEquals(manager.getAllSubtasks(), subtasks, "Подзадачи не сохраняются");
    }

    @Test
    public void shouldSaveEpics() {
        Epic epic = new Epic("epic", "test");
        Epic epic2 = new Epic("epic2", "test2");
        manager.createEpic(epic);
        manager.createEpic(epic2);
        manager.getEpicById(epic.getId());
        manager.getEpicById(epic2.getId());

        List<Task> epics = manager.getHistory();

        assertEquals(manager.getAllEpics(), epics, "Эпики не сохраняются");
    }
}