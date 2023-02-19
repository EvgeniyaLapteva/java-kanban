package service;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    public void beforeEach() {
        setHistoryManager(new InMemoryHistoryManager());
    }
}