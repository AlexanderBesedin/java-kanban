package service;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import service.http.KVTaskClient;

import java.net.URI;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager{
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager() {
        this("http://localhost:8078/");
    }

    public HttpTaskManager(String uri) {
        super(uri);
        URI url = URI.create(uri);
        this.kvTaskClient = new KVTaskClient(url);
        this.gson = Managers.getDefaultGson();
    }


    @Override
    protected void save() { //метод сохранения состояния менеджера
        try {
            if (tasks.isEmpty() && epics.isEmpty() && subtasks.isEmpty()) return;
            if (!tasks.isEmpty()) kvTaskClient.put("task", gson.toJson(getListTasks()));
            if (!epics.isEmpty()) kvTaskClient.put("epic", gson.toJson(getListEpics()));
            if (!subtasks.isEmpty()) kvTaskClient.put("subtask", gson.toJson(getListSubtasks()));

            kvTaskClient.put("history", gson.toJson(getHistory()));
        } catch (JsonIOException e) {
            System.out.println("Возникла ошибка конвертации В json.");
        }
    }

    public static HttpTaskManager loadFromServer(String serverURI) {

        HttpTaskManager httpTaskManager = new HttpTaskManager(serverURI);
        TypeToken<List<Task>> taskTypeToken = new TypeToken<>() {};
        TypeToken<List<Epic>> epicTypeToken = new TypeToken<>() {};
        TypeToken<List<Subtask>>  subtaskTypeToken = new TypeToken<>() {};
        TypeToken<List<Subtask>>  historyTypeToken = new TypeToken<>() {};

        Map<Integer, Task> allTasks = new HashMap<>(); //Мапа для всех задач

        try {
            List<Task> listTasks = httpTaskManager.gson.fromJson(
                    httpTaskManager.kvTaskClient.load("task"), taskTypeToken.getType()
            );
            List<Epic> listEpics = httpTaskManager.gson.fromJson(
                    httpTaskManager.kvTaskClient.load("epic"), epicTypeToken.getType()
            );
            List<Subtask> listSubtasks = httpTaskManager.gson.fromJson(
                    httpTaskManager.kvTaskClient.load("subtask"), subtaskTypeToken.getType()
            );

            if (!listTasks.isEmpty()) {
                for (Task task : listTasks) {
                    httpTaskManager.tasks.put(task.getId(), task);
                    allTasks.put(task.getId(), task);
                }
            }

            if (!listEpics.isEmpty()) {
                for (Epic epic : listEpics) {
                    httpTaskManager.epics.put(epic.getId(), epic);
                    allTasks.put(epic.getId(), epic);

                }
            }

            if (!listSubtasks.isEmpty()) {
                for (Subtask subtask: listSubtasks) {
                    httpTaskManager.subtasks.put(subtask.getId(), subtask);
                    allTasks.put(subtask.getId(), subtask);
                }
            }

            int maxId = 0;
            for (Integer id : allTasks.keySet()) { maxId = (maxId < id) ? id : maxId;}
            httpTaskManager.id = maxId; // Восстановили последний максимальный id

            List<Integer> history = httpTaskManager.gson.fromJson(
                    httpTaskManager.kvTaskClient.load("history"), historyTypeToken.getType()
            );

            if (!history.isEmpty()) {
                for (Integer id : history) {
                    httpTaskManager.historyManager.add(allTasks.getOrDefault(id, null));
                }
            }
        } catch (JsonIOException e) {
            System.out.println("Возникла ошибка конвертации ИЗ json.");
        }
        return httpTaskManager;
    }
}
