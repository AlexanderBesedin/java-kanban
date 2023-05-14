package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    Task getTask(int id); // Получить задачу по идентификатору

    Epic getEpic(int id); // Получить эпик по идентификатору

    Subtask getSubtask(int id); // Получить подзадачу по идентификатору

    List<Subtask> getSubtasksOfEpic(int id);

    List<Task> getListTasks(); // Получить список всех задач

    List<Epic> getListEpics(); // Получить список всех эпиков

    List<Subtask> getListSubtasks(); // Получить список всех подзадач

    List<Task> getHistory();  // добавил метод получения истории просмотров задач

    Set<Task> getPrioritizedTasks();

    void createTask(Task task); // Метод создания задачи, эпика, подзадачи

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task); //Метод обновления задачи, подзадачи, эпика

    void updateEpic(Epic epic) throws NullPointerException, IllegalArgumentException;

    void updateSubtask(Subtask subtask) throws NullPointerException, IllegalArgumentException;

    void changeTaskStatus(int id, Status status);

    void changeSubtaskStatus(int id, Status status);

    void removeTask(int id); // Удалить задачу по идентификатору

    void removeEpic(int id); // Удалить эпик по идентификатору

    void removeSubtask(Integer id); // Удалить подзадачу по идентификатору

    void clearTasks(); // Удалить все задачи

    void clearEpics(); // Удалить все эпики с подзадачами

    void clearSubtasks(); //Удалить все подзадачи

    void printTask(int id); // Метод вывода задачи любого типа по существующему id

    void printSubtasksOfEpic(int id); //Метод печати позадач выбранного эпика

    void printListTasks(); // Метод печати списка задач

    void printListEpics(); // Метод печати списка эпиков

    void printListSubtasks(); // Метод печати списка подзадач

    void printHistory(); // добавил метод печати истории

}
