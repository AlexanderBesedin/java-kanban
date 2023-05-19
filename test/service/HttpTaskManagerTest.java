package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.http.KVServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
    void save() {
    }

    @Test
    void loadFromServer() {
    }
}