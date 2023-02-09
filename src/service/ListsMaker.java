package service;

import model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ListsMaker { // Класс для вывода задач по id и для вывода списков задач
    private LinkedHashMap<Integer, Task> tasks;
    private LinkedHashMap<Integer, Epic> epics;
    private LinkedHashMap<Integer, Subtask> subtasks;

    public ListsMaker(LinkedHashMap<Integer, Task> tasks,
                      LinkedHashMap<Integer, Epic> epics,
                      LinkedHashMap<Integer, Subtask> subtasks) {
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
    }

    public void getTask(int id) { // 5 - Получить задачу по идентификатору
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id).toString() + '\n');
        } else {
            System.out.println("Задача numID-" + id + " не существует.\n");
        }
    }

    public void getEpic(int id) { // 5 - Получить эпик по идентификатору
        if (epics.containsKey(id)) {
            System.out.println(epics.get(id).toString() + '\n');
        } else {
            System.out.println("Эпик numID-" + id + " не существует.\n");
        }
    }

    public void getSubtask(int id) { // 5 - Получить подзадачу по идентификатору
        if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id).toString() + '\n');
        } else {
            System.out.println("Подзадача numID-" + id + " не существует.\n");
        }
    }

    public void getListTasks() { // 6-Получить список всех задач
        if (tasks.isEmpty()) {
            System.out.println("Ни одна задача пока не создана.\n");
        } else {
            System.out.println("Список задач:");
            for (Integer id : tasks.keySet()) {
                System.out.println("  " + tasks.get(id).toString());
            }
            System.out.print('\n'); //Создаем отступ в выводе
        }
    }

    public void getListEpics() { // 6 - Получить список всех эпиков
        if (epics.isEmpty()) {
            System.out.println("Ни один эпик пока не создан.\n");
        } else {
            System.out.println("Список эпиков:");
            for (Integer id : epics.keySet()) {
                System.out.println("  " + epics.get(id).toString());
            }
            System.out.print('\n');
        }
    }

    public void getListSubtasks() { // 6 - Получить список всех подзадач
        if (subtasks.isEmpty()) {
            System.out.println("Ни одна подзадача пока не создана.\n");
        } else {
            System.out.println("Список подзадач:");
            for (Integer id : subtasks.keySet()) {
                System.out.println("  " + subtasks.get(id).toString());
            }
            System.out.print('\n');
        }
    }

    public void getSubtasksOfEpic(int id) { // 7 - Получить список подзадач выбранного эпика
        if (epics.containsKey(id)) { // Проверяем наличие эпика с переданным id
            boolean condition = epics.get(id).getSubtasksInEpic().isEmpty();
            if (!condition) { // Проверяяем наличие подзадач у эпика
                // Получаем список id подзадач-наследников из поля класса Epic
                ArrayList<Integer> subtaskOfEpic = epics.get(id).getSubtasksInEpic();
                System.out.println("Подзадачи эпика numID-" + id + ':');
                for (Integer subtaskId : subtaskOfEpic) { // Печатаем список задач выбранного эпика
                    System.out.println("  " + subtasks.get(subtaskId).toString());
                }
                System.out.print('\n');
            } else {
                System.out.println("У эпика numID-" + id + " подзадачи отсутствуют.\n");
            }
        } else {
            System.out.println("Эпик numID-" + id + " не существует.\n");
        }
    }
}
