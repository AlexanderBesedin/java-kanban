package ru.practicum.kanban;

import ru.practicum.kanban.http.HttpTaskServer;
import ru.practicum.kanban.http.KVServer;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.manager.TaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer().start();
    }

//    public static void main(String[] args) throws IOException { //Реализация для тестирования программы до написания unit-тестов
//        String name;
//        String description;
//        int epicId;
//
//        Task task;
//        Epic epic;
//        Subtask subtask;
//        int thisYear = LocalDateTime.now().getYear();
//        TimeDurationUtil timeFormat = new TimeDurationUtil(InMemoryTaskManager.TIME_PERIOD,
//                InMemoryTaskManager.DATE_TIME_FORMATTER);
//
//        String child = "dev" + "/java-kanban/resources/saved.csv";
//        File file = new File(System.getProperty("user.home"), child);
//
//        TaskManager taskManager = Managers.getFileManager(file);
//
//        // условие для тестирования: файл сущ-ет - читаем файл, файла нет - выполняем блок создания и просмотра задач.
//        if (file.exists()) {
//            FileBackedTasksManager.loadFromFile(file);
//
//            printListTasks(taskManager);
//            printListEpics(taskManager);
//            printListSubtasks(taskManager);
//            printHistory(taskManager);
//
//            for (Task prioritizedTask : taskManager.getPrioritizedTasks()) {
//                System.out.println(prioritizedTask);
//            }
//
//            Files.deleteIfExists(file.toPath());
//        } else {
//
//           //Создал Задачу #1
//            name = "Купить корм кошке";
//            description = "Купить корм Brit Premium в магзине 4 лапы в Ленте.";
//            task = new Task(name, description);
//            taskManager.createTask(task);
//
//            //Создал Задачу #2
//            name = "Забрать загранпаспорт";
//            description = "Нужно забрать загранпаспорт в отделении миграции МВД №5.";
//            task = new Task(name, description);
//            taskManager.createTask(task);
//
//            // Создал эпик #3 с тремя подзадачами
//            name = "Получить водительское удостоверение";
//            description = "Получить ВУ категории А и В. Пройти обучение в автошколе Ягуар.";
//            epic = new Epic(name, description);
//            taskManager.createEpic(epic); //ID == 3
//
//            // Создал первую подзадачу #4
//            name = "Пройти обучение в автошколе Ягуар";
//            description = "Пройти обучение теории и практике вождения (50 часов). Сдать внутренние экзамены.";
//            epicId = epic.getId(); //ID == 3
//            subtask = new Subtask(name, description, epicId);
//            taskManager.createSubtask(subtask);
//
//            // Создал вторую подзадачу #5
//            name = "Сдать экзамен в ГИБДД на категорию В";
//            description = "Сдать теорию вождения, автодром и вождение в городе на экзамене в ГИБДД на категорию В.";
//            epicId = epic.getId();
//            LocalDateTime startTime = timeFormat.getFormatStartTime(String.format("11.05.%d 13:30", thisYear));
//            Duration duration = timeFormat.getFormatDuration("120");
//            subtask = new Subtask(name, description, epicId, startTime, duration);
//            taskManager.createSubtask(subtask);
//
//            // Создал третью подзадачу #6
//            name = "Сдать экзамен в ГИБДД на категорию А";
//            description = "Сдать практический экзамен на площадке в ГИБДД на категорию А.";
//            epicId = epic.getId();
//            startTime = timeFormat.getFormatStartTime(String.format("11.05.%d 11:53", thisYear));
//            duration = timeFormat.getFormatDuration("30");
//            subtask = new Subtask(name, description, epicId, startTime, duration);
//            taskManager.createSubtask(subtask);
//
//            System.out.println(epic.getStartTime());
//            System.out.println(epic.getEndTime());
//            System.out.println(epic.getDuration().toMinutes());
//            System.out.println(epic.getStatus());
//
////            System.out.println("Порядок приоритета задач:\n" + taskManager.getPrioritizedTasks());
//
////            Тест корректности заполнения timeOverlaps
////            Set<LocalDateTime> timeNodes = new TreeSet<>(Comparator.naturalOrder());
////            timeNodes.addAll(InMemoryTaskManager.timeOverlaps.keySet());
////            for (LocalDateTime timeNode : timeNodes) {
////                System.out.println(timeNode.format(InMemoryTaskManager.DATE_TIME_FORMATTER));
////            }
////            System.out.println(timeNodes.size());
////            System.out.println(LocalDateTime.of(LocalDate.now().getYear(),1,1,2,30).toLocalTime());
//
//            // Создал Эпик без Подзадач #7
//            name = "Подготовиться к отпуску";
//            description = "Закрыть дела, задачи перед отпуском, купить билеты, забронировать отель.";
//            epic = new Epic(name, description);
//            taskManager.createEpic(epic);
//
//            taskManager.getTask(1); // первый просмотр
//            printHistory(taskManager); //Печатаю историю просмотров.
//            // Должно быть:
//            //  * Epic 3 - считается просмотр эпика при создании, обновлении его подзадач
//            //  * Task 1
//
//            taskManager.getTask(2); // второй просмотр
//            printHistory(taskManager); // Печатаю историю просмотров.
//            // Должно быть:
//            //  * Epic 3
//            //  * Task 1
//            //  * Task 2
//
//            // Обновляем задачу 1
//            task = taskManager.getTask(1); // 3 просмотр
//            task.setName("Зайти в магазин 4 лапы");
//            task.setDescription("Купить кошке корм Brit Premium и наполнитель.");
//            taskManager.updateTask(task);
//
//            // Изменил статус задачи 1 на DONE и задачи 2 на IN_PROGRESS
//            taskManager.updateTaskStatus(1, Status.DONE);
//            taskManager.updateTaskStatus(2, Status.IN_PROGRESS);
//            printHistory(taskManager); // Проверка удаления старого и записи нового просмотра задачи1. Всего 2 просмотра
//            // Должно быть:
//            //  * Epic 3
//            //  * Task 1
//            //  * Task 2
//
//            //Изменил статус поздадач эпика 3 на DONE
//            taskManager.updateSubtaskStatus(4, Status.DONE);
//            epicId = taskManager.getSubtask(4).getEpicId(); // 4ое обращение к задаче через getSubtask. Всего 3 просмотра
//            //printSubtasksOfEpic(taskManager, epicId);
//            taskManager.getEpic(epicId); // 5ый просмотр (эпик#3), Всего 4 просмотра
//            printHistory(taskManager);
//            // Должно быть:
//            //  * Task 1
//            //  * Task 2
//            //  * Subtask 4
//            //  * Epic 3
//
//            taskManager.updateSubtaskStatus(5, Status.DONE);
//            //printSubtasksOfEpic(taskManager, epicId);
//            taskManager.getSubtask(5); // Вызов подзадачи №5
//            taskManager.getSubtask(4); // Вызов подзадачи №4
//            taskManager.getHistory(); //Печатаю историю просмотров. 6 просмотров
//            // Должно быть:
//            //  * Task 1
//            //  * Task 2
//            //  * Epic 3
//            //  * Subtask 5
//            //  * Subtask 4
//
//            taskManager.getEpic(3); // Вызов эпика №3
//            taskManager.updateSubtaskStatus(6, Status.IN_PROGRESS);
//            printHistory(taskManager);; //Печатаю историю просмотров. 6 просмотров
//            // Должно быть:
//            //  * Task 1
//            //  * Task 2
//            //  * Subtask 5
//            //  * Subtask 4
//            //  * Subtask 6
//            //  * Epic 3
//
//            System.out.println("ЗАДАЧИ ПО ПРИОРИТЕТУ:");
//            for (Task prioritizedTask : taskManager.getPrioritizedTasks()) {
//                System.out.println(prioritizedTask);
//            }
//            System.out.println();
//
//            // lower was commented
//
//            taskManager.getSubtask(6); //Вызов подзадачи 6
//            taskManager.getEpic(7); //Вызов эпика №7
//            taskManager.removeSubtask(5);
//            taskManager.removeTask(2);
//            printHistory(taskManager); //Печатаю историю просмотров. 8 просмотров
//
//            // Должно быть:
//            //  * Task 1
//            //  * Subtask 5
//            //  * Subtask 6
//            //  * Epic 7
//            //  * Epic 3
//
//            taskManager.removeEpic(3); // Удаляем эпик 3 с подзадачами 5 и 6
//            printHistory(taskManager);
//            // Должно быть:
//            //  * Task 1
//            //  * Epic 7
//
////            taskManager.deleteTasks();
////            taskManager.deleteEpics();
////            taskManager.removeEpic(7);
////            printHistory(taskManager);
////            Должно быть:
//        }
//    }

    static void printListTasks(TaskManager taskManager) {
        if (taskManager.getTasks().isEmpty()) {
            System.out.println("Ни одна задача пока не создана.\n");
        } else {
            StringBuilder list = new StringBuilder("Список задач:\n");
            for (Task task : taskManager.getTasks()) {
                list.append("  ").append(task).append('\n');
            }
            System.out.println(list);
        }
    }

    static void printListSubtasks(TaskManager taskManager) {
        if (taskManager.getSubtasks().isEmpty()) {
            System.out.println("Ни одна подзадача пока не создана.\n");
        } else {
            StringBuilder list = new StringBuilder("Список задач:\n");
            for (Task task : taskManager.getSubtasks()) {
                list.append("  ").append(task).append('\n');
            }
            System.out.println(list);
        }
    }

    static void printListEpics(TaskManager taskManager) { // Метод печати списка эпиков
        if (taskManager.getEpics().isEmpty()) {
            System.out.println("Ни один эпик пока не создан.\n");
        } else {
            StringBuilder list = new StringBuilder("Список эпиков:\n");
            for (Epic epic : taskManager.getEpics()) {
                list.append("  ").append(epic).append('\n');
            }
            System.out.println(list);
        }
    }

    static void printSubtasksOfEpic(TaskManager taskManager, int id) { //Метод печати подзадач выбранного эпика
        if (taskManager.getEpic(id) != null) {
            if (!taskManager.getEpic(id).getSubtasksIds().isEmpty()) {
                StringBuilder list = new StringBuilder("Список подзадач эпика numID-" + id + ":\n");
                for (Subtask subtask : taskManager.getSubtasksOfEpic(id)) {
                    list.append("  ").append(subtask).append('\n');
                }
                System.out.println(list);
            } else {
                System.out.println("У эпика numID-" + id + " подзадачи отсутствуют.\n");
            }
        } else {
            System.out.println("Эпик numID-" + id + " не существует.\n");
        }
    }

    static void printHistory(TaskManager taskManager) {
        StringBuilder list = new StringBuilder("История просмотров задач:\n");
        for (Task task : taskManager.getHistory()) {
            list.append("  ").append(task).append('\n');
        }
        System.out.println(list);
    }
}