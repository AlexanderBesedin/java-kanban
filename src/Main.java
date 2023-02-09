import model.*;
import service.ListsMaker;
import service.TaskCreator;
import service.TaskManager;
import service.TaskRemover;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String name;
        String description;
        int epicId;

        Task task;
        Epic epic;
        Subtask subtask;

        TaskManager taskManager = new TaskManager();
        TaskCreator taskCreator = new TaskCreator(
                taskManager.getStatus(), taskManager.getTasks(),
                taskManager.getEpics(),taskManager.getSubtasks());

        TaskRemover taskRemover = new TaskRemover(taskManager.getStatus(), taskManager.getTasks(),
                taskManager.getEpics(), taskManager.getSubtasks());

        ListsMaker listsMaker = new ListsMaker(taskManager.getTasks(), taskManager.getEpics(),
                taskManager.getSubtasks());


        //Создал Задачу1
        name = "Забрать загранпаспорт";
        description = "Нужно забрать забрать загранпаспорт в отделении миграции МВД №5.";
        task = new Task(name, description);
        taskCreator.createTask(task);

        //Создал Задачу2
        name = "Купить корм кошке";
        description = "Купить корм Brit Premium в магзине 4 лапы в Ленте.";
        task = new Task(name, description);
        taskCreator.createTask(task);

        //Создал Эпик с двумя Подзадачами
        // Создал эпик
        name = "Получить водительское удостоверение";
        description = "Получить ВУ категории А и В. Пройти обучение в автошколе Ягуар.";
        epic = new Epic(name, description);
        taskCreator.createTask(epic); //ID == 3

        // Создал первую подзадачу
        name = "Пройти обучение в автошколе Ягуар";
        description = "Пройти обучение теории и практике вождения (50 часов). Сдать внутренние экзамены.";
        epicId = epic.getId(); //ID == 3
        subtask = new Subtask(name, description, epicId);
        taskCreator.createTask(subtask);

        // Создал вторую подзадачу
        name = "Сдать экзамен в ГИБДД";
        description = "Сдать теорию вождение, автодром и вождение в городе на экзамене в ГИБДД.";
        epicId = epic.getId();
        subtask = new Subtask(name, description, epicId);
        taskCreator.createTask(subtask);

        // Создал Эпик с одной Подзадачей
        name = "Подготовиться к отпуску";
        description = "Закрыть дела, задачи перед отпуском, купить билеты, заброниронировать отель.";
        epic = new Epic(name, description);
        taskCreator.createTask(epic); //ID == 6
        // Создал подзадачу эпика
        name = "Сдать проекты по учебе";
        description = "Выполнить и сдать проекты 3 и 4 спринтов курса по Java.";
        epicId = epic.getId();
        subtask = new Subtask(name, description, epicId);
        taskCreator.createTask(subtask);

        // Печать списков созданных задач, эпиков, подзадач
        listsMaker.getListTasks();
        listsMaker.getListEpics();
        listsMaker.getListSubtasks();
        listsMaker.getSubtasksOfEpic(3);
        listsMaker.getSubtasksOfEpic(6);

        // Изменил статус задачи 1 на IN_PROGRESS и задачи 2 на DONE
        taskManager.changeTaskStatus(1, 1);
        taskManager.changeTaskStatus(2, 2);
        listsMaker.getListTasks();

        //Изменил статус поздадач эпика 3 на DONE
        taskManager.changeSubtaskStatus(4, 2);
        epicId = taskManager.getSubtasks().get(4).getEpicId();
        epic = taskManager.getEpics().get(epicId);
        taskCreator.updateEpic(epic);
        listsMaker.getSubtasksOfEpic(epicId);

        taskManager.changeSubtaskStatus(5, 2);
        epicId = taskManager.getSubtasks().get(5).getEpicId();
        epic = taskManager.getEpics().get(epicId);
        taskCreator.updateEpic(epic);
        listsMaker.getSubtasksOfEpic(epicId);

        //Изменил статус поздадач эпика 6 на IN_PROGRESS
        taskManager.changeSubtaskStatus(7, 1);
        epicId = taskManager.getSubtasks().get(7).getEpicId();
        epic = taskManager.getEpics().get(epicId);
        taskCreator.updateEpic(epic);
        listsMaker.getSubtasksOfEpic(epicId);
        listsMaker.getEpic(epicId);

        //Удаляю задачу 2
        taskRemover.removeTask(2);
        //Удаляю эпик 3
        taskRemover.removeEpic(3);

        listsMaker.getListTasks();
        listsMaker.getListEpics();
        listsMaker.getListSubtasks();
        listsMaker.getSubtasksOfEpic(3);
        listsMaker.getSubtasksOfEpic(6);

    }
}
