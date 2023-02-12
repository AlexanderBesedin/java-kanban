package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

import static service.TaskManager.*;

public class ListsMaker { // Класс для вывода задач по id и для вывода списков задач

    public void printTask(int id) { // Метод вывода задачи любого типа по существующему id
        if (getTasks().containsKey(id)) { // Проверка наличия id у мапы tasks
            System.out.println("Задача numID-" + id + ":\n" + getTask(id) + '\n');
        } else if (getEpics().containsKey(id)) { // Проверка наличия id у мапы epics
            System.out.println("Эпик numID-" + id + ":\n" + getEpic(id) + '\n');
        } else if (getSubtasks().containsKey(id)) { // Проверка наличия id у мапы subtasks
            System.out.println("Подзадача numID-" + id + ":\n" + getSubtask(id) + '\n');
        } else {
            System.out.println("Указанного numID-" + id + " не существует.\n");
        }
    }

    public void printSubtaskOfEpic(int id) { //Метод печати позадач выбранного эпика
        if (getEpics().containsKey(id)) {
            if (!getEpic(id).getSubtasksInEpic().isEmpty()) {
                String list = "Список подзадач эпика numID-" + id + ":\n";
                for (Subtask subtask : getSubtasksOfEpic(id)) {
                    list += "  " + subtask + '\n';
                }
                System.out.println(list);
            } else {
                System.out.println("У эпика numID-" + id + " подзадачи отсутствуют.\n");
            }
        } else {
            System.out.println("Эпик numID-" + id + " не существует.\n");
        }
        ;
    }

    public void printListTasks() { // Метод печати списка задач
        if (getTasks().isEmpty()) {
            System.out.println("Ни одна задача пока не создана.\n");
        } else {
            String list = "Список задач:\n";
            for (Task task : getListTasks()) {
                list += "  " + task + '\n';
            }
            System.out.println(list);
        }
    }

    public void printListEpics() { // Метод печати списка эпиков
        if (getEpics().isEmpty()) {
            System.out.println("Ни один эпик пока не создан.\n");
        } else {
            String list = "Список эпиков:\n";
            for (Epic epic : getListEpics()) {
                list += "  " + epic + '\n';
            }
            System.out.println(list);
        }
    }

    public void printListSubtasks() { // Метод печати списка подзадач
        if (getSubtasks().isEmpty()) {
            System.out.println("Ни одна подзадача пока не создана.\n");
        } else {
            String list = "Список подзадач:\n";
            for (Subtask subtask : getListSubtasks()) {
                list += "  " + subtask + '\n';
            }
            System.out.println(list);
        }
    }
}