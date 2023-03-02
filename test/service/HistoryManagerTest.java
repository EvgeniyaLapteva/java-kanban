package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {

    protected T historyManager;

    public void setHistoryManager(T historyManager) {
        this.historyManager = historyManager;
    }

    @Test
    public void addShouldAddTaskToHistory() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void shouldNotAddDuplicateToHistory() {
        Task task = new Task("task1", "test1", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);

        final List<Task> result = historyManager.getHistory();
        final List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(task);

        assertEquals(expectedResult, result, "Дублирование добавления задач в историю просмотров");
    }

    @Test
    public void shouldGetHistory() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        task.setId(1);
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        task2.setId(2);
        historyManager.add(task);
        historyManager.add(task2);

        final List<Task> result = historyManager.getHistory();
        final List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(task);
        expectedResult.add(task2);

        assertEquals(expectedResult, result, "История сохраняется неправильно");
    }

    @Test
    public void ShouldRemoveFromHistoryByStart() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        task.setId(1);
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "description3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);

        final List<Task> result = historyManager.getHistory();
        final List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(task2);
        expectedResult.add(task3);

        assertEquals(expectedResult, result, "Удаление из начала списка истории просмотров происходит неверно");
    }

    @Test
    public void shouldRemoveFromHistoryByMiddle() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        task.setId(1);
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "description3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);

        final List<Task> result = historyManager.getHistory();
        final List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(task);
        expectedResult.add(task3);

        assertEquals(expectedResult, result, "Удаление из середины списка истории просмотров происходит " +
                "неверно");
    }

    @Test
    public void shouldRemoveFromHistoryByEnd() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        task.setId(1);
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("task3", "description3", TaskStatus.NEW);
        task3.setId(3);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);

        final List<Task> result = historyManager.getHistory();
        final List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(task);
        expectedResult.add(task2);

        assertEquals(expectedResult, result, "Удаление из конца списка истории просмотров происходит неверно");
    }
}