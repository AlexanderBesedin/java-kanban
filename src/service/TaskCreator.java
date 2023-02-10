package service;

import model.*;
import static service.TaskManager.*;

import java.util.ArrayList;

public class TaskCreator {
    private static int id;

    public void createTask(Task task) { // Метод создания задачи, эпика, подзадачи
        if (task instanceof Subtask) { // Проверка объекта-аргумента на принаджежность классу Subtask
            id++;
            Subtask subtask = (Subtask) task; // Приводим объект-аргумент к типу Subtask
            subtask.setId(id); // Присваиваем id
            getEpics().get(subtask.getEpicId()).setSubtaskInEpic(id); // Записываем в список эпика id подзадачи
            subtask.setStatus(Status.getStatus(0)); //присваиваем статус NEW
            getSubtasks().put(id, subtask); // Сохраняем в соответствующую мапу
            System.out.println("Создана подзадача: \n" + subtask + '\n');
        } else if (task instanceof Epic) { // Проверка объекта-аргумента на принаджежность классу Epic
            id++;
            Epic epic = (Epic) task;
            epic.setId(id);
            epic.setStatus(Status.getStatus(0));
            getEpics().put(id, epic);
            System.out.println("Создан эпик: \n" + epic + '\n');
        } else if (Task.class != task.getClass()) {
            System.out.println("Создайте задачу типа Task, Epic или Subtask.\n");
        } else {
            id++;
            task.setId(id);
            task.setStatus(Status.getStatus(0));
            getTasks().put(id, task);
            System.out.println("Создана задача: \n" + task + '\n');
        }
    }

    public void updateTask(Task task) { //Метод обновления задачи
        getTasks().put(task.getId(), task);
        System.out.println("Обновлена задача: \n" + task + "\n" +
                "Текущий статус: " + task.getStatus() + '\n');
    }

    public void updateEpic(Epic epic) { //Метод обновления эпика
        changeStatusEpic(epic);
        getEpics().put(epic.getId(), epic);
        System.out.println("Обновлен эпик: \n" + epic + "\n" +
                "Текущий статус: " + epic.getStatus() + '\n');
    }

    public void updateSubtask(Subtask subtask) { //Метод обновления подзадачи
        getSubtasks().put(subtask.getId(), subtask);
        System.out.println("Обновлен эпик: \n" + subtask + "\n" +
                "Текущий статус: " + subtask.getStatus() + '\n');
    }

    public static void changeStatusEpic(Epic epic) { // Метод расчета статуса эпика по статусам включенных в него задач
        if (epic.getSubtasksInEpic().isEmpty()) { // Проверяяем наличие подзадач у эпика
            epic.setStatus(Status.getStatus(0)); //NEW
        } else {
            // Получаем список id подзадач-наследников из поля класса Epic
            ArrayList<Integer> subtaskOfEpic = epic.getSubtasksInEpic();
            ArrayList<String> statusSubtasks = new ArrayList<>(); //Список статусов подзадач эпика
            for (Integer id : subtaskOfEpic) { // Проход циклом по задачам для заполнения списка со статусами подзадач
                statusSubtasks.add(getSubtasks().get(id).getStatus());
            }
            // Условия для статуса эпика
            boolean isNewStatus = !statusSubtasks.contains(Status.getStatus(1))
                    && !statusSubtasks.contains(Status.getStatus(2)); //NEW
            boolean isDoneStatus = !statusSubtasks.contains(Status.getStatus(0))
                    && !statusSubtasks.contains(Status.getStatus(1)); //DONE

            if (isNewStatus) {
                epic.setStatus(Status.getStatus(0)); //NEW
            } else if (isDoneStatus) {
                epic.setStatus(Status.getStatus(2)); //DONE
            } else {
                epic.setStatus(Status.getStatus(1));
            }
        }
    }
}
