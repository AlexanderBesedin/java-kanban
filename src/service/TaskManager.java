package service;

import model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TaskManager { // Класс хранения задач всех типов
    private static LinkedHashMap<Integer, Task> tasks = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Epic> epics = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Subtask> subtasks = new LinkedHashMap<>();

    public static LinkedHashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static LinkedHashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public static LinkedHashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
    //Посчитал логичным перенести геттеры из ListMaker
    public static Task getTask(int id) { // Получить задачу по идентификатору
        return tasks.getOrDefault(id, null);
    }

    public static Epic getEpic(int id) { // Получить эпик по идентификатору
        return epics.getOrDefault(id, null);
    }

    public static Subtask getSubtask(int id) { // Получить подзадачу по идентификатору
        return subtasks.getOrDefault(id, null);
    }

    public static ArrayList<Subtask> getSubtasksOfEpic(int id) { // Получить список подзадач выбранного эпика
        if (epics.containsKey(id)) {
            ArrayList<Integer> numSubtasks = getEpic(id).getSubtasksInEpic();
            ArrayList<Subtask> list = new ArrayList<>();
            for (Integer subtaskId : numSubtasks) { // Проходим по списку id задач выбранного эпика
                list.add(subtasks.get(subtaskId));
            }
            return list;
        } else {
            return null;
        }
    }

    public static ArrayList<Task> getListTasks() { // Получить список всех задач
        ArrayList<Task> list = new ArrayList<>(tasks.values());
        return list;
    }

    public static ArrayList<Epic> getListEpics() { // Получить список всех эпиков
        ArrayList<Epic> list = new ArrayList<>(epics.values());
        return list;
    }

    public static ArrayList<Subtask> getListSubtasks() { // Получить список всех подзадач
        ArrayList<Subtask> list = new ArrayList<>(subtasks.values());
        return list;
    }

    public static void changeTaskStatus(int id, Status status) {
        tasks.get(id).setStatus(status);
    }

    public static void changeSubtaskStatus(int id, Status status) {
        subtasks.get(id).setStatus(status);
        int epicId = subtasks.get(id).getEpicId();
        updateEpicStatus(epics.get(epicId)); // Обновил статус родительского эпика
    }

// Перенес сюда updateEpicStatus()
    public static void updateEpicStatus(Epic epic) { // Метод обновления статуса эпика по статусам включенных подзадач
        if (epic.getSubtasksInEpic().isEmpty()) { // Проверяяем наличие подзадач у эпика
            epic.setStatus(Status.NEW);
            // Если эпик не имеет поздадач(пустой) - метод завершатется на данном месте, как условие блока if
            // Если имеется ввиду иное конкретное улучшение - готов его реализовать при проверке проекта 4 спринта
        } else {
            // Получаем список id подзадач-наследников из поля класса Epic
            ArrayList<Integer> subtaskOfEpic = epic.getSubtasksInEpic();
            ArrayList<Status> statusSubtasks = new ArrayList<>(); //Список статусов подзадач эпика
            for (Integer id : subtaskOfEpic) { // Проход циклом для заполнения списка со статусами подзадач
                statusSubtasks.add(getSubtasks().get(id).getStatus());
            }
            // Условия для статуса эпика
            boolean isNewStatus = !statusSubtasks.contains(Status.IN_PROGRESS)
                    && !statusSubtasks.contains(Status.DONE);
            boolean isDoneStatus = !statusSubtasks.contains(Status.NEW)
                    && !statusSubtasks.contains(Status.IN_PROGRESS);

            if (isNewStatus) {
                epic.setStatus(Status.NEW);
            } else if (isDoneStatus) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}