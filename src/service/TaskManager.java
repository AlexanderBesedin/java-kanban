package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {

    Task getTask(int id); // Получить задачу по идентификатору

    Epic getEpic(int id); // Получить эпик по идентификатору

    Subtask getSubtask(int id); // Получить подзадачу по идентификатору

    ArrayList<Subtask> getSubtasksOfEpic(int id);

    ArrayList<Task> getListTasks(); // Получить список всех задач

    ArrayList<Epic> getListEpics(); // Получить список всех эпиков

    ArrayList<Subtask> getListSubtasks(); // Получить список всех подзадач

    void createTask(Task task); // Метод создания задачи, эпика, подзадачи

    void updateTask(Task task); //Метод обновления задачи

    void updateEpic(Epic epic); //Метод обновления эпика

    void updateSubtask(Subtask subtask); //Метод обновления подзадачи

    void changeTaskStatus(int id, Status status);

    void changeSubtaskStatus(int id, Status status);

    void updateEpicStatus(Epic epic); // Метод обновления статуса эпика по статусам включенных подзадач

    void removeTask(int id); // Удалить задачу по идентификатору

    void removeEpic(int id); // Удалить эпик по идентификатору

    void removeSubtask(Integer id); // Удалить подзадачу по идентификатору

    void clearTasks(); // Удалить все задачи

    void clearEpics(); // Удалить все эпики с подзадачами

    void clearSubtasks(); //Удалить все подзадачи

}
