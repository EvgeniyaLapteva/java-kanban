package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import service.exception.TimeIntersectionException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {

    protected T taskManager;

    public void setTaskManager(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    public void shouldIncreaseCounterWhenCreateTask() {
        Task task = new Task("Увеличим счетчик", "", TaskStatus.NEW);

        taskManager.createTask(task);

        assertEquals(1, taskManager.getCounter(), "Счетчик не увеличивается");
    }

    @Test
    public void shouldIncreaseCounterWhenCreateEpic() {
        Epic epic = new Epic("Увеличим счетчик еще", "");

        taskManager.createEpic(epic);

        assertEquals(1, taskManager.getCounter(), "Счетчик не увеличивается");
    }

    @Test
    public void shouldIncreaseCounterWhenCreateSubtask() {
        taskManager.createEpic(new Epic("Увеличим счетчик еще", ""));
        SubTask subTask = new SubTask("Increase counter", "", TaskStatus.NEW, 1);

        taskManager.createSubtask(subTask);

        assertEquals(2, taskManager.getCounter(), "Счетчик не увеличивается");
    }

    @Test
    public void shouldCreateTask() {
        Task task = new Task("Задача1", "Задача для теста", TaskStatus.NEW);
        taskManager.createTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask,"Задачи не совпадают");
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = new Epic("Epic1", "Epic for test");
        taskManager.createEpic(epic);
        final int epicId = epic.getId();

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic,"Эпики не совпадают");
    }

    @Test
    public void shouldGetSubTaskIdList() {
        Epic epic = new Epic("Epic1", "Epic for test");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("subtask", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);

        final int subtaskIdListSize = epic.getSubTaskIdList().size();

        assertEquals(1, subtaskIdListSize, "Подзадача не добавлена в эпик");
    }

    @Test
    public void shouldCreateSubTask() {
        taskManager.createSubtask(new SubTask("withoutEpic", "description", TaskStatus.NEW, 1));
        Epic epic = new Epic("Epic1", "Epic for test");
        SubTask subTask = new SubTask("Subtask1", "for test", TaskStatus.NEW, 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        final int subTaskId = subTask.getId();

        final SubTask savedSabTask = taskManager.getSubtaskById(subTaskId);

        assertNotNull(savedSabTask, "Подзадача не найдена");
        assertEquals(subTask, savedSabTask,"Подзадачи не совпадают");
        assertNotNull((Integer) subTask.getEpicId(), "Эпик не найден");
        assertEquals(1, subTask.getEpicId(), "Неверный номер эпика");
    }

    @Test
    public void shouldGetAllTasks() {
        ArrayList<Task> expectedList = new ArrayList<>();
        Task task = new Task("Task1", "", TaskStatus.NEW);
        Task task1 = new Task("Task2", "description", TaskStatus.IN_PROGRESS);
        expectedList.add(task);
        expectedList.add(task1);
        taskManager.createTask(task);
        taskManager.createTask(task1);

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(expectedList, tasks, "Списки задач не равны");
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    public void shouldGetAllEpics() {
        ArrayList<Epic> epicsForTest = new ArrayList<>();
        Epic epic = new Epic("Epic1", "");
        Epic epic1 = new Epic("Epic2", "description");
        epicsForTest.add(epic);
        epicsForTest.add(epic1);
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(epicsForTest, epics, "Списки эпиков не равны");
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(2, epics.size(), "Неверное количество Эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    public void shouldGetAllSubTasks() {
        ArrayList<SubTask> subTasksForTest = new ArrayList<>();
        taskManager.createEpic(new Epic("Epic1", ""));
        SubTask subTask = new SubTask("Subtask1", "", TaskStatus.NEW, 1);
        SubTask subTask1 = new SubTask("Subtask2", "description", TaskStatus.IN_PROGRESS, 1);
        subTasksForTest.add(subTask);
        subTasksForTest.add(subTask1);
        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask1);

        final List<SubTask> subTasks = taskManager.getAllSubtasks();

        assertEquals(subTasksForTest, subTasks, "Списки подзадач не равны");
        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач");
    }

    @Test
    public void shouldDeleteAllTasks() {
        taskManager.createTask(new Task("task1", "description", TaskStatus.NEW));
        taskManager.createTask(new Task("task2", "description2", TaskStatus.NEW));

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не удалены");
    }

    @Test
    public void shouldDeleteAllEpicsAndSubtasks() {
        taskManager.createEpic(new Epic("epic1", "description"));
        taskManager.createEpic(new Epic("epic2", "description2"));
        taskManager.createSubtask(new SubTask("Subtask", "description3", TaskStatus.NEW, 1));

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Эпики не удалены");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалены");
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Epic epic = new Epic("Epic", "for test");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("subtask", "for test", TaskStatus.NEW, 1);
        SubTask subTask1 = new SubTask("subtask2", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask1);

        taskManager.deleteAllSubTasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалены");
        assertEquals(0, epic.getSubTaskIdList().size(), "Подзадачи не удалены из эпиков");
    }

    @Test
    public void shouldGetTaskById() {
        Task task = new Task("task", "description1", TaskStatus.NEW);
        taskManager.createTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        final Task taskForTest = taskManager.getTaskById(1);

        assertEquals(savedTask, taskForTest, "По id передана не та задача");
    }

    @Test
    public void shouldGetEpicById() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        assertEquals(0, epic.getSubTaskIdList().size(), "Неверное количество подзадач");
        SubTask subTask = new SubTask("subtask", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);

        final Epic epicForTest = taskManager.getEpicById(1);

        assertEquals(savedEpic, epicForTest, "По id передан не тот эпик");
    }

    @Test
    public void shouldAddSubtasksIdToEpicWhenCreateSubtask() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        assertEquals(0, epic.getSubTaskIdList().size(), "Неверное количество подзадач");
        SubTask subTask = new SubTask("subtask", "description", TaskStatus.NEW, 1);

        taskManager.createSubtask(subTask);

        assertEquals(1, epic.getSubTaskIdList().size(), "Подзадача не добавлена в эпик");
    }

    @Test
    public void shouldGetSubTaskByID() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("subtask", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);
        final int subTaskId = subTask.getId();
        final SubTask savedSubTask = taskManager.getSubtaskById(subTaskId);

        final SubTask subTaskForTest = taskManager.getSubtaskById(2);

        assertEquals(savedSubTask, subTaskForTest, "По id передана не та подзадача");
    }

    @Test
    public void shouldUpdateTask() {
        Task previousTask = new Task("PreviousTask", "Description", TaskStatus.NEW);
        taskManager.createTask(previousTask);
        int previousTaskId = previousTask.getId();
        Task updatedTask = new Task("Updated task", "updated", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask, previousTaskId);

        Task result = taskManager.getTaskById(previousTaskId);

        assertEquals(updatedTask, result, "Задача не обновлена");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic previousEpic = new Epic("Previous epic", "description");
        taskManager.createEpic(previousEpic);
        int previousEpicID = previousEpic.getId();
        Epic updatedEpic = new Epic("Updated Epic", "updated");
        taskManager.updateEpic(updatedEpic, previousEpicID);

        Epic result = taskManager.getEpicById(previousEpicID);

        assertEquals(updatedEpic, result, "Эпик не обновлен");
    }

    @Test
    public void shouldUpdateSubtask() {
        taskManager.createEpic(new Epic("Epic", ""));
        SubTask previousSubtask = new SubTask("Subtask", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(previousSubtask);
        int previousSubtaskId = previousSubtask.getId();
        SubTask updatedSubtask = new SubTask("Updated", "updated", TaskStatus.IN_PROGRESS,
                previousSubtask.getEpicId());
        taskManager.updateSubTask(updatedSubtask, previousSubtaskId);

        SubTask result = taskManager.getSubtaskById(previousSubtaskId);

        assertEquals(updatedSubtask, result, "Подзадача не обновлена");
    }

    @Test
    public void shouldDeleteTaskById() {
        taskManager.createTask(new Task("Task1", "", TaskStatus.NEW));
        taskManager.createTask(new Task("Task2", "test2", TaskStatus.IN_PROGRESS));
        taskManager.createTask(new Task("Task3", "test3", TaskStatus.DONE));
        final List<Task> tasks = taskManager.getAllTasks();
        Task forRemove = taskManager.getTaskById(2);
        tasks.remove(forRemove);

        taskManager.deleteTaskById(2);
        final List<Task> afterRemoveByMethod = taskManager.getAllTasks();

        assertEquals(tasks, afterRemoveByMethod, "Удалена не та задача");
    }

    @Test
    public void shouldDeleteEpicByID() {
        taskManager.createEpic(new Epic("Epic1", "test1"));
        taskManager.createEpic(new Epic("Epic2", "test2"));
        taskManager.createEpic(new Epic("Epic3", "test3"));
        taskManager.createSubtask(new SubTask("Subtask", "", TaskStatus.IN_PROGRESS, 2));
        final List<Epic> epicsBeforeDeletion = taskManager.getAllEpics();
        Epic epicForRemove = taskManager.getEpicById(2);
        epicsBeforeDeletion.remove(epicForRemove);

        taskManager.deleteEpicById(2);
        final List<Epic> afterRemoveByMethod = taskManager.getAllEpics();

        assertEquals(epicsBeforeDeletion, afterRemoveByMethod, "Удален не тот эпик");
    }

    @Test
    public void shouldDeleteSubtaskByID() {
        taskManager.createEpic(new Epic("Epic", ""));
        taskManager.createSubtask(new SubTask("Subtask1", "test1", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("Subtask", "test2", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("Subtask3", "test3", TaskStatus.NEW, 1));
        final List<SubTask> subtasksBeforeDeletion = taskManager.getAllSubtasks();
        SubTask subtaskForDeletion = taskManager.getSubtaskById(2);
        subtasksBeforeDeletion.remove(subtaskForDeletion);

        taskManager.deleteSubTaskById(2);
        final List<SubTask> afterRemoveByMethod = taskManager.getAllSubtasks();

        assertEquals(subtasksBeforeDeletion, afterRemoveByMethod, "Удалена не та подзадача");
    }

    @Test
    public void  shouldThrowExceptionWithWrongTaskIdWhenDeleteTaskById() {
        Executable executable = () -> taskManager.deleteTaskById(1);

        IllegalArgumentException deleteException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует задача по выбранному идентификатору", deleteException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongTaskIdWhenGetTaskByID() {
        Executable executable = () -> taskManager.getTaskById(1);

        IllegalArgumentException getException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует задача по выбранному идентификатору", getException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUpdateTask() {
        Executable executable = () -> taskManager.updateTask(new Task("task", "", TaskStatus.NEW), 1);

        IllegalArgumentException updateException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует задача по выбранному идентификатору", updateException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongEpicIdWhenGetEpicById() {
        Executable executable = () -> taskManager.getEpicById(1);

        IllegalArgumentException getException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует эпик по выбранному идентификатору", getException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongEpicIdWhenUpdateEpic() {
        Executable executable = () -> taskManager.updateEpic(new Epic("epic", ""), 1);

        IllegalArgumentException updateException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует эпик по выбранному идентификатору", updateException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongEpicIdWhenDeleteEpicById() {
        Executable executable = () -> taskManager.deleteEpicById(1);

        IllegalArgumentException deleteException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует эпик по выбранному идентификатору", deleteException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongEpicIdWhenGetListOfSubTasksByEpic() {
        Executable executable = () -> taskManager.getListOfSubTasksByEpic(1);

        IllegalArgumentException getListException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует эпик по выбранному идентификатору", getListException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongSubtaskIdWhenGetSubtaskById() {
        Executable executable = () -> taskManager.getSubtaskById(1);

        IllegalArgumentException getException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует подзадача по выбранному идентификатору", getException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongSubtaskIdWhenUpdateSubtask() {
        Executable executable = () -> taskManager.updateSubTask(new SubTask("subtask", "", TaskStatus.NEW,
                2), 1);
        IllegalArgumentException updateException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует подзадача по выбранному идентификатору", updateException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWithWrongSubtaskIdWhenDeleteSubtaskById() {
        Executable executable = () -> taskManager.deleteSubTaskById(1);

        IllegalArgumentException deleteException = assertThrows(IllegalArgumentException.class, executable);

        assertEquals("Отсутствует подзадача по выбранному идентификатору", deleteException.getMessage());
    }

    @Test
    public void shouldGetListOfSubTasksByEpic() {
        taskManager.createEpic(new Epic("Epic", "description"));
        SubTask subTask = new SubTask("Subtask", "test1", TaskStatus.NEW, 1);
        SubTask subTask1 = new SubTask("Subtask2", "test2", TaskStatus.NEW, 1);
        final List<SubTask> listExpected = new ArrayList<>();
        listExpected.add(subTask);
        listExpected.add(subTask1);
        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask1);

        final List<SubTask> resultList = taskManager.getListOfSubTasksByEpic(1);

        assertEquals(listExpected, resultList, "Списки подзадач не совпадают");
    }

    @Test
    public void shouldUpdateEpicStatusToNew() {
        Epic epic = new Epic("Epic", "without tasks");

        taskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Статус при создании эпика без подзадач " +
                "не установлен");
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = new Epic("Epic", "without tasks");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("subtask1", "new", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("subtask2", "done", TaskStatus.DONE, 1);

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус эпика при создании позадач " +
                "со статусом NEW");
    }

    @Test
    public void shouldUpdateEpicStatusToDone() {
        Epic epic = new Epic("Epic", "without tasks");
        taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask("subtask1", "new", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("subtask2", "done", TaskStatus.DONE, 1);
        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);

        taskManager.updateSubTask(new SubTask("subtask1", "done", TaskStatus.DONE, 1), 2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Неверный статус при статусе всех подзадач DONE");
    }

    @Test
    public void testEpicStatusWhenSubtasksListIsEmpty() {
        Epic epic = new Epic("Epic", "without tasks");

        taskManager.createEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Статус при создании эпика без подзадач " +
                "не установлен");
    }

    @Test
    public void testEpicStatusWhenAllSubtasksStatusesNew() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);

        taskManager.createSubtask(new SubTask("subtask1", "new", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("subtask2", "new", TaskStatus.NEW, 1));

        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Неверный статус эпика при статусе всех " +
                "подзадач NEW");
    }

    @Test
    public void testEpicStatusWhenAllSubtasksStatusesDone() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);

        taskManager.createSubtask(new SubTask("subtask1", "DONE", TaskStatus.DONE, 1));
        taskManager.createSubtask(new SubTask("subtask2", "DONE", TaskStatus.DONE, 1));

        assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Неверный статус эпика при статусе всех " +
                "подзадач DONE");
    }

    @Test
    public void testEpicStatusWhenSubtaskStatusesNewAndDone() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);

        taskManager.createSubtask(new SubTask("subtask1", "new", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("subtask2", "DONE", TaskStatus.DONE, 1));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус эпика при статусе подзадач" +
                "NEW и DONE");
    }

    @Test
    public void testEpicStatusWhenAllSubtasksStatusesInProgress() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);

        taskManager.createSubtask(new SubTask("subtask1", "IN PROGRESS", TaskStatus.IN_PROGRESS, 1));
        taskManager.createSubtask(new SubTask("subtask2", "IN PROGRESS", TaskStatus.IN_PROGRESS, 1));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус эпика при статусе всех " +
                "подзадач IN_PROGRESS");
    }

    @Test
    public void shouldGetHistory() {
        Epic epic = new Epic("epic", "description");
        SubTask subTask = new SubTask("subtask", "for epic", TaskStatus.NEW, 1);
        Task task = new Task("task", "", TaskStatus.NEW);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        taskManager.createTask(task);
        final List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(epic);
        expectedResult.add(subTask);
        expectedResult.add(task);

        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getTaskById(3);
        final List<Task> result = taskManager.getHistory();

        assertEquals(expectedResult, result, "История вызовов сохранена неправильно");
    }

    @Test
    public void shouldPrioritizedTasks() {
        Task task = new Task("title", "timeTest", TaskStatus.NEW, 60,
                LocalDateTime.of(2023, 2, 23, 10,0));
        Task task1 = new Task("title2", "testTime", TaskStatus.NEW, 180,
                LocalDateTime.of(2023, 3, 30, 20,0));
        Epic epic = new Epic("epic", "");
        SubTask subTask = new SubTask("subtask", "", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 5, 15, 8, 0), 3);
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        final TreeSet<Task> result = taskManager.getPrioritizedTasks();

        final TreeSet<Task> expectedResult = new TreeSet<>((t1, t2) -> {
            if (t1.equals(t2)) {
                return 0;
            } else if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return Integer.compare(t1.getId(), t2.getId());
            } else if (t1.getStartTime() == null && t2.getStartTime() != null) {
                return 1;
            } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
                return -1;
            } else {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        });
        expectedResult.add(task);
        expectedResult.add(task1);
        expectedResult.add(epic);
        expectedResult.add(subTask);
        
        assertEquals(expectedResult, result, "Неправильно приоритезированы задачи");
        assertNotNull(result, "Трисет не создан");
    }

    @Test
    public void shouldCheckIntersectionAndThrowExceptionWhenCreateTask() {
        Task task = new Task("for time", "check", TaskStatus.NEW, 180,
                LocalDateTime.of(2023, 2, 23, 10, 0));
        Task task2 = new Task("", "intersection", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 23, 10, 10));
        Executable executable = () -> {
            taskManager.createTask(task);
            taskManager.createTask(task2);
        };

        TimeIntersectionException exception = assertThrows(TimeIntersectionException.class, executable);

        assertEquals("Невозможно создать задачу с выбранным периодом выполнения - " +
                "пересечение по времени с уже существующей задачей/подзадачей", exception.getMessage());
    }

    @Test
    public void shouldCheckIntersectionAndThrowExceptionWhenCreateSubtask() {
        taskManager.createEpic(new Epic("epic", "description"));
        SubTask subTask = new SubTask("for time", "check", TaskStatus.NEW, 180,
                LocalDateTime.of(2023, 2, 23, 10, 0), 1);
        SubTask subTask1 = new SubTask("", "intersection", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 23, 10, 10), 1);
        Executable executable = () -> {
            taskManager.createSubtask(subTask);
            taskManager.createSubtask(subTask1);
        };

        TimeIntersectionException exception = assertThrows(TimeIntersectionException.class, executable);

        assertEquals("Невозможно создать задачу с выбранным периодом выполнения - " +
                "пересечение по времени с уже существующей задачей/подзадачей", exception.getMessage());
    }
}
