package service;

import model.Epic;

import java.util.ArrayList;

import static service.TaskManager.*;

public class TaskRemover { // Класс для удаления задач по id, всех задач выбранного типа

    public void removeTask(int id) { // Удалить задачу по идентификатору
        if (getTasks().containsKey(id)) { // Проверяем наличие искомой задачи в хэшмапе tasks по ключу
            System.out.println("Задача " + getTasks().remove(id) + '\n' +
                    "УДАЛЕНА.\n");
        } else {
            System.out.println("Задача numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    public void removeEpic(int id) { // Удалить эпик по идентификатору
        if (getEpics().containsKey(id)) { // Проверяем наличие эпика с введенным идентификатором
            boolean condition = getEpics().get(id).getSubtasksInEpic().isEmpty(); //Условие проверки наличия задач в эпике
            if (!condition) {
                ArrayList<Integer> subtaskOfEpic = getEpics().get(id).getSubtasksInEpic();
                for (Integer subtaskId : subtaskOfEpic) { // Удаляем подзадачи выбранного эпика
                    getSubtasks().remove(subtaskId);
                }
            }
            System.out.println("Эпик " + getEpics().remove(id) + '\n' +
                    "УДАЛЕН.\n");
        } else {
            System.out.println("Эпик numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    public void removeSubtask(int id) { // Удалить подзадачу по идентификатору
        if (getSubtasks().containsKey(id)) {
            int epicId = getSubtasks().get(id).getEpicId();
            Epic epic = getEpics().get(epicId); // Получаем родительский эпик
            epic.removeSubtaskInEpic(id); // Удаляем из списка родительского эпика id подзадачи
            TaskCreator.changeStatusEpic(epic); // Обновляем статус эпика
            System.out.println("Подзадача " + getSubtasks().remove(id) + '\n' +
                    " УДАЛЕНА.\n");
        } else {
            System.out.println("Подзадача numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    public void clearTasks() { // Удалить все задачи
        if (getTasks().isEmpty()) {
            System.out.println("Ни одна задача пока не создана.\n");
        } else {
            getTasks().clear();
            System.out.println("Все задачи удалены.\n");
        }
    }

    public void clearEpics() { // Удалить все эпики с подзадачами
        if (getEpics().isEmpty()) {
            System.out.println("Ни один эпик пока не создан.\n");
        } else {
            getEpics().clear();
            getSubtasks().clear();
            System.out.println("Все эпики с подзадачами удалены.\n");
        }
    }

    public void clearSubtasks() { //Удалить все подзадачи
        if (getSubtasks().isEmpty()) {
            System.out.println("Ни одна подзадача пока не создана.\n");
        } else {
            getSubtasks().clear();
            for (Integer id : getEpics().keySet()) { // После удаления всех подзадач возвращаем статус "NEW" каждому эпику
                Epic epic = getEpics().get(id);
                epic.setStatus(Status.getStatus(0));
            }
            System.out.println("Подзадачи во всех эпиках удалены.\n");
        }
    }
}
