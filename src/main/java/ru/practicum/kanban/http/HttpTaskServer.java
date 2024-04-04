package ru.practicum.kanban.http;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.kanban.exception.ManagerSaveException;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.manager.HttpTaskManager;
import ru.practicum.kanban.service.manager.TaskManager;
import ru.practicum.kanban.service.utils.Managers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Реализует основной API данного сервиса по основным эндпоинтам:
//   /tasks - только GET-запросы получения всех существующих задач, сортированных по приоритету (время начала выполнения)
//   /tasks/<key>?id= - GET/DELETE-запросы по id (целое число), где key - тип задачи task/epic/subtask
//   /tasks/<key> - GET/DELETE-запросы, POST-запросы с данными в теле запроса,где key - тип задачи task/epic/subtask
//   /tasks/subtask/epic?id= - GET-запрос на получение всех подзадач subtask по id эпика
//   /tasks/history - GET-запрос на получение истории вызова/просмотра задач
// Сервер работает на указанном порту и обрабатывает запросы клиента по эндпоинтам API сервиса,
// делегируя обработку данных соответствующим методам HttpTaskManager. Возможна работа с FileBackedTasksManager.
public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handler);
    }

    public HttpTaskServer() throws IOException {
        this(new HttpTaskManager());
    }

//    public static void main(String[] args) throws IOException { // для первичного тестирования API
//        String child = "dev" + "/java-kanban/resources/save.csv";
//        File file = new File(System.getProperty("user.home"), child);
////        final HttpTaskServer taskServer = new HttpTaskServer(FileBackedTasksManager.loadFromFile(file));
//        final HttpTaskServer taskServer = new HttpTaskServer();
//        taskServer.start();
//    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private void handler(HttpExchange h) {
        try {
            String method = h.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetMethod(h);
                    break;
                case "POST":
                    handlePostMethod(h);
                    break;
                case "DELETE":
                    handleDeleteMethod(h);
                    break;
                default:
                    writeResponse(h, "Such an endpoint doesn't exist", 404);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Request processing error");
        }
    }

    private void handleGetMethod(HttpExchange h) throws IOException {
        String[] partsPath = h.getRequestURI().toString().split("/");
        String endPath = partsPath[partsPath.length - 1];
        Integer id;

        if (endPath.equals("tasks")) { //GET /tasks - получение списка всех задач по приоритету
            writeResponse(h, gson.toJson(manager.getPrioritizedTasks()), 200);
        }

        if (endPath.equals("history")) { //GET /tasks/history - получение истории просмотренных задач
            writeResponse(h, gson.toJson(manager.getHistory()), 200);
        }

        if (endPath.equals("task")) {  //GET /tasks/task - Получение списка задач
            List<Task> tasks = manager.getTasks();
            if (tasks.isEmpty()) {
                writeResponse(h, "No tasks available", 404); //Задачи отсутствуют
            } else writeResponse(h, gson.toJson(tasks), 200);
        }

        if (endPath.equals("epic")) { //GET /tasks/epic - Получение списка эпиков
            List<Epic> epics = manager.getEpics();
            if (epics.isEmpty()) {
                writeResponse(h, "No epics available", 404);
            } else writeResponse(h, gson.toJson(epics), 200);
        }

        if (endPath.equals("subtask")) {
            List<Subtask> subtasks = manager.getSubtasks();
            if (subtasks.isEmpty()) {
                writeResponse(h, "No subtasks available", 404);
            } else writeResponse(h, gson.toJson(subtasks), 200);
        }

        if (endPath.startsWith("task?id=")) { //GET /tasks/task?id=
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }

            Task task = manager.getTask(id);
            if (task == null) {
                writeResponse(h, "Task with ID = " + id + " doesn't exist", 404);
            } else writeResponse(h, gson.toJson(task), 200);
        }

        if (endPath.startsWith("epic?id=") && partsPath.length == 3) { //GET /tasks/epic?id=
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }

            Epic epic = manager.getEpic(id);
            if (epic == null) {
                writeResponse(h, "Epic with ID = " + id + " doesn't exist", 404);
            } else writeResponse(h, gson.toJson(epic), 200);
        }

        if (endPath.startsWith("subtask?id=")) { //GET /tasks/subtask?id=
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }

            Subtask subtask = manager.getSubtask(id);
            if (subtask == null) {
                writeResponse(h, "Subtask with ID = " + id + " doesn't exist", 404);
            } else writeResponse(h, gson.toJson(subtask), 200);
        }

        if (endPath.contains("epic?id=") && partsPath[2].equals("subtask")) { //GET /tasks/subtask/epic?id=
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }

            if (manager.getEpic(id) == null) {
                writeResponse(h, "Epic with ID = " + id + " doesn't exist", 404);
                return;
            }

            List<Subtask> subtasksByEpic = manager.getSubtasksOfEpic(id);
            if (subtasksByEpic.isEmpty()) {
                writeResponse(h, "An epic with ID = " + id + " doesn't have subtasks", 404);
            } else writeResponse(h, gson.toJson(subtasksByEpic), 200);
        }
    }

    private void handlePostMethod(HttpExchange h) throws IOException {
        Task task = taskFromJson(h);

        if (task == null) {
            writeResponse(h, "Invalid request! The task could not be created or updated", 400);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
                System.out.println("Created subtask with ID = " + subtask.getId());
                writeResponse(h, gson.toJson(manager.getSubtask(subtask.getId())), 201);
            } else {
                manager.updateSubtask(subtask);
                System.out.println("Updated subtask with ID = " + subtask.getId());
                writeResponse(h, gson.toJson(manager.getSubtask(subtask.getId())), 200);
            }
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            if (epic.getId() == 0) {
                manager.createEpic(epic);
                System.out.println("Created epic with ID = " + epic.getId());
                writeResponse(h, gson.toJson(manager.getEpic(epic.getId())), 201);
            } else {
                manager.updateEpic(epic);
                System.out.println("Updated epic with ID = " + epic.getId());
                writeResponse(h, gson.toJson(manager.getEpic(epic.getId())), 200);
            }
        } else if (task.getClass() == Task.class) {
            if (task.getId() == 0) {
                manager.createTask(task);
                System.out.println("Created task with ID = " + task.getId());
                writeResponse(h, gson.toJson(manager.getTask(task.getId())), 201);
            } else {
                manager.updateTask(task);
                System.out.println("Updated task with ID = " + task.getId());
                writeResponse(h, gson.toJson(manager.getTask(task.getId())), 200);
            }
        }
    }

    private void handleDeleteMethod(HttpExchange h) throws IOException {
        String[] partPath = h.getRequestURI().toString().split("/");
        String endPath = partPath[partPath.length - 1];
        Integer id;

        if (endPath.equals("task")) {  // DELETE/ tasks/task - удаление всех задач
            manager.deleteTasks();
            if (manager.getTasks().isEmpty()) {
                writeResponse(h, "All tasks deleted", 200);
            }
        } else if (endPath.equals("epic")) { // DELETE/ tasks/epic - удаление всех эпиков
            manager.deleteEpics();
            if (manager.getEpics().isEmpty() && manager.getSubtasks().isEmpty()) {
                writeResponse(h, "All epics with subtasks have been deleted", 200);
            }
        } else if (endPath.equals("subtask")) {  // DELETE/ tasks/subtask - удаление всех подзадач
            manager.deleteSubtasks();
            if (manager.getSubtasks().isEmpty()) {
                writeResponse(h, "All subtasks deleted", 200);
            }
        } else if (endPath.startsWith("task?id=")) {// DELETE/ tasks/task/?id= - удаление задачи
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }
            if (manager.removeTask(id)) {
                writeResponse(h, "Task with ID = " + id + " has been deleted", 200);
            } else {
                writeResponse(h, "Task with ID = " + id + " doesn't exist", 404);
            }
        } else if (endPath.startsWith("epic?id=")) {// DELETE/ tasks/epic/?id= - удаление эпика
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }
            if (manager.removeEpic(id)) {
                writeResponse(h, "Epic with ID = " + id + " has been deleted", 200);
            } else {
                writeResponse(h, "Epic with ID = " + id + " doesn't exist", 404);
            }
        } else if (endPath.startsWith("subtask?id=")) {// DELETE/ tasks/subtask/?id= - удаление подзадачи
            id = parseId(h);
            if (id == null) {
                writeResponse(h, "Incorrect ID", 405);
                return;
            }
            if (manager.removeSubtask(id)) {
                writeResponse(h, "Subtask with ID = " + id + " has been deleted", 200);
            } else {
                writeResponse(h, "Subtask with ID = " + id + " doesn't exist", 404);
            }
        }
    }

    private Task taskFromJson(HttpExchange h) throws IOException {
        try {
            String[] partsPath = h.getRequestURI().toString().split("/");
            String endPath = partsPath[partsPath.length - 1];
            String body = new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

            if (JsonParser.parseString(body).isJsonObject()) {
                switch (endPath) {
                    case "task":
                        return gson.fromJson(body, Task.class);
                    case "epic":
                        return gson.fromJson(body, Epic.class);
                    case "subtask":
                        return gson.fromJson(body, Subtask.class);
                    default:
                        throw new IllegalStateException(
                                "Error! The request URL should contain one of the task types: task, epic, subtask");
                }
            } else {
                throw new JsonSyntaxException("Invalid request body format");
            }
        } catch (JsonSyntaxException | IllegalStateException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void writeResponse(HttpExchange h, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            h.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            h.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = h.getResponseBody()) {
                os.write(bytes);
            }
        }
        h.close();
    }

    private Integer parseId(HttpExchange h) {
        String id = h.getRequestURI().getQuery().substring("id=".length());
        return !id.isBlank() && Integer.parseInt(id) > 0 ? Integer.valueOf(id) : null;
    }
}
