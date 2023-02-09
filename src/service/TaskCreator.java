package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TaskCreator {
    private static int id;
    private String[] status;
    private LinkedHashMap<Integer, Task> tasks;
    private LinkedHashMap<Integer, Epic> epics;
    private LinkedHashMap<Integer, Subtask> subtasks;

    public TaskCreator(String[] status,
                       LinkedHashMap<Integer, Task> tasks,
                       LinkedHashMap<Integer, Epic> epics,
                       LinkedHashMap<Integer, Subtask> subtasks) {
        this.status = status;
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
    }

    public void createTask(Object o) { // Метод создания задачи, эпика, подзадачи
        id++;
        if (o instanceof Subtask) { // Проверка объекта-аргумента на принаджежность классу Subtask
            Subtask subtask = (Subtask) o; // Приводим объект-аргумент к типу Subtask
            subtask.setId(id); // Присваиваем id
            epics.get(subtask.getEpicId()).setSubtaskInEpic(id); // Записываем в список эпика id подзадачи
            subtask.setStatus(status[0]); //присваиваем статус NEW
            subtasks.put(id, subtask); // Сохраняем в соответствующую мапу
            System.out.println("Создана подзадача: \n" + subtask + '\n');
        } else if (o instanceof Epic) { // Проверка объекта-аргумента на принаджежность классу Epic
            Epic epic = (Epic) o;
            epic.setId(id);
            epic.setStatus(status[0]);
            epics.put(id, epic);
            System.out.println("Создан эпик: \n" + epic + '\n');
        } else if (o instanceof Task) { // Проверка объекта-аргумента на принаджежность классу Task
            Task task = (Task) o;
            task.setId(id);
            task.setStatus(status[0]);
            tasks.put(id, task);
            System.out.println("Создана задача: \n" + task + '\n');
        } else {
            System.out.println("Создайте задачу типа Task, Epic или Subtask.\n");
        }
    }

    public void updateTask(Task task) { //Метод обновления задачи
        tasks.put(task.getId(), task);
        System.out.println("Обновлена задача: \n" + task + "\n" +
                "Текущий статус: " + task.getStatus() + '\n');
    }

    public void updateEpic(Epic epic) { //Метод обновления эпика
        changeStatusEpic(epic);
        epics.put(epic.getId(), epic);
        System.out.println("Обновлен эпик: \n" + epic + "\n" +
                "Текущий статус: " + epic.getStatus() + '\n');
    }

    public void updateSubtask(Subtask subtask) { //Метод обновления подзадачи
        subtasks.put(subtask.getId(), subtask);
        System.out.println("Обновлен эпик: \n" + subtask + "\n" +
                "Текущий статус: " + subtask.getStatus() + '\n');
    }

    private void changeStatusEpic(Epic epic) { // Метод расчета статуса эпика по статусам включенных в него задач
        if (epic.getSubtasksInEpic().isEmpty()) { // Проверяяем наличие подзадач у эпика
            epic.setStatus(status[0]); //NEW
        } else {
            // Получаем список id подзадач-наследников из поля класса Epic
            ArrayList<Integer> subtaskOfEpic = epic.getSubtasksInEpic();
            ArrayList<String> statusSubtasks = new ArrayList<>(); //Список статусов подзадач эпика
            for (Integer id : subtaskOfEpic) { // Проход циклом по задачам для заполнения списка со статусами подзадач
                statusSubtasks.add(subtasks.get(id).getStatus());
            }
            // Условие condition для статуса эпика
            boolean[] condition = {!statusSubtasks.contains(status[1]) && !statusSubtasks.contains(status[2]),//NEW
                    !statusSubtasks.contains(status[0]) && !statusSubtasks.contains(status[1]),//DONE
            };

            if (condition[0]) {
                epic.setStatus(status[0]); //NEW
            } else if (condition[1]) {
                epic.setStatus(status[2]); //DONE
            } else {
                epic.setStatus(status[1]);
            }
        }
    }
}
