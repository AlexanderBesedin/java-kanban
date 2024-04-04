package ru.practicum.kanban.service.manager;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    Task getTask(int id); // Получить задачу по идентификатору

    Epic getEpic(int id); // Получить эпик по идентификатору

    Subtask getSubtask(int id); // Получить подзадачу по идентификатору

    List<Subtask> getSubtasksOfEpic(int id);

    List<Task> getTasks(); // Получить список всех задач

    List<Epic> getEpics(); // Получить список всех эпиков

    List<Subtask> getSubtasks(); // Получить список всех подзадач

    List<Task> getHistory();  // добавил метод получения истории просмотров задач

    Set<Task> getPrioritizedTasks();

    void createTask(Task task); // Метод создания задачи, эпика, подзадачи

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task); //Метод обновления задачи, подзадачи, эпика

    void updateEpic(Epic epic) throws NullPointerException, IllegalArgumentException;

    void updateSubtask(Subtask subtask) throws NullPointerException, IllegalArgumentException;

    void updateTaskStatus(int id, Status status);

    void updateSubtaskStatus(int id, Status status);

    boolean removeTask(int id); // Удалить задачу по идентификатору

    boolean removeEpic(int id); // Удалить эпик по идентификатору

    boolean removeSubtask(Integer id); // Удалить подзадачу по идентификатору

    void deleteTasks(); // Удалить все задачи

    void deleteEpics(); // Удалить все эпики с подзадачами

    void deleteSubtasks(); //Удалить все подзадачи
}
