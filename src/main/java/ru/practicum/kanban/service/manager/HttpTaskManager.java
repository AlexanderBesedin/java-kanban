package ru.practicum.kanban.service.manager;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import ru.practicum.kanban.http.KVTaskClient;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.utils.Managers;

import java.util.*;
import java.util.stream.Collectors;

//  Выполняет сериализацию/десериализацию данных между конечным клиентом и сервером-хранилищем KVServer.
//  Конечный клиент (прим. браузер) отправляет запросы на HttpTaskServer, который реализует основной API данного сервиса.
//  HttpTaskManager обрабатывает запросы по эндпоинтам HttpTaskServer и переадресует их соответствующим методам KVTaskClient.
//  Наследует от FileBackedTaskManager реализацию базовых методов интерфейса TaskManager.
//  Принимает через конструктор URL-адрес хранилища KVServer.
public class HttpTaskManager extends FileBackedTasksManager {
    private final static String KEY_TASKS = "tasks";
    private final static String KEY_SUBTASKS = "subtasks";
    private final static String KEY_EPICS = "epics";
    private final static String KEY_HISTORY = "history";
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager() {
        this("http://localhost:8078/");
    }

    public HttpTaskManager(String url) {
        super(url);
        this.kvTaskClient = new KVTaskClient(url);
        this.gson = Managers.getGson();
    }

    //  Выполняет сериализацию и сохранение актуальных данных менеджера задач
    @Override
    protected void save() { //метод сохранения состояния менеджера
        try {
            if (tasks.isEmpty() && epics.isEmpty() && subtasks.isEmpty()) return;
            if (!tasks.isEmpty()) kvTaskClient.put(KEY_TASKS, gson.toJson(getTasks()));
            if (!epics.isEmpty()) kvTaskClient.put(KEY_EPICS, gson.toJson(getEpics()));
            if (!subtasks.isEmpty()) kvTaskClient.put(KEY_SUBTASKS, gson.toJson(getSubtasks()));
            kvTaskClient.put(KEY_HISTORY, gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList())));
        } catch (JsonIOException e) {
            System.out.println("Возникла ошибка конвертации В json.");
        }
    }

    //  Выполняет загрузку актуальных данных менеджера задач с KVServer и их десериализацию
    public static HttpTaskManager loadFromServer(String serverURI) {
        HttpTaskManager httpTaskManager = new HttpTaskManager(serverURI);
        Map<Integer, Task> allTasks = new HashMap<>();

        try {
            List<Task> listTasks = httpTaskManager.gson.fromJson(httpTaskManager.kvTaskClient.load(KEY_TASKS),
                    new TypeToken<List<Task>>() {
                    }.getType());
            List<Epic> listEpics = httpTaskManager.gson.fromJson(httpTaskManager.kvTaskClient.load(KEY_EPICS),
                    new TypeToken<List<Epic>>() {
                    }.getType());
            List<Subtask> listSubtasks = httpTaskManager.gson.fromJson(httpTaskManager.kvTaskClient.load(KEY_SUBTASKS),
                    new TypeToken<List<Subtask>>() {
                    }.getType());

            List<Integer> history = httpTaskManager.gson.fromJson(httpTaskManager.kvTaskClient.load(KEY_HISTORY),
                    new TypeToken<List<Integer>>() {
                    }.getType());

            Optional.ofNullable(listTasks)
                    .ifPresent(list -> list.forEach(task -> {
                        httpTaskManager.tasks.put(task.getId(), task);
                        allTasks.put(task.getId(), task);
                    }));

            Optional.ofNullable(listEpics)
                    .ifPresent(list -> list.forEach(epic -> {
                        httpTaskManager.epics.put(epic.getId(), epic);
                        allTasks.put(epic.getId(), epic);
                    }));

            Optional.ofNullable(listSubtasks)
                    .ifPresent(list -> list.forEach(subtask -> {
                        httpTaskManager.subtasks.put(subtask.getId(), subtask);
                        allTasks.put(subtask.getId(), subtask);
                    }));

            allTasks.keySet().stream()
                    .peek(taskId -> { // Восстановили историю просмотров задач
                                if (history != null && !history.isEmpty()) {
                                    history.forEach(id -> httpTaskManager.historyManager.add(allTasks.getOrDefault(id, null)));
                                }
                            }
                    )
                    .max(Comparator.naturalOrder()) // Восстановили максимальный id с последней сессии
                    .ifPresent(taskId -> httpTaskManager.id = taskId);
        } catch (JsonIOException e) {
            System.out.println("Возникла ошибка конвертации ИЗ json.");
        }
        return httpTaskManager;
    }
}
