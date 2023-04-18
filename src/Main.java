import model.Epic;
import model.Subtask;
import model.Task;
import service.*;
import service.FileBackedTasksManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static final String HOME = System.getProperty("user.home");
    public static void main(String[] args) throws IOException {
        String name;
        String description;
        int epicId;

        Task task;
        Epic epic;
        Subtask subtask;

        String child = "dev" + "/java-kanban/resources/saved.csv";
        File file = new File(HOME, child);

        TaskManager taskManager = Managers.getDefault();
        taskManager = Managers.getFBManager(file);

        // условие для тестирования: файл сущ-ет - читаем файл, файла нет - выполняем блок создания и просмотра задач.
        if (file.exists()) {
            FileBackedTasksManager.loadFromFile(file);

            taskManager.printListTasks();
            taskManager.printListEpics();
            taskManager.printListSubtasks();
            taskManager.printHistory();

            Files.deleteIfExists(file.toPath());
        } else {

            //Создал Задачу #1
            name = "Купить корм кошке";
            description = "Купить корм Brit Premium в магзине 4 лапы в Ленте.";
            task = new Task(name, description);
            taskManager.createTask(task);

            //Создал Задачу #2
            name = "Забрать загранпаспорт";
            description = "Нужно забрать загранпаспорт в отделении миграции МВД №5.";
            task = new Task(name, description);
            taskManager.createTask(task);

            // Создал эпик #3 с тремя подзадачами
            name = "Получить водительское удостоверение";
            description = "Получить ВУ категории А и В. Пройти обучение в автошколе Ягуар.";
            epic = new Epic(name, description);
            taskManager.createTask(epic); //ID == 3

            // Создал первую подзадачу #4
            name = "Пройти обучение в автошколе Ягуар";
            description = "Пройти обучение теории и практике вождения (50 часов). Сдать внутренние экзамены.";
            epicId = epic.getId(); //ID == 3
            subtask = new Subtask(name, description, epicId);
            taskManager.createTask(subtask);

            // Создал вторую подзадачу #5
            name = "Сдать экзамен в ГИБДД на категорию В";
            description = "Сдать теорию вождения, автодром и вождение в городе на экзамене в ГИБДД на категорию В.";
            epicId = epic.getId();
            subtask = new Subtask(name, description, epicId);
            taskManager.createTask(subtask);

            // Создал третью подзадачу #6
            name = "Сдать экзамен в ГИБДД на категорию А";
            description = "Сдать практический экзамен на площадке в ГИБДД на категорию А.";
            epicId = epic.getId();
            subtask = new Subtask(name, description, epicId);
            taskManager.createTask(subtask);

            // Создал Эпик без Подзадач #7
            name = "Подготовиться к отпуску";
            description = "Закрыть дела, задачи перед отпуском, купить билеты, заброниронировать отель.";
            epic = new Epic(name, description);
            taskManager.createTask(epic);


            taskManager.getTask(1); // первый посмотр
            taskManager.printHistory(); //Печатаю историю просмотров. 1 просмотр
            // Должно быть:
            //  * Epic 3 - считается просмотр эпика при созданиии, обновлении его подзадач
            //  * Task 1

            taskManager.getTask(2); // второй просмотр
            taskManager.printHistory(); // Печатаю историю просмотров. 2 просмотра
            // Должно быть:
            //  * Epic 3
            //  * Task 1
            //  * Task 2

            // Обновляем задачу 1
            task = taskManager.getTask(1); // 3 просмотр
            task.setName("Зайти в магазин 4 лапы");
            task.setDescription("Купить кошке корм Brit Premium и наполнитель.");
            taskManager.updateTask(task);

            // Изменил статус задачи 1 на DONE и задачи 2 на IN_PROGRESS
            taskManager.changeTaskStatus(1, Status.DONE);
            taskManager.changeTaskStatus(2, Status.IN_PROGRESS);
            taskManager.printHistory(); // Проверка удаления старого  и записи нового просмотра задачи1. Всего 2 просмотра
            // Должно быть:
            //  * Epic 3
            //  * Task 1
            //  * Task 2

            //Изменил статус поздадач эпика 3 на DONE
            taskManager.changeSubtaskStatus(4, Status.DONE);
            epicId = taskManager.getSubtask(4).getEpicId(); // 4ое обращение к задачи через getSubtask. Всего 3 просмотра
            //taskManager.printSubtasksOfEpic(epicId);
            taskManager.getEpic(epicId); // 5ый просмотр (эпик#3), Всего 4 просмотра
            taskManager.printHistory();
            // Должно быть:
            //  * Task 1
            //  * Task 2
            //  * Subtask 4
            //  * Epic 3

            taskManager.changeSubtaskStatus(5, Status.DONE);
            //taskManager.printSubtasksOfEpic(epicId);
            taskManager.getSubtask(5); // Вызов подзадачи №5
            taskManager.getSubtask(4); // Вызов подзадачи №4
            taskManager.printHistory(); //Печатаю историю просмотров. 6 просмотров
            // Должно быть:
            //  * Task 1
            //  * Task 2
            //  * Epic 3
            //  * Subtask 5
            //  * Subtask 4

            taskManager.getEpic(3); // Вызов эпика №3
            taskManager.changeSubtaskStatus(6, Status.DONE);
            taskManager.printHistory(); //Печатаю историю просмотров. 6 просмотров
            // Должно быть:
            //  * Task 1
            //  * Task 2
            //  * Subtask 5
            //  * Subtask 4
            //  * Subtask 6
            //  * Epic 3

    /*
            taskManager.getSubtask(6); //Вызов подзадачи 6
            taskManager.getEpic(7); //Вызов эпика №7
            taskManager.removeSubtask(4);
            taskManager.removeTask(2);
            taskManager.printHistory(); //Печатаю историю просмотров. 8 просмотров
            // Должно быть:
            //  * Task 1
            //  * Subtask 5
            //  * Subtask 6
            //  * Epic 3
            //  * Epic 7

            taskManager.removeEpic(3); // Удаляем эпик 3 с подзадачами 5 и 6
            taskManager.printHistory();
            // Должно быть:
            //  * Task 1
            //  * Epic 7

            taskManager.clearTasks();
            taskManager.clearEpics();
            //taskManager.removeEpic(7);
            taskManager.printHistory();
            // Должно быть:
    */
        }

    }
}