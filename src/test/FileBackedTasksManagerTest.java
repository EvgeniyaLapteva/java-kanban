package test;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import service.FileBackedTasksManager;
import service.TaskManager;
import service.exception.ManagerSaveException;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("src/resources/historyForTest.csv");
        setTaskManager(new FileBackedTasksManager(file));
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(Path.of("src/resources/historyForTest.csv"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldCreateTask() {
        super.shouldCreateTask();
    }

    @Test
    void shouldIncreaseCounter() {
        super.shouldIncreaseCounter();
    }

    @Test
    void shouldCreateEpic() {
        super.shouldCreateEpic();
    }

    @Test
    void shouldCreateSubTask() {
        super.shouldCreateSubTask();
    }

    @Test
    void shouldCreateListOfAllTasks() {
        super.shouldCreateListOfAllTasks();
    }

    @Test
    void shouldCreateListOfAllEpics() {
        super.shouldCreateListOfAllEpics();
    }

    @Test
    void shouldCreateListOfAllSubTasks() {
        super.shouldCreateListOfAllSubTasks();
    }

    @Test
    void shouldDeleteAllTasks() {
        super.shouldDeleteAllTasks();
    }

    @Test
    void shouldDeleteAllEpicsAnSubtasks() {
        super.shouldDeleteAllEpicsAnSubtasks();
    }

    @Test
    void shouldDeleteAllSubtasks() {
        super.shouldDeleteAllSubtasks();
    }

    @Test
    void shouldGetTaskById() {
        super.shouldGetTaskById();
    }

    @Test
    void shouldGetEpicById() {
        super.shouldGetEpicById();
    }

    @Test
    void shouldGetSubTaskByID() {
        super.shouldGetSubTaskByID();
    }

    @Test
    void shouldUpdateTask() {
        super.shouldUpdateTask();
    }

    @Test
    void shouldUpdateEpic() {
        super.shouldUpdateEpic();
    }

    @Test
    void shouldUpdateSubtask() {
        super.shouldUpdateSubtask();
    }

    @Test
    void shouldDeleteTaskById() {
        super.shouldDeleteTaskById();
    }

    @Test
    void shouldDeleteEpicByID() {
        super.shouldDeleteEpicByID();
    }

    @Test
    void shouldDeleteSubtaskByID() {
        super.shouldDeleteSubtaskByID();
    }

    @Test
    void shouldThrowExceptionWithWrongTaskId() {
        super.shouldThrowExceptionWithWrongTaskId();
    }

    @Test
    void shouldThrowExceptionWithWrongEpicId() {
        super.shouldThrowExceptionWithWrongEpicId();
    }

    @Test
    void shouldThrowExceptionWithWrongSubtaskId() {
        super.shouldThrowExceptionWithWrongSubtaskId();
    }

    @Test
    void shouldGetListOfSubTasksByEpic() {
        super.shouldGetListOfSubTasksByEpic();
    }

    @Test
    void shouldUpdateEpicStatus() {
        super.shouldUpdateEpicStatus();
    }

    @Test
    void testEpicStatusWhenSubtasksListIsEmpty() {
        super.testEpicStatusWhenSubtasksListIsEmpty();
    }

    @Test
    void testEpicStatusWhenAllSubtasksStatusesNew() {
        super.testEpicStatusWhenAllSubtasksStatusesNew();
    }

    @Test
    void testEpicStatusWhenAllSubtasksStatusesDone() {
        super.testEpicStatusWhenAllSubtasksStatusesDone();
    }

    @Test
    void testEpicStatusWhenSubtaskStatusesNewAndDone() {
        super.testEpicStatusWhenSubtaskStatusesNewAndDone();
    }

    @Test
    void testEpicStatusWhenAllSubtasksStatusesInProgress() {
        super.testEpicStatusWhenAllSubtasksStatusesInProgress();
    }

    @Test
    void shouldGetHistory() {
        super.shouldGetHistory();
    }

    @Test
    void shouldPrioritizedTasks() {
        super.shouldPrioritizedTasks();
    }

    @Test
    void shouldCheckIntersectionAndThrowException() {
        super.shouldCheckIntersectionAndThrowException();
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        Task firstTask = new Task("Выполнить 6 ТЗ", "Разобраться с методами", TaskStatus.IN_PROGRESS);
        Epic firstEpic = new Epic("Сходить за покупками", "По пути в школу");

        taskManager.createTask(firstTask);
        taskManager.createEpic(firstEpic);

        FileBackedTasksManager loadedFromFile = FileBackedTasksManager.loadFromFile(file);

        assertEquals(List.of(firstTask), loadedFromFile.getAllTasks(), "Задачи не равны");
        assertEquals(List.of(firstEpic), loadedFromFile.getAllEpics(), "Эпики не равны");
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
}