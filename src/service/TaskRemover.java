package service;

import model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TaskRemover { // Класс для удаления задач по id, всех задач выбранного типа
    private String[] status;
    private LinkedHashMap<Integer, Task> tasks;
    private LinkedHashMap<Integer, Epic> epics;
    private LinkedHashMap<Integer, Subtask> subtasks;

    public TaskRemover(String[] status,
                       LinkedHashMap<Integer, Task> tasks,
                       LinkedHashMap<Integer, Epic> epics,
                       LinkedHashMap<Integer, Subtask> subtasks) {
        this.status = status;
        this.tasks = tasks;
        this.epics = epics;
        this.subtasks = subtasks;
    }

    public void removeTask(int id) { // Удалить задачу по идентификатору
        if (tasks.containsKey(id)) { // Проверяем наличие искомой задачи в хэшмапе tasks по ключу
            System.out.println("Задача " + tasks.remove(id) + '\n' +
                    "УДАЛЕНА.\n");
        } else {
            System.out.println("Задача numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    public void removeEpic(int id) { // Удалить эпик по идентификатору
        if (epics.containsKey(id)) { // Проверяем наличие эпика с введенным идентификатором
            boolean condition = epics.get(id).getSubtasksInEpic().isEmpty(); //Условие проверки наличия задач в эпике
            if (!condition) {
                ArrayList<Integer> subtaskOfEpic = epics.get(id).getSubtasksInEpic();
                for (Integer subtaskId : subtaskOfEpic) { // Удаляем подзадачи выбранного эпика
                    subtasks.remove(subtaskId);
                }
            }
            System.out.println("Эпик " + epics.remove(id) + '\n' +
                    "УДАЛЕН.\n");
        } else {
            System.out.println("Эпик numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    public void removeSubtask(int id) { // Удалить подзадачу по идентификатору
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.get(id).getEpicId()); // Получаем родительский эпик
            epic.removeSubtaskInEpic(id); // Удаляем из списка родительского эпика id подзадачи
            System.out.println("Подзадача " + subtasks.remove(id) + '\n' +
                    " УДАЛЕНА.\n");
        } else {
            System.out.println("Подзадача numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    public void clearTasks() { // Удалить все задачи
        if (tasks.isEmpty()) {
            System.out.println("Ни одна задача пока не создана.\n");
        } else {
            tasks.clear();
            System.out.println("Все задачи удалены.\n");
        }
    }

    public void clearEpics() { // Удалить все эпики с подзадачами
        if (epics.isEmpty()) {
            System.out.println("Ни один эпик пока не создан.\n");
        } else {
            epics.clear();
            subtasks.clear();
            System.out.println("Все эпики с подзадачами удалены.\n");
        }
    }

    public void clearSubtasks() { //Удалить все подзадачи
        if (subtasks.isEmpty()) {
            System.out.println("Ни одна подзадача пока не создана.\n");
        } else {
            subtasks.clear();
            for (Integer id : epics.keySet()) { // После удаления всех подзадач возвращаем статус "NEW" каждому эпику
                Epic epic = epics.get(id);
                epic.setStatus(status[0]);
            }
            System.out.println("Подзадачи во всех эпиках удалены.\n");
        }
    }
}
