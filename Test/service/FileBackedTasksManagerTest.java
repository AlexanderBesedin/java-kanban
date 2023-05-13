package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final String HOME = System.getProperty("user.home");
    String child = "dev" + "/java-kanban/resources/testFBM.csv";
    File file = new File(HOME, child);
    @BeforeEach
    void setUp() {
        taskManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    void tearDown() throws IOException {
        InMemoryTaskManager.setLastId(0);
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        Files.deleteIfExists(file.toPath());
    }

    @Test
    void shouldReadEmptyFile() throws IOException { //загрузка файла без задач
        Files.createFile(file.toPath());
        FileBackedTasksManager.loadFromFile(file);

        assertTrue(taskManager.getListTasks().isEmpty()
                && taskManager.getListSubtasks().isEmpty()
                && taskManager.getListEpics().isEmpty());
    }

    @Test
    void shouldReadEpicFromFile() throws IOException { //Эпик без подзадач
        taskManager.createTask(new Epic(name, description));
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
        taskManager.createTask(new Epic(name, description));

        List<String> result = Files.readAllLines(file.toPath());
        assertEquals("", result.get(result.size()-1));
    }
}