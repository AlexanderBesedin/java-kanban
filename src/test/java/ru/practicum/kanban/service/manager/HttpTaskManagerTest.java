package ru.practicum.kanban.service.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.http.KVServer;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer kvServer;
    private static final int PORT = 8078;
    private static final String URL = "http://localhost:" + PORT + "/";

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager(URL);
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @Test
    void shouldLoadTasksAndHistoryFromServer() {
        Task task = makeTask(name, description); //task id#1
        Epic epic = makeEpic(name, description); //epic id#2
        Subtask subtask1 = new Subtask(name, description, 2); //subtask id#3
        Subtask subtask2 = new Subtask(name, description, 2); //subtask id#4
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        List<Task> expectedCreated = List.of(task, epic, subtask1, subtask2);

        List<Task> expectedHistory = List.of(
                taskManager.getTask(1),
                taskManager.getEpic(2),
                taskManager.getSubtask(4),
                taskManager.getSubtask(3)
        );

        taskManager = HttpTaskManager.loadFromServer(URL);

        List<Task> actual = new ArrayList<>();
        actual.addAll(taskManager.getTasks());
        actual.addAll(taskManager.getEpics());
        actual.addAll(taskManager.getSubtasks());

        assertEquals(expectedCreated.toString(), actual.toString());
        assertEquals(expectedHistory.toString(), taskManager.getHistory().toString());
        assertEquals(4, taskManager.id); // проверили восстановление максимального Id
    }

    @Test
    void shouldGetEmptyServer() {
        taskManager = HttpTaskManager.loadFromServer(URL);

        List<Task> actual = new ArrayList<>();
        actual.addAll(taskManager.getTasks());
        actual.addAll(taskManager.getEpics());
        actual.addAll(taskManager.getSubtasks());

        assertTrue(actual.isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }
}