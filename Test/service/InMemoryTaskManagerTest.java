package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    void tearDown() {
        InMemoryTaskManager.setLastId(0);
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
    }

    @Test
    void getLastId() {
    }

    @Test
    void setLastId() {
    }

    @Test
    void checkTimeOverlaps() {
    }

    @Test
    void updateEpicStatus() {
    }

    @Test
    void updateEpicDuration() {
    }
}