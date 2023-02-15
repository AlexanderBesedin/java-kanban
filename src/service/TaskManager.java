package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    Task getTask(int id); // Получить задачу по идентификатору

    Epic getEpic(int id); // Получить эпик по идентификатору

    Subtask getSubtask(int id); // Получить подзадачу по идентификатору

    List<Subtask> getSubtasksOfEpic(int id);

    List<Task> getListTasks(); // Получить список всех задач

    List<Epic> getListEpics(); // Получить список всех эпиков

    List<Subtask> getListSubtasks(); // Получить список всех подзадач

    void createTask(Task task); // Метод создания задачи, эпика, подзадачи

    void updateTask(Task task); //Метод обновления задачи, подзадачи, эпика

    void changeTaskStatus(int id, Status status);

    void changeSubtaskStatus(int id, Status status);


    void removeTask(int id); // Удалить задачу по идентификатору

    void removeEpic(int id); // Удалить эпик по идентификатору

    void removeSubtask(Integer id); // Удалить подзадачу по идентификатору

    void clearTasks(); // Удалить все задачи

    void clearEpics(); // Удалить все эпики с подзадачами

    void clearSubtasks(); //Удалить все подзадачи

    public void printTask(int id); // Метод вывода задачи любого типа по существующему id

    public void printSubtasksOfEpic(int id); //Метод печати позадач выбранного эпика

    public void printListTasks(); // Метод печати списка задач

    public void printListEpics(); // Метод печати списка эпиков

    public void printListSubtasks(); // Метод печати списка подзадач

}
