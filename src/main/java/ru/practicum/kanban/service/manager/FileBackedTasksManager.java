package ru.practicum.kanban.service.manager;

import ru.practicum.kanban.model.*;
import ru.practicum.kanban.exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public final static String TITLE = "id|type|name|status|description|start_time|duration|epic_or_subtasksID\n";
    private final File file;

    public FileBackedTasksManager() {
        this("src/main/resources/savedData.csv");
    }
    public FileBackedTasksManager(File file) {
        this.file = file;
    }
    public FileBackedTasksManager(String filePath) {
        this.file = new File(filePath);
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
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateTaskStatus(int id, Status status) {
        super.updateTaskStatus(id, status);
        save();
    }

    @Override
    public void updateSubtaskStatus(int id, Status status) {
        super.updateSubtaskStatus(id, status);
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
    public boolean removeTask(int id) {
        boolean result = super.removeTask(id);
        save();
        return result;
    }

    @Override
    public boolean removeEpic(int id) {
        boolean result = super.removeEpic(id);
        save();
        return result;
    }

    @Override
    public boolean removeSubtask(Integer id) {
        boolean result = super.removeSubtask(id);
        save();
        return result;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    protected void save() {
        try {
            Files.deleteIfExists(file.toPath()); // При очередном вызове метода save() удаляем файл для перезаписи
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка при создании файла", e);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            writer.write(TITLE);

            for (int i = 1; i <= id; i++) {
                Task task = tasks.getOrDefault(i, null);
                Epic epic = epics.getOrDefault(i, null);
                Subtask subtask = subtasks.getOrDefault(i, null);

                if (task != null) writer.write(toString(task));
                if (epic != null) writer.write(toString(epic));
                if (subtask != null) writer.write(toString(subtask));
            }

            writer.println();
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка при записи файла", e);
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager newFBTaskManager = new FileBackedTasksManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Проверка обращения к пустому файлу, файлу с заголовком
            if (Files.size(file.toPath()) <= TITLE.getBytes(StandardCharsets.UTF_8).length) return newFBTaskManager;

            Map<Integer, Task> allTasks = new HashMap<>();  // Создаем внутреннюю мапу для всех задач из файла
            String line = reader.readLine(); // Считываем строку-заголовок

            while (!line.isBlank()) { //line != null &&
                line = reader.readLine();
                Task task = fromString(line);
                if (task == null) continue;

                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    newFBTaskManager.subtasks.put(subtask.getId(), subtask);
                    allTasks.put(subtask.getId(), subtask);
                } else if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    newFBTaskManager.epics.put(epic.getId(), epic);
                    allTasks.put(epic.getId(), epic);
                } else {
                    newFBTaskManager.tasks.put(task.getId(), task);
                    allTasks.put(task.getId(), task);
                }
            }

            int maxId = 0;
            for (Integer id : allTasks.keySet()) { // Записываем последнее max значение счетчика id в IMTaskManager
                maxId = (maxId < id) ? id : maxId;
            }
            newFBTaskManager.id = maxId; // Восстановили последний максимальный id

            line = reader.readLine();
            List<Integer> history = historyFromString(line);
            if (!history.isEmpty()) {
                for (Integer id : history) {
                    newFBTaskManager.historyManager.add(allTasks.getOrDefault(id, null));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти или прочесть файл по указанному пути", e);
        }
        return newFBTaskManager;
    }

    private String toString(Task task)  {
        String typeTask = task.getClass().getSimpleName();
        // Поле для записи родительского id эпика у сабтаски или для id потомственных сабтасок эпика
        String epicOrSubtasksID = "";
        if (task instanceof Subtask) {
            epicOrSubtasksID = String.valueOf(((Subtask) task).getEpicId());
        } else if (task instanceof Epic) {
            StringBuilder ids = new StringBuilder();
            List<Integer> subtasksInEpic = ((Epic) task).getSubtasksIds();
            if (subtasksInEpic != null && !subtasksInEpic.isEmpty()) {
                for (Integer id : subtasksInEpic) {
                    ids.append(String.format("%d|", id));
                }
                ids.deleteCharAt(ids.lastIndexOf("|")); // удаляем последний разделитель
                epicOrSubtasksID = ids.toString();
            }
        }
        //      id,type,name,status,description,start_time,duration,epic_or_subtasksID
        return String.format("%d|%S|%s|%s|%s|%s|%s|%s\n",
                                task.getId(),
                                typeTask,
                                task.getName(),
                                task.getStatus(),
                                task.getDescription(),
                                task.getStartTime(),
                                task.getDuration(),
                                epicOrSubtasksID);
    }

    private static Task fromString(String value) {
        // id - field[0],
        // type - field[1],
        // name - field[2],
        // status - field[3],
        // description - field[4],
        // start_time - field[5],
        // duration - field[6],
        // epic_or_subtasksID - field[7] ... field[field.length-1]

        if (!value.isBlank()) {
            String[] field = value.split("\\|");
            LocalDateTime startTime = field[5].equals("null")  ? null : LocalDateTime.parse(field[5]);
            Duration duration = field[6].equals("null") ? null : Duration.parse(field[6]);
            TaskType taskType = TaskType.valueOf(field[1]);

            switch (taskType) {
                case TASK:
                    Task task = new Task(field[2], field[4]); // Создали задачу с полями в конструкторе
                    task.setId(Integer.parseInt(field[0])); // записали id
                    task.setStatus(Status.valueOf(field[3])); // записали статус
                    task.setStartTime(startTime);
                    task.setDuration(duration);
                    return task;
                case EPIC:
                    Epic epic = new Epic(field[2], field[4]);
                    epic.setId(Integer.parseInt(field[0]));
                    epic.setStatus(Status.valueOf(field[3]));
                    epic.setStartTime(startTime);
                    epic.setDuration(duration);
                    if (field.length > 7) { // Проверяем наличие подзадач у эпика
                        for (int i = 7; i < field.length; i++) {
                            epic.setSubtaskInEpic(Integer.parseInt(field[i]));
                        }
                    }
                    return epic;
                case SUBTASK:
                    Subtask subtask = new Subtask(field[2], field[4], Integer.parseInt(field[7]));
                    subtask.setId(Integer.parseInt(field[0]));
                    subtask.setStatus(Status.valueOf(field[3]));
                    subtask.setStartTime(startTime);
                    subtask.setDuration(duration);
                    return subtask;
            }
        }
        return null;
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
        if (value == null) return history;
        if (!value.isBlank()) {
            String[] views = value.split("\\|");
            for (String view : views) {
                history.add(Integer.valueOf(view));
            }
        }
        return history;
    }
}