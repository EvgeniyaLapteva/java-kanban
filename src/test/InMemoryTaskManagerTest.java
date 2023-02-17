package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        setTaskManager(new InMemoryTaskManager());
    }

    @Test
    public void shouldCreateTask() {
        super.shouldCreateTask();
    }

    @Test
    public void shouldIncreaseCounter() {
        super.shouldIncreaseCounter();
    }

    @Test
    public void shouldCreateEpic() {
        super.shouldCreateEpic();
    }

    @Test
    public void shouldCreateSubTask() {
        super.shouldCreateSubTask();
    }

    @Test
    public void shouldCreateListOfAllTasks() {
        super.shouldCreateListOfAllTasks();
    }

    @Test
    public void shouldCreateListOfAllEpics() {
        super.shouldCreateListOfAllEpics();
    }

    @Test
    public void shouldCreateListOfAllSubTasks() {
        super.shouldCreateListOfAllSubTasks();
    }

    @Test
    public void shouldDeleteAllTasks() {
        super.shouldDeleteAllTasks();
    }

    @Test
    public void shouldDeleteAllEpicsAnSubtasks() {
        super.shouldDeleteAllEpicsAnSubtasks();
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        super.shouldDeleteAllSubtasks();
    }

    @Test
    public void shouldGetTaskById() {
        super.shouldGetTaskById();
    }

    @Test
    public void shouldGetEpicById() {
        super.shouldGetEpicById();
    }

    @Test
    public void shouldGetSubTaskByID() {
        super.shouldGetSubTaskByID();
    }

    @Test
    public void shouldUpdateTask() {
        super.shouldUpdateTask();
    }

    @Test
    public void shouldUpdateEpic() {
        super.shouldUpdateEpic();
    }

    @Test
    public void shouldUpdateSubtask() {
        super.shouldUpdateSubtask();
    }

    @Test
    public void shouldDeleteTaskById() {
        super.shouldDeleteTaskById();
    }

    @Test
    public void shouldDeleteEpicByID() {
        super.shouldDeleteEpicByID();
    }

    @Test
    void shouldDeleteSubtaskByID() {
        super.shouldDeleteSubtaskByID();
    }

    @Test
    public void shouldThrowExceptionWithWrongTaskId() {
        super.shouldThrowExceptionWithWrongTaskId();
    }

    @Test
    public void shouldThrowExceptionWithWrongEpicId() {
        super.shouldThrowExceptionWithWrongEpicId();
    }

    @Test
    public void shouldThrowExceptionWithWrongSubtaskId() {
        super.shouldThrowExceptionWithWrongSubtaskId();
    }

    @Test
    public void shouldGetListOfSubTasksByEpic() {
        super.shouldGetListOfSubTasksByEpic();
    }

    @Test
    public void shouldUpdateEpicStatus() {
        super.shouldUpdateEpicStatus();
    }

    @Test
    public void testEpicStatusWhenSubtasksListIsEmpty() {
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
}