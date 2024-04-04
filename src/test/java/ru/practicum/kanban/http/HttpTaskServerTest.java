package ru.practicum.kanban.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.manager.HttpTaskManager;
import ru.practicum.kanban.service.manager.InMemoryTaskManager;
import ru.practicum.kanban.service.manager.TaskManager;
import ru.practicum.kanban.service.utils.Managers;
import ru.practicum.kanban.service.utils.TimeDurationUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private final static int THIS_YEAR = LocalDateTime.now().getYear();
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private final TimeDurationUtil timeFormat = new TimeDurationUtil(
            InMemoryTaskManager.TIME_PERIOD,
            InMemoryTaskManager.DATE_TIME_FORMATTER);
    private final Gson gson = Managers.getGson();
    private final String baseURL = "http://localhost:8080/tasks/";
    protected String name = "name";
    protected String description = "some description";

    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        kvServer.stop();
        httpTaskServer.stop();
    }

    //shouldGET
    @Test
    void shouldGetPrioritizedTaskListByTime() throws IOException, InterruptedException { // GET tasks/
        taskManager.createEpic(new Epic(name, description));//id#1
        Subtask subtask = new Subtask(
                name, description, 1,
                timeFormat.getFormatStartTime(String.format("12.05.%d 09:37", THIS_YEAR)),
                timeFormat.getFormatDuration("55")
        );
        taskManager.createSubtask(subtask);//id#2

        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime(String.format("12.05.%d 10:30", THIS_YEAR)),
                timeFormat.getFormatDuration("90")
        );
        taskManager.createTask(task1);//id#3

        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime(String.format("12.05.%d 12:08", THIS_YEAR)),
                timeFormat.getFormatDuration("30")
        );
        taskManager.createTask(task2);//id#4

        List<Task> list = List.of(
                subtask,
                task1,
                task2,
                makeTask(name, description) // StartTime = null
        );
        HttpResponse<String> response = createGetRequest("");

        assertEquals(gson.toJson(list), response.body());
    }

    @Test
    void shouldGetPrioritizedTaskListById() throws IOException, InterruptedException { // GET tasks/ (Null-time case)
        makeTask(name, description);
        makeTask(name, description);
        makeTask(name, description);
        Set<Task> list = taskManager.getPrioritizedTasks();
        HttpResponse<String> response = createGetRequest("");

        assertEquals(gson.toJson(list), response.body());
    }

    @Test
    void shouldGetErrorWhileUseIncorrectMethodEndpoint() throws IOException, InterruptedException {
        Task task = makeTask(name, description);
        task.setStatus(Status.IN_PROGRESS);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(baseURL + "task");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(body) // неиспользуемый метод
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Such an endpoint doesn't exist", response.body());
    }

    @Test
    void shouldGetListTasks() throws IOException, InterruptedException { // GET /tasks/task
        List<Task> list = List.of(
                makeTask(name, description),
                makeTask(name, description),
                makeTask(name, description)
        );
        HttpResponse<String> response = createGetRequest("task");

        assertEquals(gson.toJson(list), response.body());
    }

    @Test
    void shouldGetEmptyListTasks() throws IOException, InterruptedException { // GET /tasks/task (empty list case)
        new Task(name, description);
        HttpResponse<String> response = createGetRequest("task");
        assertEquals("No tasks available", response.body());
    }

    @Test
    void shouldGetListEpics() throws IOException, InterruptedException { // GET /tasks/epic
        List<Epic> list = List.of(
                makeEpic(name, description),
                makeEpic(name, description),
                makeEpic(name, description)
        );
        HttpResponse<String> response = createGetRequest("epic");

        assertEquals(gson.toJson(list), response.body());
    }

    @Test
    void shouldGetEmptyListEpics() throws IOException, InterruptedException { // GET /tasks/epic (empty list case)
        new Epic(name, description);
        HttpResponse<String> response = createGetRequest("epic");

        assertEquals("No epics available", response.body());
    }

    @Test
    void shouldGetListSubtasks() throws IOException, InterruptedException { // GET /tasks/subtask
        Epic epic = makeEpic(name, description);
        Epic epic1 = makeEpic(name, description);

        Subtask sub1 = new Subtask(name, description, epic.getId());
        Subtask sub2 = new Subtask(name, description, epic.getId());
        Subtask sub3 = new Subtask(name, description, epic1.getId());
        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);
        taskManager.createSubtask(sub3);
        HttpResponse<String> response = createGetRequest("subtask");

        assertEquals(gson.toJson(taskManager.getSubtasks()), response.body());
    }

    @Test
    void shouldGetEmptyListSubtasks() throws IOException, InterruptedException { // GET /tasks/subtask (empty list case)
        Epic epic = makeEpic(name, description);
        new Subtask(name, description, epic.getId());
        new Subtask(name, description, epic.getId());
        HttpResponse<String> response = createGetRequest("subtask");

        assertEquals("No subtasks available", response.body());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3
        HttpResponse<String> response = createGetRequest("task?id=3");

        assertEquals(gson.toJson(taskManager.getTask(3)), response.body());
    }

    @Test
    void shouldGetErrorAfterNotExistTaskId() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3
        HttpResponse<String> response = createGetRequest("task?id=5");

        assertEquals("Task with ID = 5 doesn't exist", response.body());
    }

    @Test
        //Возможно следует обработать с кастомным сообщением
    void shouldGetErrorAfterIncorrectTaskId() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3
        HttpResponse<String> response = createGetRequest("task?id=5de");

        assertEquals("<h1>400 Bad Request</h1>NumberFormatException thrown", response.body());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException { // GET /tasks/epic/?id=
        makeTask(name, description); // task id=1
        makeEpic(name, description); // epic id=2
        HttpResponse<String> response = createGetRequest("epic?id=2");

        assertEquals(gson.toJson(taskManager.getEpic(2)), response.body());
    }

    @Test
    void shouldGetErrorAfterNotExistEpicId() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeTask(name, description); // task id=1
        makeEpic(name, description); // epic id=2
        HttpResponse<String> response = createGetRequest("epic?id=1");

        assertEquals("Epic with ID = 1 doesn't exist", response.body());
    }

    @Test
//Возможно следует обработать с кастомным сообщением
    void shouldGetErrorAfterIncorrectEpicId() throws IOException, InterruptedException { // GET /tasks/epic/?id=
        makeTask(name, description); // task id=1
        makeEpic(name, description); // epic id=2
        HttpResponse<String> response = createGetRequest("epic?id=shc7");

        assertEquals("<h1>400 Bad Request</h1>NumberFormatException thrown", response.body());
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException { // GET /tasks/subtask/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3
        HttpResponse<String> response = createGetRequest("subtask?id=2");

        assertEquals(gson.toJson(taskManager.getSubtask(2)), response.body());
    }

    @Test
    void shouldGetErrorAfterNotExistSubtaskId() throws IOException, InterruptedException { // GET /tasks/subtask/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3
        HttpResponse<String> response = createGetRequest("subtask?id=3");

        assertEquals("Subtask with ID = 3 doesn't exist", response.body());
    }

    @Test
//Возможно следует обработать с кастомным сообщением
    void shouldGetErrorAfterIncorrectSubtaskId() throws IOException, InterruptedException { // GET /tasks/subtask/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3
        HttpResponse<String> response = createGetRequest("task?id=5q7e");

        assertEquals("<h1>400 Bad Request</h1>NumberFormatException thrown", response.body());
    }

    @Test
    void shouldGetSubtasksByEpicId() throws IOException, InterruptedException { // GET /tasks/subtask/epic?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        taskManager.createSubtask(new Subtask(name, description, 1));
        taskManager.createSubtask(new Subtask(name, description, 1));
        HttpResponse<String> response = createGetRequest("subtask/epic?id=1");

        assertEquals(gson.toJson(taskManager.getSubtasksOfEpic(1)), response.body());
    }

    @Test
    void shouldGetEmptyListSubtasksByEpicId() throws IOException, InterruptedException { // GET /tasks/subtask/epic/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        taskManager.createSubtask(new Subtask(name, description, 1));
        taskManager.createSubtask(new Subtask(name, description, 1));
        taskManager.deleteSubtasks();
        HttpResponse<String> response = createGetRequest("subtask/epic?id=1");

        assertEquals("An epic with ID = 1 doesn't have subtasks", response.body());
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException { // GET /tasks/history
        makeTask(name, description); //id=1
        makeSubtask(name, description); //epic id=2, subtask id=3
        List<Task> list = List.of(
                taskManager.getEpic(2),
                taskManager.getTask(1),
                taskManager.getSubtask(3)
        );
        HttpResponse<String> response = createGetRequest("history");

        assertEquals(gson.toJson(list), response.body());
    }

    @Test
    void shouldGetEmptyHistory() throws IOException, InterruptedException { // GET /tasks/history
        makeTask(name, description); //id=1
        makeSubtask(name, description); //epic id=2, subtask id=3
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        HttpResponse<String> response = createGetRequest("history");

        assertEquals(gson.toJson(taskManager.getHistory()), response.body());
    }

    //shouldDELETE

    @Test
    void shouldDeleteListTasks() throws IOException, InterruptedException { // DELETE /tasks/task
        makeTask(name, description);
        makeTask(name, description);
        makeTask(name, description);
        HttpResponse<String> response = createDeleteRequest("task");

        assertEquals("All tasks deleted", response.body());
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void shouldDeleteListSubtasks() throws IOException, InterruptedException { // DELETE /tasks/subtask
        makeSubtask(name, description);
        makeSubtask(name, description);
        makeSubtask(name, description);
        HttpResponse<String> response = createDeleteRequest("subtask");

        assertEquals("All subtasks deleted", response.body());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteListEpics() throws IOException, InterruptedException { // DELETE /tasks/epic
        makeSubtask(name, description);
        makeSubtask(name, description);
        makeSubtask(name, description);
        HttpResponse<String> response = createDeleteRequest("epic");

        assertEquals("All epics with subtasks have been deleted", response.body());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException { // DELETE /tasks/task/?id=
        makeTask(name, description);
        HttpResponse<String> response = createDeleteRequest("task?id=1");

        assertEquals("Task with ID = 1 has been deleted", response.body());
        assertNull(taskManager.getTask(1));
    }

    @Test
    void shouldGetErrorWhenDeleteTaskByNotExistId() throws IOException, InterruptedException { // DELETE /tasks/task/?id=
        makeTask(name, description);
        HttpResponse<String> response = createDeleteRequest("task?id=2");

        assertEquals("Task with ID = 2 doesn't exist", response.body());
        assertNull(taskManager.getTask(2));
    }

    @Test
// возможно нужно переписать с кастомным сообщением
    void shouldGetErrorWhenDeleteTaskByIncorrectId() throws IOException, InterruptedException { // DELETE /tasks/task/?id=
        makeTask(name, description);
        HttpResponse<String> response = createDeleteRequest("task?id=2asH");

        assertEquals("<h1>400 Bad Request</h1>NumberFormatException thrown", response.body());
        assertNotNull(taskManager.getTask(1));
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException { // DELETE /tasks/epic/?id=
        makeEpic(name, description);
        HttpResponse<String> response = createDeleteRequest("epic?id=1");

        assertEquals("Epic with ID = 1 has been deleted", response.body());
        assertNull(taskManager.getEpic(1));
    }

    @Test
    void shouldGetErrorWhenDeleteEpicByNotExistId() throws IOException, InterruptedException { // DELETE /tasks/epic/?id=
        makeEpic(name, description);
        HttpResponse<String> response = createDeleteRequest("epic?id=2");

        assertEquals("Epic with ID = 2 doesn't exist", response.body());
        assertNull(taskManager.getEpic(2));
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException { // DELETE /tasks/subtask/?id=
        makeSubtask(name, description);// epic id=1, subtask id=2
        HttpResponse<String> response = createDeleteRequest("subtask?id=2");

        assertEquals("Subtask with ID = 2 has been deleted", response.body());
        assertNull(taskManager.getSubtask(2));
    }

    @Test
    void shouldGetErrorWhenDeleteSubtaskByNotExistId() throws IOException, InterruptedException { // DELETE /tasks/subtask/?id=
        makeSubtask(name, description);// epic id=1, subtask id=2
        HttpResponse<String> response = createDeleteRequest("subtask?id=1");

        assertEquals("Subtask with ID = 1 doesn't exist", response.body());
        assertNull(taskManager.getSubtask(1));
    }

    //shouldPOST

    @Test
    void shouldPostCreateTask() throws IOException, InterruptedException {// POST /tasks/task
        Task task = new Task(name, description);
        HttpResponse<String> response = createPostRequest("task", gson.toJson(task));

        assertEquals(gson.toJson(taskManager.getTask(1)), response.body());
    }

    @Test
    void shouldPostUpdateTask() throws IOException, InterruptedException {// POST /tasks/task
        Task task = makeTask(name, description);
        task.setStatus(Status.IN_PROGRESS);
        HttpResponse<String> response = createPostRequest("task", gson.toJson(task));

        assertEquals(gson.toJson(taskManager.getTask(1)), response.body());
        assertEquals(taskManager.getTask(1).getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void shouldPostCreateEpic() throws IOException, InterruptedException {// POST /tasks/epic
        Epic epic = new Epic(name, description);
        HttpResponse<String> response = createPostRequest("epic", gson.toJson(epic));

        assertEquals(gson.toJson(taskManager.getEpic(1)), response.body());
    }

    @Test
    void shouldPostUpdateEpic() throws IOException, InterruptedException {// POST /tasks/epic
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        HttpResponse<String> response = createPostRequest("epic", gson.toJson(epic));

        assertEquals(gson.toJson(taskManager.getEpic(1)), response.body());
        assertEquals(taskManager.getEpic(1).getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void shouldPostCreateSubtask() throws IOException, InterruptedException {// POST /tasks/subtask
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        HttpResponse<String> response = createPostRequest("subtask", gson.toJson(subtask));

        assertEquals(gson.toJson(taskManager.getSubtask(2)), response.body());
    }

    @Test
    void shouldPostUpdateSubtask() throws IOException, InterruptedException {// POST /tasks/subtask
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        HttpResponse<String> response = createPostRequest("subtask", gson.toJson(subtask));

        assertEquals(gson.toJson(taskManager.getSubtask(2)), response.body());
        assertEquals(taskManager.getSubtask(2).getStatus(), Status.DONE);
    }

    @Test
    void shouldDoNotPostNullTask() throws IOException, InterruptedException { // POST /tasks/ null
        HttpResponse<String> response = createPostRequest("task", gson.toJson(null));
        assertEquals("Invalid request! The task could not be created or updated", response.body());
    }

    private Task makeTask(String name, String description) {
        Task task = new Task(name, description);
        taskManager.createTask(task);
        return task;
    }

    private Epic makeEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        return epic;
    }

    private Subtask makeSubtask(String name, String description) {
        Epic epic = makeEpic(name, description); //#1
        Subtask subtask = new Subtask(name, description, epic.getId());
        taskManager.createSubtask(subtask); //#2
        return subtask;
    }

    private HttpResponse<String> createGetRequest(String endpoint) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(baseURL + endpoint);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> createDeleteRequest(String endpoint) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(baseURL + endpoint);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> createPostRequest(String endpoint, String json) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(baseURL + endpoint);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}