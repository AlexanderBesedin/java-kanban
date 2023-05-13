package service;

import exception.*;
import model.Epic;
import model.Subtask;
import model.Task;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public final static String TITLE = "id|type|name|status|description|start_time|duration|epic_or_subtasksID\n";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void changeTaskStatus(int id, Status status) {
        super.changeTaskStatus(id, status);
        save();
    }

    @Override
    public void changeSubtaskStatus(int id, Status status) {
        super.changeSubtaskStatus(id, status);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }
    @Override
    public void updateEpicDuration(Epic epic) {
        super.updateEpicDuration(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    private void save() {
        try {
            Files.deleteIfExists(file.toPath()); // При очередном вызове метода save() удаляем файл для перезаписи
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка при создании файла", e);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            writer.write(TITLE);

            for (int i = 1; i <= getLastId(); i++) {
                Task task = tasks.getOrDefault(i, null);
                Epic epic = epics.getOrDefault(i, null);
                Subtask subtask = subtasks.getOrDefault(i, null);
                if (task != null) {
                    writer.write(toString(task));
                } else if (epic != null) {
                    writer.write(toString(epic));
                } else if (subtask != null) {
                    writer.write(toString(subtask));
                }
                if (task == null && epic == null && subtask == null) return;
            }

            writer.println();
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка при записи файла", e);
        }
    }

    private String toString(Task task)  {
        String typeTask = task.getClass().getSimpleName();
        // Поле для записи родительского id эпика у сабтаски или для id потомственных сабтасок эпика
        String epicOrSubtasksID = "";
        if (task instanceof Subtask) {
            epicOrSubtasksID = String.valueOf(((Subtask) task).getEpicId());
        } else if (task instanceof Epic) {
            StringBuilder ids = new StringBuilder();
            List<Integer> subtasksInEpic = ((Epic) task).getSubtasksInEpic();
            if (!subtasksInEpic.isEmpty()) {
                for (Integer id : subtasksInEpic) {
                    ids.append(String.format("%d|", id));
                }
                ids.deleteCharAt(ids.lastIndexOf("|")); // удаляем последний разделитель
                epicOrSubtasksID = ids.toString();
            }
        }
        //      id,type,name,status,description,start_time,duration,epic_or_subtasksID
        return String.format("%d|%S|%s|%s|%s|%s|%s|%s\n", task.getId(), typeTask, task.getName(), task.getStatus(),
                task.getDescription(), task.getStartTime(), task.getDuration(), epicOrSubtasksID);
    }

    private static Task fromString(String value) {
        // id - field[0],
        // type - field[1],
        // name - field[2],
        // status - field[3],
        // description - field[4],
        // start_time - field[5],
        // duration - field[6],
        // epic_or_subtasksID - field[7]

        Task result = null;
        if (!value.isBlank()) {
            String[] field = value.split("\\|");
            TaskType taskType = TaskType.valueOf(field[1]);
            switch (taskType) {
                case TASK:
                    Task task = new Task(field[2], field[4]); // Создали задачу с полями в конструкторе
                    task.setId(Integer.parseInt(field[0])); // записали id
                    task.setStatus(Status.valueOf(field[3])); // записали статус
                    LocalDateTime startTime = field[5].equals("null")  ? null : LocalDateTime.parse(field[5]);
                    task.setStartTime(startTime);
                    Duration duration = field[6].equals("null") ? null : Duration.parse(field[6]);
                    task.setDuration(duration);
                    result = task;
                    break;
                case EPIC:
                    Epic epic = new Epic(field[2], field[4]);
                    epic.setId(Integer.parseInt(field[0]));
                    epic.setStatus(Status.valueOf(field[3]));
                    startTime = field[5].equals("null") ? null : LocalDateTime.parse(field[5]);
                    epic.setStartTime(startTime);
                    duration = field[6].equals("null") ? null : Duration.parse(field[6]);
                    epic.setDuration(duration);
                    if (field.length > 7) { // Проверяем наличие подзадач у эпика
                        for (int i = 7; i < field.length; i++) {
                            epic.setSubtaskInEpic(Integer.parseInt(field[i]));
                        }
                    }
                    result = epic;
                    break;
                case SUBTASK:
                    Subtask subtask = new Subtask(field[2], field[4], Integer.parseInt(field[7]));
                    subtask.setId(Integer.parseInt(field[0]));
                    subtask.setStatus(Status.valueOf(field[3]));
                    startTime = field[5].equals("null") ? null : LocalDateTime.parse(field[5]);
                    subtask.setStartTime(startTime);
                    duration = field[6].equals("null") ? null : Duration.parse(field[6]);
                    subtask.setDuration(duration);
                    result = subtask;
                    break;
            }
        }
        return result;
    }

    private static String historyToString(HistoryManager manager) {
        List <Task> history = manager.getHistory();
        StringBuilder result = new StringBuilder();

        if (!history.isEmpty()) {
            for (Task task : history) {
                result.append(String.format("%s|", task.getId()));
            }
            result.deleteCharAt(result.lastIndexOf("|")); // удаляем последний разделитель
        }
        return result.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isBlank()) {
            String[] views = value.split("\\|");
            for (String view : views) {
                history.add(Integer.valueOf(view));
            }
        }
        return history;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager newFBTaskManager = new FileBackedTasksManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Проверка обращения к пустому файлу, файлу с заголовком
            if (Files.size(file.toPath()) <= TITLE.getBytes(StandardCharsets.UTF_8).length) return newFBTaskManager;

            Map<Integer, Task> allTasks = new HashMap<>();  // Создаем внутреннюю мапу для всех задач из файла
            String line = reader.readLine(); // Считываем строку-заголовок

            while (!line.isBlank()) {
                line = reader.readLine();
                Task task = fromString(line);
                if (task == null) continue;

                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    subtasks.put(subtask.getId(), subtask);
                    allTasks.put(subtask.getId(), subtask);
                } else if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    epics.put(epic.getId(), epic);
                    allTasks.put(epic.getId(), epic);
                } else {
                    tasks.put(task.getId(), task);
                    allTasks.put(task.getId(), task);
                }
            }

            int maxId = 0;
            for (Integer id : allTasks.keySet()) { // Записываем последнее max значение счетчика id в IMTaskManager
                maxId = (maxId < id) ? id : maxId;
            }
            setLastId(maxId); // Восстановили последний максимальный id

            line = reader.readLine();
            List<Integer> history = historyFromString(line);
            if (!history.isEmpty()) {
                for (Integer id : history) {
                    historyManager.add(allTasks.getOrDefault(id, null));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти или прочесть файл по указанному пути", e);
        }
        return newFBTaskManager;
    }

}