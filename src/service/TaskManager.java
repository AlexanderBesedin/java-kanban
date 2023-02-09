package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.LinkedHashMap;

public class TaskManager { // Класс хранения задач всех типов
    private String[] status;
    private LinkedHashMap<Integer, Task> tasks;
    private LinkedHashMap<Integer, Epic> epics;
    private LinkedHashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        status = new String[]{"NEW", "IN_PROGRESS", "DONE"};
        tasks = new LinkedHashMap<>();
        epics = new LinkedHashMap<>();
        subtasks = new LinkedHashMap<>();
    }

    public void changeTaskStatus(int id, int num) {
        String status = this.status[num];
        tasks.get(id).setStatus(status);
    }

    public void changeSubtaskStatus(int id, int num) {
        String status = this.status[num];
        subtasks.get(id).setStatus(status);

    }

    public String[] getStatus() {
        return status;
    }

    public LinkedHashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public LinkedHashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public LinkedHashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}