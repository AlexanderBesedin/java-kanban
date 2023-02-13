import model.Epic;
import model.Subtask;
import model.Task;
import service.*;

public class Main {

    public static void main(String[] args) {
        String name;
        String description;
        int epicId;

        Task task;
        Epic epic;
        Subtask subtask;

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        //Создал Задачу1
        name = "Забрать загранпаспорт";
        description = "Нужно забрать загранпаспорт в отделении миграции МВД №5.";
        task = new Task(name, description);
        taskManager .createTask(task);

        //Создал Задачу2
        name = "Купить корм кошке";
        description = "Купить корм Brit Premium в магзине 4 лапы в Ленте.";
        task = new Task(name, description);
        taskManager .createTask(task);

        //Создал Эпик с двумя Подзадачами
        // Создал эпик
        name = "Получить водительское удостоверение";
        description = "Получить ВУ категории А и В. Пройти обучение в автошколе Ягуар.";
        epic = new Epic(name, description);
        taskManager .createTask(epic); //ID == 3

        // Создал первую подзадачу
        name = "Пройти обучение в автошколе Ягуар";
        description = "Пройти обучение теории и практике вождения (50 часов). Сдать внутренние экзамены.";
        epicId = epic.getId(); //ID == 3
        subtask = new Subtask(name, description, epicId);
        taskManager .createTask(subtask);

        // Создал вторую подзадачу
        name = "Сдать экзамен в ГИБДД";
        description = "Сдать теорию вождения, автодром и вождение в городе на экзамене в ГИБДД на категорию В.";
        epicId = epic.getId();
        subtask = new Subtask(name, description, epicId);
        taskManager .createTask(subtask);

        // Создал Эпик с одной Подзадачей
        name = "Подготовиться к отпуску";
        description = "Закрыть дела, задачи перед отпуском, купить билеты, заброниронировать отель.";
        epic = new Epic(name, description);
        taskManager .createTask(epic); //ID == 6
        // Создал подзадачу эпика
        name = "Сдать проекты по учебе";
        description = "Выполнить и сдать проекты 3 и 4 спринтов курса по Java.";
        epicId = epic.getId();
        subtask = new Subtask(name, description, epicId);
        taskManager .createTask(subtask);

        // Печать списков созданных задач, эпиков, подзадач
        taskManager.printListTasks();
        taskManager.printListEpics();
        taskManager.printListSubtasks();
        taskManager.printSubtaskOfEpic(3);
        taskManager.printSubtaskOfEpic(6);

        // Изменил статус задачи 1 на IN_PROGRESS и задачи 2 на DONE
        taskManager .changeTaskStatus(1, Status.IN_PROGRESS);
        taskManager .changeTaskStatus(2, Status.DONE);
        taskManager .printListTasks();

        //Изменил статус поздадач эпика 3 на DONE
        taskManager .changeSubtaskStatus(4, Status.DONE);
        epicId = 3;
        taskManager.printSubtaskOfEpic(3);
        taskManager.printTask(epicId);

        taskManager .changeSubtaskStatus(5, Status.DONE);
        taskManager.printSubtaskOfEpic(epicId);
        taskManager.printTask(epicId);

        taskManager .changeSubtaskStatus(7, Status.IN_PROGRESS);
        epicId = 6;
        taskManager .printSubtaskOfEpic(epicId);
        taskManager .printTask(epicId);

        //Удаляю задачу 2
        taskManager .removeTask(2);
        //Удаляю эпик 3
        taskManager .removeEpic(3);

        taskManager .printListTasks();
        taskManager .printListEpics();
        taskManager .printListSubtasks();
        taskManager .printSubtaskOfEpic(3);
        taskManager .printSubtaskOfEpic(6);

        taskManager .clearSubtasks();
        taskManager .printListSubtasks();

        //Проверка отсутствия задач у эпика 6.
        taskManager .printSubtaskOfEpic(6);
        taskManager .printTask(6);
        System.out.println(taskManager .getSubtasksOfEpic(6));
    }
}