package service;

import java.util.ArrayList;

import static service.TaskManager.*;

public class ListsMaker { // Класс для вывода задач по id и для вывода списков задач

    public String getTask(int id) { // 5 - Получить задачу по идентификатору
        if (getTasks().containsKey(id)) {
            return getTasks().get(id).toString() + '\n';
        } else {
            return "Задача numID-" + id + " не существует.\n";
        }
    }

    public String getEpic(int id) { // 5 - Получить эпик по идентификатору
        if (getEpics().containsKey(id)) {
            return getEpics().get(id).toString() + '\n';
        } else {
            return "Эпик numID-" + id + " не существует.\n";
        }
    }

    public String getSubtask(int id) { // 5 - Получить подзадачу по идентификатору
        if (getSubtasks().containsKey(id)) {
            return getSubtasks().get(id).toString() + '\n';
        } else {
            return "Подзадача numID-" + id + " не существует.\n";
        }
    }

    public String getListTasks() { // 6-Получить список всех задач
        if (getTasks().isEmpty()) {
            return "Ни одна задача пока не создана.\n";
        } else {
            String list = "Список задач:\n";
            for (Integer id : getTasks().keySet()) {
                  list += "  " + getTasks().get(id).toString() +'\n';
            }
            return list;
        }
    }

    public String getListEpics() { // 6 - Получить список всех эпиков
        if (getEpics().isEmpty()) {
            return "Ни один эпик пока не создан.\n";
        } else {
            String list = "Список эпиков:\n";
            for (Integer id : getEpics().keySet()) {
                list += "  " + getEpics().get(id).toString() +'\n';
            }
            return list;
        }
    }

    public String getListSubtasks() { // 6 - Получить список всех подзадач
        if (getSubtasks().isEmpty()) {
            return "Ни одна подзадача пока не создана.\n";
        } else {
            String list = "Список подзадач:\n";
            for (Integer id : getSubtasks().keySet()) {
                list += "  " + getSubtasks().get(id).toString() +'\n';
            }
            return list;
        }
    }

    public String getSubtasksOfEpic(int id) { // 7 - Получить список подзадач выбранного эпика
        if (getEpics().containsKey(id)) { // Проверяем наличие эпика с переданным id
            boolean condition = getEpics().get(id).getSubtasksInEpic().isEmpty();
            if (!condition) { // Проверяяем наличие подзадач у эпика
                // Получаем список id подзадач-наследников из поля класса Epic
                ArrayList<Integer> subtaskOfEpic = getEpics().get(id).getSubtasksInEpic();
                String list = "Подзадачи эпика numID-" + id + ":\n";
                for (Integer subtaskId : subtaskOfEpic) { // Печатаем список задач выбранного эпика
                    list += "  " + getSubtasks().get(subtaskId).toString() +'\n';
                }
                return list;
            } else {
                return "У эпика numID-" + id + " подзадачи отсутствуют.\n";
            }
        } else {
            return "Эпик numID-" + id + " не существует.\n";
        }
    }
}
