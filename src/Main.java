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

        TaskManager taskManager = Managers.getDefault();

        //Создал Задачу1
        name = "Забрать загранпаспорт";
        description = "Нужно забрать загранпаспорт в отделении миграции МВД №5.";
        task = new Task(name, description);
        taskManager.createTask(task);

        //Создал Задачу2
        name = "Купить корм кошке";
        description = "Купить корм Brit Premium в магзине 4 лапы в Ленте.";
        task = new Task(name, description);
        taskManager.createTask(task);

        //Создал Эпик с двумя Подзадачами
        // Создал эпик
        name = "Получить водительское удостоверение";
        description = "Получить ВУ категории А и В. Пройти обучение в автошколе Ягуар.";
        epic = new Epic(name, description);
        taskManager.createTask(epic); //ID == 3

        // Создал первую подзадачу
        name = "Пройти обучение в автошколе Ягуар";
        description = "Пройти обучение теории и практике вождения (50 часов). Сдать внутренние экзамены.";
        epicId = epic.getId(); //ID == 3
        subtask = new Subtask(name, description, epicId);
        taskManager.createTask(subtask);

        // Создал вторую подзадачу
        name = "Сдать экзамен в ГИБДД";
        description = "Сдать теорию вождения, автодром и вождение в городе на экзамене в ГИБДД на категорию В.";
        epicId = epic.getId();
        subtask = new Subtask(name, description, epicId);
        taskManager.createTask(subtask);

        // Создал Эпик с одной Подзадачей
        name = "Подготовиться к отпуску";
        description = "Закрыть дела, задачи перед отпуском, купить билеты, заброниронировать отель.";
        epic = new Epic(name, description);
        taskManager.createTask(epic); //ID == 6
        // Создал подзадачу эпика
        name = "Сдать проекты по учебе";
        description = "Выполнить и сдать проекты 3 и 4 спринтов курса по Java.";
        epicId = epic.getId();
        subtask = new Subtask(name, description, epicId);
        taskManager.createTask(subtask);

        // Печать списков созданных задач, эпиков, подзадач
        taskManager.printListTasks();
        taskManager.printListEpics();
        taskManager.printListSubtasks();
        taskManager.printSubtasksOfEpic(3);
        taskManager.printSubtasksOfEpic(6);

        // Изменил статус задачи 1 на IN_PROGRESS и задачи 2 на DONE
        taskManager.changeTaskStatus(1, Status.IN_PROGRESS);
        taskManager.changeTaskStatus(2, Status.IN_PROGRESS);
        taskManager.printTask(1);

        taskManager.printHistory(); //Печатаю историю просмотров. 1 просмотр
        taskManager.printTask(2);

        taskManager.printHistory(); // Печатаю историю просмотров. 2 просмотра

        // Обновляем задачу 2
        task = taskManager.getTask(2);
        task.setName("Зайти в магазин 4 лапы");
        task.setDescription("Купить кошке корм Brit Premium и наполнитель.");
        taskManager.updateTask(task);

        //Изменил статус поздадач эпика 3 на DONE
        taskManager.changeSubtaskStatus(4, Status.DONE);
        epicId = taskManager.getSubtask(4).getEpicId(); // вызов сабтаски №4 для получения родительского epicId
        taskManager.printSubtasksOfEpic(epicId);
        taskManager.printTask(epicId); // вызов эпика №3

        taskManager.changeSubtaskStatus(5, Status.DONE);
        taskManager.printSubtasksOfEpic(epicId);
        taskManager.printTask(epicId); // Вызов эпика №3
        taskManager.printHistory(); //Печатаю историю просмотров. 6 просмотров

        taskManager.changeSubtaskStatus(7, Status.IN_PROGRESS);
        epicId = taskManager.getSubtask(7).getEpicId(); // вызов сабтаски №7 для получения родительского epicId
        taskManager.printSubtasksOfEpic(epicId);
        taskManager.printTask(epicId); //Вызов эпика №6
        taskManager.printHistory(); //Печатаю историю просмотров. 8 просмотров

        taskManager.printTask(1); //Вызов задачи 1
        taskManager.printTask(4); //Вызов подзадачи 4
        taskManager.printHistory(); //Печатаю историю просмотров. 10 просмотров
        taskManager.printTask(5); // Вызов подзадачи 5
        taskManager.printHistory(); //Печатаю историю просмотров. 11ый просмотр, самый первый просмотр удален из истории
        taskManager.printTask(7); //

        subtask = taskManager.getSubtask(7);
        subtask.setName("Сдать проект 4 спринта.");
        subtask.setDescription("Завершить и сдать 4 спринтов курса по Java.");
        taskManager.updateTask(subtask);
        taskManager.printHistory();

        //Удаляю задачу 2
        taskManager.removeTask(2);
        //Удаляю эпик 3
        taskManager.removeEpic(3);

        taskManager.printListTasks();
        taskManager.printListEpics();
        taskManager.printListSubtasks();
        taskManager.printSubtasksOfEpic(3); //Обращаемся к удаленному эпику
        taskManager.printSubtasksOfEpic(6);

        taskManager.clearSubtasks();
        taskManager.printListSubtasks();
        taskManager.printHistory();
    }
}