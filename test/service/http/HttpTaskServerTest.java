package service.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;
import service.utils.TimeDurationUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;
    private final TimeDurationUtil timeFormat = new TimeDurationUtil(
            InMemoryTaskManager.TIME_PERIOD,
            InMemoryTaskManager.DATE_TIME_FORMATTER);
    Gson gson = Managers.getDefaultGson();
    private final String baseURL = "http://localhost:8080/tasks/";
    protected String name = "name";
    protected String description = "some description";
    @BeforeEach
    void setUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = new HttpTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
    }

    @AfterEach
    void tearDown() {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        kvServer.stop();
        httpTaskServer.stop();
    }

    //shouldGET
    @Test
    void shouldGetPrioritizedTaskListByTime() throws IOException, InterruptedException { // GET tasks/
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);//id#1
        Subtask subtask = new Subtask(
                name, description, 1,
                timeFormat.getFormatStartTime("12.05.2023 09:37"),
                timeFormat.getFormatDuration("55")
        );
        taskManager.createSubtask(subtask);//id#2
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 10:30"),
                timeFormat.getFormatDuration("90")
        );
        taskManager.createTask(task1);//id#3
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:08"),
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

        assertEquals("Такого эндпоинта не существует", response.body());
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
        assertEquals("Задачи отсутствуют.", response.body());
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
        assertEquals("Эпики отсутствуют.", response.body());
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
        assertEquals(gson.toJson(taskManager.getListSubtasks()), response.body());
    }

    @Test
    void shouldGetEmptyListSubtasks() throws IOException, InterruptedException { // GET /tasks/subtask (empty list case)
        Epic epic = makeEpic(name, description);
        new Subtask(name, description, epic.getId());
        new Subtask(name, description, epic.getId());

        HttpResponse<String> response = createGetRequest("subtask");
        assertEquals("Подзадачи отсутствуют.", response.body());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3

        HttpResponse<String> response = createGetRequest("task/?id=3");
        assertEquals(gson.toJson(taskManager.getTask(3)), response.body());
    }

    @Test
    void shouldGetErrorAfterNotExistTaskId() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3

        HttpResponse<String> response = createGetRequest("task/?id=5");
        assertEquals("Задача с ID = 5 не существует.", response.body());
    }

    @Test
    void shouldGetErrorAfterIncorrectTaskId() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3

        HttpResponse<String> response = createGetRequest("task/?id=5de");
        assertEquals("Введен некорректный ID", response.body());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException { // GET /tasks/epic/?id=
        makeTask(name, description); // task id=1
        makeEpic(name, description); // epic id=2

        HttpResponse<String> response = createGetRequest("epic/?id=2");
        assertEquals(gson.toJson(taskManager.getEpic(2)), response.body());
    }

    @Test
    void shouldGetErrorAfterNotExistEpicId() throws IOException, InterruptedException { // GET /tasks/task/?id=
        makeTask(name, description); // task id=1
        makeEpic(name, description); // epic id=2

        HttpResponse<String> response = createGetRequest("epic/?id=1");
        assertEquals("Эпик с ID = 1 не существует.", response.body());
    }

    @Test
    void shouldGetErrorAfterIncorrectEpicId() throws IOException, InterruptedException { // GET /tasks/epic/?id=
        makeTask(name, description); // task id=1
        makeEpic(name, description); // epic id=2

        HttpResponse<String> response = createGetRequest("epic/?id=shc7");
        assertEquals("Введен некорректный ID", response.body());
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException { // GET /tasks/subtask/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3

        HttpResponse<String> response = createGetRequest("subtask/?id=2");
        assertEquals(gson.toJson(taskManager.getSubtask(2)), response.body());
    }

    @Test
    void shouldGetErrorAfterNotExistSubtaskId() throws IOException, InterruptedException { // GET /tasks/subtask/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3

        HttpResponse<String> response = createGetRequest("subtask/?id=3");
        assertEquals("Подзадача с ID = 3 не существует.", response.body());
    }

    @Test
    void shouldGetErrorAfterIncorrectSubtaskId() throws IOException, InterruptedException { // GET /tasks/subtask/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        makeTask(name, description); // task id=3

        HttpResponse<String> response = createGetRequest("task/?id=5q7e");
        assertEquals("Введен некорректный ID", response.body());
    }

    @Test
    void shouldGetSubtasksByEpicId() throws IOException, InterruptedException { // GET /tasks/subtask/epic/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        taskManager.createSubtask(new Subtask(name, description, 1));
        taskManager.createSubtask(new Subtask(name, description, 1));


        HttpResponse<String> response = createGetRequest("subtask/epic/?id=1");
        assertEquals(gson.toJson(taskManager.getSubtasksOfEpic(1)), response.body());
    }

    @Test
    void shouldGetEmptyListSubtasksByEpicId() throws IOException, InterruptedException { // GET /tasks/subtask/epic/?id=
        makeSubtask(name, description); // epic id=1, subtask id=2
        taskManager.createSubtask(new Subtask(name, description, 1));
        taskManager.createSubtask(new Subtask(name, description, 1));
        taskManager.clearSubtasks();

        HttpResponse<String> response = createGetRequest("subtask/epic/?id=1");
        assertEquals("У эпика с ID = 1 нет подзадач.", response.body());
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
        taskManager.clearTasks();
        taskManager.clearEpics();

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
        assertEquals("Все задачи удалены.", response.body());
        assertTrue(taskManager.getListTasks().isEmpty());
    }

    @Test
    void shouldDeleteListSubtasks() throws IOException, InterruptedException { // DELETE /tasks/subtask
        makeSubtask(name, description);
        makeSubtask(name, description);
        makeSubtask(name, description);

        HttpResponse<String> response = createDeleteRequest("subtask");
        assertEquals("Все подзадачи удалены.", response.body());
        assertTrue(taskManager.getListSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteListEpics() throws IOException, InterruptedException { // DELETE /tasks/epic
        makeSubtask(name, description);
        makeSubtask(name, description);
        makeSubtask(name, description);

        HttpResponse<String> response = createDeleteRequest("epic");
        assertEquals("Все эпики с подзадачами удалены.", response.body());
        assertTrue(taskManager.getListSubtasks().isEmpty());
        assertTrue(taskManager.getListEpics().isEmpty());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException { // DELETE /tasks/task/?id=
        makeTask(name, description);

        HttpResponse<String> response = createDeleteRequest("task/?id=1");
        assertEquals("Задача с ID = 1 удалена.", response.body());
        assertNull(taskManager.getTask(1));
    }

    @Test
    void shouldGetErrorWhenDeleteTaskByNotExistId() throws IOException, InterruptedException { // DELETE /tasks/task/?id=
        makeTask(name, description);

        HttpResponse<String> response = createDeleteRequest("task/?id=2");
        assertEquals("Задача с ID = 2 не существует.", response.body());
        assertNull(taskManager.getTask(2));
    }

    @Test
    void shouldGetErrorWhenDeleteTaskByIncorrectId() throws IOException, InterruptedException { // DELETE /tasks/task/?id=
        makeTask(name, description);

        HttpResponse<String> response = createDeleteRequest("task/?id=2asH");
        assertEquals("Введен некорректный ID", response.body());
        assertNotNull(taskManager.getTask(1));
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException { // DELETE /tasks/epic/?id=
        makeEpic(name, description);

        HttpResponse<String> response = createDeleteRequest("epic/?id=1");
        assertEquals("Эпик с ID = 1 удален.", response.body());
        assertNull(taskManager.getEpic(1));
    }

    @Test
    void shouldGetErrorWhenDeleteEpicByNotExistId() throws IOException, InterruptedException { // DELETE /tasks/epic/?id=
        makeEpic(name, description);

        HttpResponse<String> response = createDeleteRequest("epic/?id=2");
        assertEquals("Эпик с ID = 2 не существует.", response.body());
        assertNull(taskManager.getEpic(2));
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException { // DELETE /tasks/subtask/?id=
        makeSubtask(name, description);// epic id=1, subtask id=2

        HttpResponse<String> response = createDeleteRequest("subtask/?id=2");
        assertEquals("Подзадача с ID = 2 удалена.", response.body());
        assertNull(taskManager.getSubtask(2));
    }

    @Test
    void shouldGetErrorWhenDeleteSubtaskByNotExistId() throws IOException, InterruptedException { // DELETE /tasks/subtask/?id=
        makeSubtask(name, description);// epic id=1, subtask id=2

        HttpResponse<String> response = createDeleteRequest("subtask/?id=1");
        assertEquals("Подзадача с ID = 1 не существует.", response.body());
        assertNull(taskManager.getSubtask(1));
    }

    //shouldPOST

    @Test
    void shouldPostCreateTask() throws IOException, InterruptedException {// POST /tasks/task
        Task task = new Task(name, description);
        HttpResponse<String> response = createPostRequest("task", gson.toJson(task));

        assertEquals("Создана задача с ID = 1", response.body());
        assertNotNull(taskManager.getTask(1));
    }

    @Test
    void shouldPostUpdateTask() throws IOException, InterruptedException {// POST /tasks/task
        Task task = makeTask(name, description);
        task.setStatus(Status.IN_PROGRESS);

        HttpResponse<String> response = createPostRequest("task", gson.toJson(task));
        assertEquals("Обновлена задача с ID = 1", response.body());
        assertEquals(taskManager.getTask(1).getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void shouldPostCreateEpic() throws IOException, InterruptedException {// POST /tasks/epic
        Epic epic = new Epic(name, description);
        HttpResponse<String> response = createPostRequest("epic", gson.toJson(epic));

        assertEquals("Создан эпик с ID = 1", response.body());
        assertNotNull(taskManager.getEpic(1));
    }

    @Test
    void shouldPostUpdateEpic() throws IOException, InterruptedException {// POST /tasks/epic
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        HttpResponse<String> response = createPostRequest("epic", gson.toJson(epic));

        assertEquals("Обновлен эпик с ID = 1", response.body());
        assertEquals(taskManager.getEpic(1).getStatus(), Status.IN_PROGRESS);
    }

    @Test
    void shouldPostCreateSubtask() throws IOException, InterruptedException {// POST /tasks/subtask
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        HttpResponse<String> response = createPostRequest("subtask", gson.toJson(subtask));

        assertEquals("Создана подзадача с ID = 2", response.body());
        assertNotNull(taskManager.getSubtask(2));
    }

    @Test
    void shouldPostUpdateSubtask() throws IOException, InterruptedException {// POST /tasks/subtask
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        HttpResponse<String> response = createPostRequest("subtask", gson.toJson(subtask));

        assertEquals("Обновлена подзадача с ID = 2", response.body());
        assertEquals(taskManager.getSubtask(2).getStatus(), Status.DONE);
    }

    @Test
    void shouldDoNotPostNullTask() throws IOException, InterruptedException { // POST /tasks/ null
        HttpResponse<String> response = createPostRequest("task", gson.toJson(null));
        assertEquals("Некорректный запрос. Задача не может быть создана или обновлена", response.body());
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