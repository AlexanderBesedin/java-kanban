package service;

import model.*;

import java.util.LinkedHashMap;

public class TaskManager { // Класс хранения задач всех типов
    private static LinkedHashMap<Integer, Task> tasks = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Epic> epics = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Subtask> subtasks = new LinkedHashMap<>();


    public static void changeTaskStatus(int id, int num) {
        String status = Status.getStatus(num);
        tasks.get(id).setStatus(status);
    }

    public static void changeSubtaskStatus(int id, int num) {
        String status = Status.getStatus(num);
        subtasks.get(id).setStatus(status);
    }

    public static LinkedHashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static LinkedHashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public static LinkedHashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}