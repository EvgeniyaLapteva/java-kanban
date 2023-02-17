package test;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import service.TaskManager;
import service.exception.TimeIntersectionException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {

    T taskManager;

    public void setTaskManager(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task("Задача1", "Задача для теста", TaskStatus.NEW);
        taskManager.createTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask,"Задачи не совпадают");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldIncreaseCounter() {
        taskManager.createTask(new Task("Увеличим счетчик", "", TaskStatus.NEW));
        assertEquals(1, taskManager.getCounter(), "Счетчик не увеличивается");

        taskManager.createEpic(new Epic("Увеличим счетчик еще", ""));
        assertEquals(2, taskManager.getCounter(), "Счетчик не увеличивается");

        taskManager.createSubtask(new SubTask("Increase counter", "", TaskStatus.NEW, 2));
        assertEquals(3, taskManager.getCounter(), "Счетчик не увеличивается");
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = new Epic("Epic1", "Epic for test");
        taskManager.createEpic(epic);
        final int epicId = epic.getId();

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic,"Эпики не совпадают");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество Эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");

        SubTask subTask = new SubTask("subtask", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);
        assertEquals(1, epic.getSubTaskIdList().size(), "Подзадача не добавлена в эпик");
    }

    @Test
    void shouldCreateSubTask() {
        taskManager.createSubtask(new SubTask("withoutEpic", "description", TaskStatus.NEW, 1));
        Epic epic = new Epic("Epic1", "Epic for test");
        SubTask subTask = new SubTask("Subtask1", "for test", TaskStatus.NEW, 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        final int subTaskId = subTask.getId();
        final SubTask savedSabTask = taskManager.getSubtaskById(subTaskId);

        assertNotNull(savedSabTask, "Подзадача не найдена");
        assertEquals(subTask, savedSabTask,"Подзадачи не совпадают");

        final List<SubTask> subTasks = taskManager.getAllSubtasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают");

        assertNotNull((Integer) subTask.getEpicId(), "Эпик не найден");
        assertEquals(1, subTask.getEpicId(), "Неверный номер эпика");
    }

    @Test
    void shouldCreateListOfAllTasks() {
        ArrayList<Task> tasksForTest = new ArrayList<>();
        Task task = new Task("Task1", "", TaskStatus.NEW);
        Task task1 = new Task("Task2", "description", TaskStatus.IN_PROGRESS);

        tasksForTest.add(task);
        tasksForTest.add(task1);

        taskManager.createTask(task);
        taskManager.createTask(task1);

        assertEquals(tasksForTest, taskManager.getAllTasks(), "Списки задач не равны");
    }

    @Test
    void shouldCreateListOfAllEpics() {
        ArrayList<Epic> epicsForTest = new ArrayList<>();
        Epic epic = new Epic("Epic1", "");
        Epic epic1 = new Epic("Epic2", "description");

        epicsForTest.add(epic);
        epicsForTest.add(epic1);

        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        assertEquals(epicsForTest, taskManager.getAllEpics(), "Списки эпиков не равны");
    }

    @Test
    void shouldCreateListOfAllSubTasks() {
        ArrayList<SubTask> subTasksForTest = new ArrayList<>();
        taskManager.createEpic(new Epic("Epic1", ""));
        SubTask subTask = new SubTask("Subtask1", "", TaskStatus.NEW, 1);
        SubTask subTask1 = new SubTask("Subtask2", "description", TaskStatus.IN_PROGRESS, 1);

        subTasksForTest.add(subTask);
        subTasksForTest.add(subTask1);

        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask1);

        assertEquals(subTasksForTest, taskManager.getAllSubtasks(), "Списки подзадач не равны");
    }

    @Test
    void shouldDeleteAllTasks() {

        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пуст");

        taskManager.createTask(new Task("task1", "description", TaskStatus.NEW));
        taskManager.createTask(new Task("task2", "description2", TaskStatus.NEW));

        assertEquals(2, taskManager.getAllTasks().size(), "Неверное количество задач");

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не удалены");
    }

    @Test
    void shouldDeleteAllEpicsAnSubtasks() {
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пуст");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пуст");

        taskManager.createEpic(new Epic("epic1", "description"));
        taskManager.createEpic(new Epic("epic2", "description2"));
        taskManager.createSubtask(new SubTask("Subtask", "description3", TaskStatus.NEW, 1));

        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество подзадач");
        assertEquals(2, taskManager.getAllEpics().size(), "Неверное количество Эпиков");

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Эпики не удалены");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалены");
    }

    @Test
    void shouldDeleteAllSubtasks() {
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пуст");
        Epic epic = new Epic("Epic", "for test");
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("subtask", "for test", TaskStatus.NEW, 1);
        SubTask subTask1 = new SubTask("subtask2", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask1);

        assertEquals(2, epic.getSubTaskIdList().size(), "Количество подзадач в эпике не верно");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Неверное количество подзадач");
        assertEquals(epic.getSubTaskIdList().size(), taskManager.getAllSubtasks().size(), "Количество" +
                " подзадач не совпадает");

        taskManager.deleteAllSubTasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалены");
        assertEquals(0, epic.getSubTaskIdList().size(), "Подзадачи не удалены из эпиков");
    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task("task", "description1", TaskStatus.NEW);
        taskManager.createTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);
        final Task taskForTest = taskManager.getTaskById(1);
        assertEquals(savedTask, taskForTest, "По id передана не та задача");
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        assertEquals(0, epic.getSubTaskIdList().size(), "Неверное количество подзадач");
        SubTask subTask = new SubTask("subtask", "description", TaskStatus.NEW, 1);
        taskManager.createSubtask(subTask);
        assertEquals(1, epic.getSubTaskIdList().size(), "Подзадача не добавлена в эпик");
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);
        final Epic epicForTest = taskManager.getEpicById(1);
        assertEquals(savedEpic, epicForTest, "По id передан не тот эпик");
    }

    @Test
    void shouldGetSubTaskByID() {
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
    void shouldUpdateTask() {
        Task previousTask = new Task("PreviousTask", "Description", TaskStatus.NEW);
        taskManager.createTask(previousTask);
        int previousTaskId = previousTask.getId();
        Task updatedTask = new Task("Updated task", "updated", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask, previousTaskId);
        Task result = taskManager.getTaskById(previousTaskId);
        assertEquals(updatedTask, result, "Задача не обновлена");
    }

    @Test
    void shouldUpdateEpic() {
        Epic previousEpic = new Epic("Previous epic", "description");
        taskManager.createEpic(previousEpic);
        int previousEpicID = previousEpic.getId();
        Epic updatedEpic = new Epic("Updated Epic", "updated");
        taskManager.updateEpic(updatedEpic, previousEpicID);
        Epic result = taskManager.getEpicById(previousEpicID);
        assertEquals(updatedEpic, result, "Эпик не обновлен");
    }

    @Test
    void shouldUpdateSubtask() {
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
    void shouldDeleteTaskById() {
        taskManager.createTask(new Task("Task1", "test", TaskStatus.NEW));
        taskManager.createTask(new Task("Task2", "test2", TaskStatus.IN_PROGRESS));
        taskManager.createTask(new Task("Task3", "test3", TaskStatus.DONE));

        final List<Task> tasks = taskManager.getAllTasks();
        Task forRemove = taskManager.getTaskById(2);

        taskManager.deleteTaskById(2);
        tasks.remove(forRemove);

        final List<Task> afterRemoveByMethod = taskManager.getAllTasks();
        assertEquals(tasks, afterRemoveByMethod, "Удалена не та задача");
    }

    @Test
    void shouldDeleteEpicByID() {
        taskManager.createEpic(new Epic("Epic1", "test1"));
        taskManager.createEpic(new Epic("Epic2", "test2"));
        taskManager.createEpic(new Epic("Epic3", "test3"));
        taskManager.createSubtask(new SubTask("Subtask", "test", TaskStatus.IN_PROGRESS, 2));

        final List<Epic> epicsBeforeDeletion = taskManager.getAllEpics();
        Epic epicForRemove = taskManager.getEpicById(2);

        taskManager.deleteEpicById(2);
        epicsBeforeDeletion.remove(epicForRemove);

        final List<Epic> afterRemoveByMethod = taskManager.getAllEpics();
        assertEquals(epicsBeforeDeletion, afterRemoveByMethod, "Удален не тот эпик");
    }

    @Test
    void shouldDeleteSubtaskByID() {
        taskManager.createEpic(new Epic("Epic", "test"));
        taskManager.createSubtask(new SubTask("Subtask1", "test1", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("Subtask", "test2", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("Subtask3", "test3", TaskStatus.NEW, 1));

        final List<SubTask> subtasksBeforeDeletion = taskManager.getAllSubtasks();
        SubTask subtaskForDeletion = taskManager.getSubtaskById(2);

        taskManager.deleteSubTaskById(2);
        subtasksBeforeDeletion.remove(subtaskForDeletion);

        final List<SubTask> epicsListOfSubtasksIdsAfter = taskManager.getListOfSubTasksByEpic(1);
        final List<SubTask> afterRemoveByMethod = taskManager.getAllSubtasks();
        assertEquals(subtasksBeforeDeletion, afterRemoveByMethod, "Удалена не та подзадача");
    }

    @Test
    void  shouldThrowExceptionWithWrongTaskId() {
        IllegalArgumentException deleteException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.deleteTaskById(1);
                    }
                }
        );
        IllegalArgumentException getException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.getTaskById(1);
                    }
                }
        );
        IllegalArgumentException updateException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateTask(new Task("Task", "", TaskStatus.NEW), 1);
                    }
                }
        );
        assertEquals("Отсутствует задача по выбранному идентификатору", deleteException.getMessage());
        assertEquals("Отсутствует задача по выбранному идентификатору", getException.getMessage());
        assertEquals("Отсутствует задача по выбранному идентификатору", updateException.getMessage());
    }

    @Test
    void shouldThrowExceptionWithWrongEpicId() {
        IllegalArgumentException getException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.getEpicById(1);
                    }
                }
        );
        IllegalArgumentException updateException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateEpic(new Epic("epic", ""), 1);
                    }
                }
        );
        IllegalArgumentException deleteException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute(){
                        taskManager.deleteEpicById(1);
                    }
                }
        );
        IllegalArgumentException getListException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.getListOfSubTasksByEpic(1);
                    }
                }
        );
        assertEquals("Отсутствует эпик по выбранному идентификатору", getException.getMessage());
        assertEquals("Отсутствует эпик по выбранному идентификатору", updateException.getMessage());
        assertEquals("Отсутствует эпик по выбранному идентификатору", deleteException.getMessage());
        assertEquals("Отсутствует эпик по выбранному идентификатору", getListException.getMessage());
    }

    @Test
    void shouldThrowExceptionWithWrongSubtaskId() {
        IllegalArgumentException getException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.getSubtaskById(1);
                    }
                }
        );
        IllegalArgumentException updateException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.updateSubTask(new SubTask("subtask", "", TaskStatus.NEW,2),
                                1);
                    }
                }
        );
        IllegalArgumentException deleteException = assertThrows(
                IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.deleteSubTaskById(1);
                    }
                }
        );
        assertEquals("Отсутствует подзадача по выбранному идентификатору", getException.getMessage());
        assertEquals("Отсутствует подзадача по выбранному идентификатору", updateException.getMessage());
        assertEquals("Отсутствует подзадача по выбранному идентификатору", deleteException.getMessage());
    }

    @Test
    void shouldGetListOfSubTasksByEpic() {
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
    void shouldUpdateEpicStatus() {
        Epic epic = new Epic("Epic", "without tasks");
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Статус при создании эпика без подзадач " +
                "не установлен");

        SubTask subTask1 = new SubTask("subtask1", "new", TaskStatus.NEW, 1);
        SubTask subTask2 = new SubTask("subtask2", "done", TaskStatus.DONE, 1);

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус эпика при создании позадач " +
                "со статусом NEW");

        taskManager.updateSubTask(new SubTask("subtask1", "done", TaskStatus.DONE, 1), 2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Неверный статус при статусе всех подзадач DONE");
    }

    @Test
    void testEpicStatusWhenSubtasksListIsEmpty() {
        Epic epic = new Epic("Epic", "without tasks");
        taskManager.createEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Статус при создании эпика без подзадач " +
                "не установлен");
    }

    @Test
    void testEpicStatusWhenAllSubtasksStatusesNew() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubTask("subtask1", "new", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("subtask2", "new", TaskStatus.NEW, 1));

        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Неверный статус эпика при статусе всех " +
                "подзадач NEW");
    }

    @Test
    void testEpicStatusWhenAllSubtasksStatusesDone() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubTask("subtask1", "DONE", TaskStatus.DONE, 1));
        taskManager.createSubtask(new SubTask("subtask2", "DONE", TaskStatus.DONE, 1));

        assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Неверный статус эпика при статусе всех " +
                "подзадач DONE");
    }

    @Test
    void testEpicStatusWhenSubtaskStatusesNewAndDone() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubTask("subtask1", "new", TaskStatus.NEW, 1));
        taskManager.createSubtask(new SubTask("subtask2", "DONE", TaskStatus.DONE, 1));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус эпика при статусе подзадач" +
                "NEW и DONE");
    }

    @Test
    void testEpicStatusWhenAllSubtasksStatusesInProgress() {
        Epic epic = new Epic("Epic", "description");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubTask("subtask1", "IN PROGRESS", TaskStatus.IN_PROGRESS, 1));
        taskManager.createSubtask(new SubTask("subtask2", "IN PROGRESS", TaskStatus.IN_PROGRESS, 1));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Неверный статус эпика при статусе всех " +
                "подзадач IN_PROGRESS");
    }

    @Test
    void shouldGetHistory() {
        Epic epic = new Epic("epic", "description");
        SubTask subTask = new SubTask("subtask", "for epic", TaskStatus.NEW, 1);
        Task task = new Task("task", "test", TaskStatus.NEW);
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
    void shouldPrioritizedTasks() {
        Task task = new Task("title", "timeTest", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2023, 2, 23, 10,0));
        Task task1 = new Task("title2", "testTime", TaskStatus.NEW, Duration.ofMinutes(180),
                LocalDateTime.of(2023, 3, 30, 20,0));
        Epic epic = new Epic("epic", "test");
        SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2023, 5, 15, 8, 0), 3);
        Task task2 = new Task("1", "", TaskStatus.NEW);
        SubTask subTask1 = new SubTask("title", "", TaskStatus.NEW, 3);

        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);

        final TreeSet<Task> result = taskManager.getPrioritizedTasks();

        final TreeSet<Task> expectedResult = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return 1;
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
    void shouldCheckIntersectionAndThrowException() {
        Task task = new Task("for time", "check", TaskStatus.NEW, Duration.ofMinutes(180),
                LocalDateTime.of(2023, 2, 23, 10, 0));
        Task task2 = new Task("test", "intersection", TaskStatus.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2023, 2, 23, 10, 10));


        TimeIntersectionException exception = assertThrows(
                TimeIntersectionException.class, new Executable() {
                    @Override
                    public void execute() {
                        taskManager.createTask(task);
                        taskManager.createTask(task2);
                    }
                }
        );
        assertEquals("Невозможно создать задачу с выбранным периодом выполнения - " +
                "пересечение по времени с уже существующей задачей/подзадачей", exception.getMessage());
    }
}
