package service;

import model.Epic;
import model.Subtask;
import model.Task;

import static service.TaskManager.*;

public class TaskCreator {
    private static int id;

    public void createTask(Task task) { // Метод создания задачи, эпика, подзадачи
        if (task instanceof Subtask) { // Проверка объекта-аргумента на принаджежность классу Subtask
            id++;
            Subtask subtask = (Subtask) task; // Приводим объект-аргумент к типу Subtask
            subtask.setId(id); // Присваиваем id
            subtask.setStatus(Status.NEW); //присваиваем статус NEW
            getSubtasks().put(id, subtask); // Сохраняем в соответствующую мапу
            getEpics().get(subtask.getEpicId()).setSubtaskInEpic(id); // Записываем в список родительского эпика id подзадачи
            updateEpicStatus(getEpics().get(subtask.getEpicId())); // Обновляем статсус родительского эпика
            System.out.println("Создана подзадача: \n" + subtask + '\n');
        } else if (task instanceof Epic) { // Проверка объекта-аргумента на принаджежность классу Epic
            id++;
            Epic epic = (Epic) task;
            epic.setId(id);
            epic.setStatus(Status.NEW);
            getEpics().put(id, epic);
            System.out.println("Создан эпик: \n" + epic + '\n');
        } else if (Task.class != task.getClass()) {
            System.out.println("Создайте задачу типа Task, Epic или Subtask.\n");
        } else {
            id++;
            task.setId(id);
            task.setStatus(Status.NEW);
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
        updateEpicStatus(epic);
        getEpics().put(epic.getId(), epic);
        System.out.println("Обновлен эпик: \n" + epic + "\n" +
                "Текущий статус: " + epic.getStatus() + '\n');
    }

    public void updateSubtask(Subtask subtask) { //Метод обновления подзадачи
        getSubtasks().put(subtask.getId(), subtask);
        Epic epic = getEpics().get(subtask.getEpicId());
        updateEpicStatus(epic); // Обновляем статус родительского эпика
        System.out.println("Обновлена подзадача: \n" + subtask + "\n" +
                "Текущий статус: " + subtask.getStatus() + '\n');
    }
}
