package ru.practicum.kanban.service.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final static File file = new File("src/test/resources/testFBM.csv");

    @BeforeEach
    void setUp() {
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    void tearDown() throws IOException {
        taskManager.id = 0;
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        Files.deleteIfExists(file.toPath());
    }

    @Test
    void shouldReadEmptyFile() throws IOException { //загрузка файла без задач
        Files.createFile(file.toPath());
        taskManager = FileBackedTasksManager.loadFromFile(file);

        assertTrue(taskManager.getTasks().isEmpty()
                && taskManager.getSubtasks().isEmpty()
                && taskManager.getEpics().isEmpty());
    }

    @Test
    void shouldReadEpicFromFile() throws IOException { //Эпик без подзадач
        taskManager.createEpic(new Epic(name, description));
        List<String> result = Files.readAllLines(file.toPath());

        assertEquals(
                List.of("id|type|name|status|description|start_time|duration|epic_or_subtasksID",
                        "1|EPIC|name|NEW|some description|null|null|",
                        ""),
                result
        );
    }

    @Test
    void shouldReadFileWhenHistoryEmpty() throws IOException { //Пустой список истории
        taskManager.createTask(new Task(name, description));
        taskManager.createTask(new Task(name, description));
        taskManager.createEpic(new Epic(name, description));
        List<String> result = Files.readAllLines(file.toPath());

        assertEquals("", result.get(result.size() - 1));
    }

    @Test //Проверка метода установки максимального id методом setLastId() при десериализации
    void shouldReadLastIdFromFile() {
        makeTask(name, description);// #id1
        makeSubtask(name, description);// #id2 - epic, id3 - subtask
        taskManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(3, taskManager.id);
    }
}