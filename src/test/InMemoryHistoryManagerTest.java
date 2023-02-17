package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    public void beforeEach() {
        setHistoryManager(new InMemoryHistoryManager());
    }

    @Test
    void addShouldAddTaskToHistory() {
        super.addShouldAddTaskToHistory();
    }

    @Test
    void shouldNotAddDuplicateToHistory() {
        super.shouldNotAddDuplicateToHistory();
    }

    @Test
    void shouldGetHistory() {
        super.shouldGetHistory();
    }

    @Test
    void ShouldRemoveFromHistoryByStart() {
        super.ShouldRemoveFromHistoryByStart();
    }

    @Test
    void ShouldRemoveFromHistoryByMiddle() {
        super.ShouldRemoveFromHistoryByMiddle();
    }

    @Test
    void shouldRemoveFromHistoryByEnd() {
        super.shouldRemoveFromHistoryByEnd();
    }
}