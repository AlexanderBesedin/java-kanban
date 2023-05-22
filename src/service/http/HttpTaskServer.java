package service.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private final Gson gson;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.gson = Managers.getDefaultGson();
        this.manager = manager;
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handle);
        httpServer.start();
    }

    public void stop() {httpServer.stop(0);}

    private void handle(HttpExchange h) {
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
                    writeResponse(h, "Такого эндпоинта не существует", 404);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка обработки запроса");
        }
    }

    private void handleGetMethod(HttpExchange h) throws IOException {
        String[] partPath = h.getRequestURI().toString().split("/");
        String endPath = partPath[partPath.length-1];
        String response;
        Integer id;

        if (endPath.equals("tasks")) { // GET/tasks - получение списка всех задач по приоритету
            response = gson.toJson(manager.getPrioritizedTasks());
            writeResponse(h, response, 200);
        } else if (endPath.equals("history")) { // GET/tasks/history - получение истории просмотренных задач
            response = gson.toJson(manager.getHistory());
            writeResponse(h, response, 200);
        } else if (endPath.equals("task")) {  // GET/ tasks/task - Получение списка задач
            List<Task> tasks = manager.getListTasks();
            if (tasks.isEmpty()) {
                writeResponse(h, "Задачи отсутствуют.", 404);
            } else {
                response = gson.toJson(tasks);
                writeResponse(h, response, 200);
            }
        } else if (endPath.equals("epic")) { // GET/ tasks/epic - Получение списка эпиков
            List<Epic> epics = manager.getListEpics();
            if (epics.isEmpty()) {
                writeResponse(h, "Эпики отсутствуют.", 404);
            } else {
                response = gson.toJson(epics);
                writeResponse(h, response, 200);
            }
        } else if (endPath.equals("subtask")) {
            List<Subtask> subtasks = manager.getListSubtasks();
            if (subtasks.isEmpty()) {
                writeResponse(h, "Подзадачи отсутствуют.", 404);
            } else {
                response = gson.toJson(subtasks);
                writeResponse(h, response, 200);
            }
        } else if (endPath.contains("?id=") && partPath[2].equals("task")) {
            id = parseIDFromString(endPath);
            if (id == null) {
                writeResponse(h, "Введен некорректный ID", 405);
                return;
            }
            Task task = manager.getTask(id);
            if (task == null) {
                writeResponse(h, "Задача с ID = " + id + " не существует.", 404);
                return;
            }
            response = gson.toJson(task);
            writeResponse(h, response, 200);
        } else if (endPath.contains("?id=") && partPath[2].equals("epic")) {
            id = parseIDFromString(endPath);
            if (id == null) {
                writeResponse(h, "Введен некорректный ID", 405);
                return;
            }
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                writeResponse(h, "Эпик с ID = " + id + " не существует.", 404);
                return;
            }
            response = gson.toJson(epic);
            writeResponse(h, response, 200);
        } else if (partPath[3].contains("?id=") && partPath[2].equals("subtask")) {
            id = parseIDFromString(endPath);

            if (id == null) {writeResponse(h, "Введен некорректный ID", 405); return;}
            Subtask subtask = manager.getSubtask(id);
            if (subtask == null) {
                writeResponse(h, "Подзадача с ID = " + id + " не существует.", 404);
                return;
            }
            response = gson.toJson(subtask);
            writeResponse(h, response, 200);
        } else if (endPath.contains("?id=") && partPath[3].equals("epic")) {
            id = parseIDFromString(endPath);
            if (id == null) { writeResponse(h, "Введен некорректный ID", 405); return;}
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                writeResponse(h, "Эпик с ID = " + id + " не существует.", 404);
                return;
            }
            List<Subtask> subtasksByEpic = manager.getSubtasksOfEpic(id);
            if (subtasksByEpic.isEmpty()) {
                writeResponse(h, "У эпика с ID = " + id + " нет подзадач.", 404);
                return;
            }
            response = gson.toJson(subtasksByEpic);
            writeResponse(h, response, 200);
        }
    }

    private void handlePostMethod(HttpExchange h) throws IOException {
        Task task = taskFromJson(h);

        if (task == null) {
            writeResponse(h, "Некорректный запрос. Задача не может быть создана или обновлена",400);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
                writeResponse(h,"Создана подзадача с ID = " + subtask.getId(), 201);
            } else {
                manager.updateSubtask(subtask);
                writeResponse(h,"Обновлена подзадача с ID = " + subtask.getId(),202);
            }
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            if (epic.getId() == 0) {
                manager.createEpic(epic);
                writeResponse(h,"Создан эпик с ID = " + epic.getId(), 201);
            } else {
                manager.updateEpic(epic);
                writeResponse(h,"Обновлен эпик с ID = " + epic.getId(),202);
            }
        } else if (task.getClass() == Task.class) {
            if (task.getId() == 0) {
                manager.createTask(task);
                writeResponse(h,"Создана задача с ID = " + task.getId(), 201);
            } else {
                manager.updateTask(task);
                writeResponse(h,"Обновлена задача с ID = " + task.getId(),202);
            }
        }
    }

    private void handleDeleteMethod(HttpExchange h) throws IOException {
        String[] partPath = h.getRequestURI().toString().split("/");
        String endPath = partPath[partPath.length-1];
        Integer id;

        if (endPath.equals("task")) {  // DELETE/ tasks/task - удаление всех задач
            manager.clearTasks();
            if (manager.getListTasks().isEmpty()) {
                writeResponse(h, "Все задачи удалены.", 200);
            }
        } else if (endPath.equals("epic")) { // DELETE/ tasks/epic - удаление всех эпиков
            manager.clearEpics();
            if (manager.getListEpics().isEmpty() && manager.getListSubtasks().isEmpty()) {
                writeResponse(h, "Все эпики с подзадачами удалены.", 200);
            }
        } else if (endPath.equals("subtask")) {  // DELETE/ tasks/subtask - удаление всех подзадач
            manager.clearSubtasks();
            if (manager.getListSubtasks().isEmpty()) {
                writeResponse(h, "Все подзадачи удалены.", 200);
            }
        } else if (endPath.contains("?id=") && partPath[2].equals("task")) {// DELETE/ tasks/task/?id= - удаление задачи
            id = parseIDFromString(endPath);
            if (id == null) {
                writeResponse(h, "Введен некорректный ID", 405);
                return;
            }
            if (manager.removeTask(id)) {
                writeResponse(h, "Задача с ID = " + id + " удалена.", 200);
            } else {
                writeResponse(h, "Задача с ID = " + id + " не существует.", 404);
            }
        } else if (endPath.contains("?id=") && partPath[2].equals("epic")) {// DELETE/ tasks/epic/?id= - удаление эпика
            id = parseIDFromString(endPath);
            if (id == null) {
                writeResponse(h, "Введен некорректный ID", 405);
                return;
            }
            if (manager.removeEpic(id)) {
                writeResponse(h, "Эпик с ID = " + id + " удален.", 200);
            } else {
                writeResponse(h, "Эпик с ID = " + id + " не существует.", 404);
            }
        } else if (endPath.contains("?id=") && partPath[2].equals("subtask")) {// DELETE/ tasks/subtask/?id= - удаление подзадачи
            id = parseIDFromString(endPath);
            if (id == null) {
                writeResponse(h, "Введен некорректный ID", 405);
                return;
            }
            if (manager.removeSubtask(id)) {
                writeResponse(h, "Подзадача с ID = " + id + " удалена.", 200);
            } else {
                writeResponse(h, "Подзадача с ID = " + id + " не существует.", 404);
            }
        }
    }

    private Task taskFromJson(HttpExchange h) throws IOException {
        try {
            String[] partPath = h.getRequestURI().toString().split("/");
            String endPath = partPath[partPath.length-1];
            String body = new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.isJsonObject()) {
                String type = jsonObject.get("type").toString();
                type = type.replace("\"", "");
                if (endPath.equals("task") && type.equals("TASK")) {
                    return gson.fromJson(body, Task.class);
                } else if (endPath.equals("epic") && type.equals("EPIC")) {
                    return gson.fromJson(body, Epic.class);
                } else if (endPath.equals("subtask") && type.equals("SUBTASK")) {
                    return gson.fromJson(body, Subtask.class);
                } else {
                    throw new IllegalStateException(
                            "Ошибка: некорректный тип задачи ИЛИ url запроса не соответствует типу задачи.");
                }
            } else {
                throw new JsonSyntaxException("Ответ от сервера не соответствует ожидаемому.");
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

    private Integer parseIDFromString(String s) { // ?id={number}
        String parse = s.replace("?id=", "");
        try {
            if (parse.isBlank()) {
                throw new IllegalArgumentException("Не указан ID.");
            }
            int id = Integer.parseInt(parse);
            if (id < 0) {
                throw new IllegalArgumentException("ID не может быть отрицательным числом.");
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("Недопустимый формат ввода. ID должен быть целым положительным числом.");
            return null;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
