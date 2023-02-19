package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import service.exception.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("test/resources/historyForTest.csv");
        setTaskManager(new FileBackedTasksManager(file));
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(Path.of("test/resources/historyForTest.csv"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        Task firstTask = new Task("Выполнить 6 ТЗ", "Разобраться с методами", TaskStatus.IN_PROGRESS);
        Epic firstEpic = new Epic("Сходить за покупками", "По пути в школу");
        SubTask subTask = new SubTask("test", "test", TaskStatus.NEW, 2);
        taskManager.createTask(firstTask);
        taskManager.createEpic(firstEpic);
        taskManager.createSubtask(subTask);

        FileBackedTasksManager loadedFromFile = FileBackedTasksManager.loadFromFile(file);

        assertEquals(List.of(firstTask), loadedFromFile.getAllTasks(), "Задачи не равны");
        assertEquals(List.of(firstEpic), loadedFromFile.getAllEpics(), "Эпики не равны");
        assertEquals(List.of(subTask), loadedFromFile.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndLoadWhenEmptyHistory() {
        FileBackedTasksManager loadedFromFile = FileBackedTasksManager.loadFromFile(file);
        loadedFromFile.save();
        assertEquals(Collections.EMPTY_LIST, loadedFromFile.getHistory());
    }

    @Test
    public void shouldSaveAndLoadWhenManagerHasNoTasks() {
        FileBackedTasksManager loadedFromFile = FileBackedTasksManager.loadFromFile(file);
        loadedFromFile.save();
        assertEquals(Collections.EMPTY_LIST, loadedFromFile.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, loadedFromFile.getAllSubtasks());
        assertEquals(Collections.EMPTY_LIST, loadedFromFile.getAllEpics());
    }

    @Test
    public void shouldThrowExceptionWhenFileDoesNotExist() {
        file = new File("с/test.е");
        setTaskManager(new FileBackedTasksManager(file));
        Executable executable = () -> taskManager.save();
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, executable);

        assertEquals("Не удается выполнить сохранение в файл", exception.getMessage());
    }

    @Test
    public void shouldSaveAndLoadHistoryFromString() {
        Task task = new Task("title", "", TaskStatus.NEW);
        Epic epic = new Epic("epic", "");
        SubTask subTask = new SubTask("subtask", "", TaskStatus.IN_PROGRESS, 2);
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        List<Task> expectedResult = taskManager.getHistory();

        TaskManager loadedManager = FileBackedTasksManager.loadFromFile(file);
        List<Task> result = loadedManager.getHistory();

        assertEquals(expectedResult, result, "История просмотров восстановлена неправильно");
    }
}