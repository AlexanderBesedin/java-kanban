import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {
        String name;
        String description;
        int epicId;

        Task task;
        Epic epic;
        Subtask subtask;

        TaskManager taskManager = new TaskManager();
        TaskCreator taskCreator = new TaskCreator();
        TaskRemover taskRemover = new TaskRemover();
        ListsMaker listsMaker = new ListsMaker();


        //Создал Задачу1
        name = "Забрать загранпаспорт";
        description = "Нужно забрать загранпаспорт в отделении миграции МВД №5.";
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
        description = "Сдать теорию вождения, автодром и вождение в городе на экзамене в ГИБДД на категорию В.";
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
        System.out.println(listsMaker.getListTasks());
        System.out.println(listsMaker.getListEpics());
        System.out.println(listsMaker.getListSubtasks());
        System.out.println(listsMaker.getSubtasksOfEpic(3));
        System.out.println(listsMaker.getSubtasksOfEpic(6));

        // Изменил статус задачи 1 на IN_PROGRESS и задачи 2 на DONE
        TaskManager.changeTaskStatus(1, 1);
        TaskManager.changeTaskStatus(2, 2);
        System.out.println(listsMaker.getListTasks());

        //Изменил статус поздадач эпика 3 на DONE
        TaskManager.changeSubtaskStatus(4, 2);
        epicId = TaskManager.getSubtasks().get(4).getEpicId();
        epic = TaskManager.getEpics().get(epicId);
        taskCreator.updateEpic(epic);
        System.out.println(listsMaker.getSubtasksOfEpic(epicId));

        TaskManager.changeSubtaskStatus(5, 2);
        epicId = TaskManager.getSubtasks().get(5).getEpicId();
        epic = TaskManager.getEpics().get(epicId);
        taskCreator.updateEpic(epic);
        System.out.println(listsMaker.getSubtasksOfEpic(epicId));

        //Изменил статус поздадач эпика 6 на IN_PROGRESS
        TaskManager.changeSubtaskStatus(7, 1);
        epicId = TaskManager.getSubtasks().get(7).getEpicId();
        epic = TaskManager.getEpics().get(epicId);
        taskCreator.updateEpic(epic);
        System.out.println(listsMaker.getSubtasksOfEpic(epicId));
        System.out.println(listsMaker.getEpic(epicId));

        //Удаляю задачу 2
        taskRemover.removeTask(2);
        //Удаляю эпик 3
        taskRemover.removeEpic(3);

        System.out.println(listsMaker.getListTasks());
        System.out.println(listsMaker.getListEpics());
        System.out.println(listsMaker.getListSubtasks());
        System.out.println(listsMaker.getSubtasksOfEpic(3));
        System.out.println(listsMaker.getSubtasksOfEpic(6));
    }
}
