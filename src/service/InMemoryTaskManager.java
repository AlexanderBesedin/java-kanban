package service;

import model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InMemoryTaskManager implements TaskManager { // Класс хранения задач всех типов
    private static int id;
    private static LinkedHashMap<Integer, Task> tasks = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Epic> epics = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, Subtask> subtasks = new LinkedHashMap<>();

    @Override
    public Task getTask(int id) { // Получить задачу по идентификатору
        return tasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpic(int id) { // Получить эпик по идентификатору
        return epics.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(int id) { // Получить подзадачу по идентификатору
        return subtasks.getOrDefault(id, null);
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int id) { // Получить список подзадач выбранного эпика
        if (epics.containsKey(id)) {
            ArrayList<Integer> numSubtasks = getEpic(id).getSubtasksInEpic();
            ArrayList<Subtask> list = new ArrayList<>();
            for (Integer subtaskId : numSubtasks) { // Проходим по списку id задач выбранного эпика
                list.add(subtasks.get(subtaskId));
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Task> getListTasks() { // Получить список всех задач
        ArrayList<Task> list = new ArrayList<>(tasks.values());
        return list;
    }

    @Override
    public ArrayList<Epic> getListEpics() { // Получить список всех эпиков
        ArrayList<Epic> list = new ArrayList<>(epics.values());
        return list;
    }

    @Override
    public ArrayList<Subtask> getListSubtasks() { // Получить список всех подзадач
        ArrayList<Subtask> list = new ArrayList<>(subtasks.values());
        return list;
    }

    // методы TaskCreator
    @Override
    public void createTask(Task task) { // Метод создания задачи, эпика, подзадачи
        if (task instanceof Subtask) { // Проверка объекта-аргумента на принаджежность классу Subtask
            id++;
            Subtask subtask = (Subtask) task; // Приводим объект-аргумент к типу Subtask
            subtask.setId(id); // Присваиваем id
            subtask.setStatus(Status.NEW); //присваиваем статус NEW
            subtasks.put(id, subtask); // Сохраняем в соответствующую мапу
            epics.get(subtask.getEpicId()).setSubtaskInEpic(id); // Записываем в список родительского эпика id подзадачи
            updateEpicStatus(epics.get(subtask.getEpicId())); // Обновляем статсус родительского эпика
            System.out.println("Создана подзадача: \n" + subtask + '\n');
        } else if (task instanceof Epic) { // Проверка объекта-аргумента на принаджежность классу Epic
            id++;
            Epic epic = (Epic) task;
            epic.setId(id);
            epic.setStatus(Status.NEW);
            epics.put(id, epic);
            System.out.println("Создан эпик: \n" + epic + '\n');
        } else if (Task.class != task.getClass()) {
            System.out.println("Создайте задачу типа Task, Epic или Subtask.\n");
        } else {
            id++;
            task.setId(id);
            task.setStatus(Status.NEW);
            tasks.put(id, task);
            System.out.println("Создана задача: \n" + task + '\n');
        }
    }

    @Override
    public void updateTask(Task task) { //Метод обновления задачи
        tasks.put(task.getId(), task);
        System.out.println("Обновлена задача: \n" + task + "\n" +
                "Текущий статус: " + task.getStatus() + '\n');
    }

    @Override
    public void updateEpic(Epic epic) { //Метод обновления эпика
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
        System.out.println("Обновлен эпик: \n" + epic + "\n" +
                "Текущий статус: " + epic.getStatus() + '\n');
    }

    @Override
    public void updateSubtask(Subtask subtask) { //Метод обновления подзадачи
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic); // Обновляем статус родительского эпика
        System.out.println("Обновлена подзадача: \n" + subtask + "\n" +
                "Текущий статус: " + subtask.getStatus() + '\n');
    }

    @Override
    public void changeTaskStatus(int id, Status status) {
        tasks.get(id).setStatus(status);
    }

    @Override
    public void changeSubtaskStatus(int id, Status status) {
        subtasks.get(id).setStatus(status);
        int epicId = subtasks.get(id).getEpicId();
        updateEpicStatus(epics.get(epicId)); // Обновил статус родительского эпика
    }

// Перенес сюда updateEpicStatus()
    @Override
    public void updateEpicStatus(Epic epic) { // Метод обновления статуса эпика по статусам включенных подзадач
        if (epic.getSubtasksInEpic().isEmpty()) { // Проверяяем наличие подзадач у эпика
            epic.setStatus(Status.NEW);
            // Если эпик не имеет поздадач(пустой) - метод завершатется на данном месте, как условие блока if
            // Если имеется ввиду иное конкретное улучшение - готов его реализовать при проверке проекта 4 спринта
        } else {
            // Получаем список id подзадач-наследников из поля класса Epic
            ArrayList<Integer> subtaskOfEpic = epic.getSubtasksInEpic();
            ArrayList<Status> statusSubtasks = new ArrayList<>(); //Список статусов подзадач эпика
            for (Integer id : subtaskOfEpic) { // Проход циклом для заполнения списка со статусами подзадач
                statusSubtasks.add(subtasks.get(id).getStatus());
            }
            // Условия для статуса эпика
            boolean isNewStatus = !statusSubtasks.contains(Status.IN_PROGRESS)
                    && !statusSubtasks.contains(Status.DONE);
            boolean isDoneStatus = !statusSubtasks.contains(Status.NEW)
                    && !statusSubtasks.contains(Status.IN_PROGRESS);

            if (isNewStatus) {
                epic.setStatus(Status.NEW);
            } else if (isDoneStatus) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    // методы TaskRemover
    @Override
    public void removeTask(int id) { // Удалить задачу по идентификатору
        if (tasks.containsKey(id)) { // Проверяем наличие искомой задачи в хэшмапе tasks по ключу
            System.out.println("Задача " + tasks.remove(id) + '\n' +
                    "УДАЛЕНА.\n");
        } else {
            System.out.println("Задача numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    @Override
    public void removeEpic(int id) { // Удалить эпик по идентификатору
        if (epics.containsKey(id)) { // Проверяем наличие эпика с введенным идентификатором
            boolean condition = epics.get(id).getSubtasksInEpic().isEmpty(); //Условие проверки наличия задач в эпике
            if (!condition) {
                ArrayList<Integer> subtaskOfEpic = epics.get(id).getSubtasksInEpic();
                for (Integer subtaskId : subtaskOfEpic) { // Удаляем подзадачи выбранного эпика
                    subtasks.remove(subtaskId);
                }
            }
            System.out.println("Эпик " + epics.remove(id) + '\n' +
                    "УДАЛЕН.\n");
        } else {
            System.out.println("Эпик numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    @Override
    public void removeSubtask(Integer id) { // Удалить подзадачу по идентификатору
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId); // Получаем родительский эпик
            epic.getSubtasksInEpic().remove(id); // Удаляем из списка родительского эпика id подзадачи
            updateEpicStatus(epic); // Обновляем статус эпика
            //При выводе sout также удаляется subtask
            System.out.println("Подзадача " + subtasks.remove(id) + '\n' +
                    " УДАЛЕНА.\n");
        } else {
            System.out.println("Подзадача numID-" + id + " не существует. Удаление невозможно.\n");
        }
    }

    @Override
    public void clearTasks() { // Удалить все задачи
        if (tasks.isEmpty()) {
            System.out.println("Ни одна задача пока не создана.\n");
        } else {
            tasks.clear();
            System.out.println("Все задачи удалены.\n");
        }
    }

    @Override
    public void clearEpics() { // Удалить все эпики с подзадачами
        if (epics.isEmpty()) {
            System.out.println("Ни один эпик пока не создан.\n");
        } else {
            epics.clear();
            subtasks.clear();
            System.out.println("Все эпики с подзадачами удалены.\n");
        }
    }

    @Override
    public void clearSubtasks() { //Удалить все подзадачи
        if (subtasks.isEmpty()) {
            System.out.println("Ни одна подзадача пока не создана.\n");
        } else {
            subtasks.clear();
            for (Integer id : epics.keySet()) { // После удаления всех подзадач возвращаем статус "NEW" каждому эпику
                Epic epic = epics.get(id);
                epic.getSubtasksInEpic().clear(); // Удалил подзадачи в списке эпика
                epic.setStatus(Status.NEW);
            }
            System.out.println("Подзадачи во всех эпиках удалены.\n");
        }
    }

    public void printTask(int id) { // Метод вывода задачи любого типа по существующему id
        if (tasks.containsKey(id)) { // Проверка наличия id у мапы tasks
            System.out.println("Задача numID-" + id + ":\n" + getTask(id) + '\n');
        } else if (epics.containsKey(id)) { // Проверка наличия id у мапы epics
            System.out.println("Эпик numID-" + id + ":\n" + getEpic(id) + '\n');
        } else if (subtasks.containsKey(id)) { // Проверка наличия id у мапы subtasks
            System.out.println("Подзадача numID-" + id + ":\n" + getSubtask(id) + '\n');
        } else {
            System.out.println("Указанного numID-" + id + " не существует.\n");
        }
    }

    public void printSubtaskOfEpic(int id) { //Метод печати позадач выбранного эпика
        if (epics.containsKey(id)) {
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
        if (tasks.isEmpty()) {
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
        if (epics.isEmpty()) {
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
        if (subtasks.isEmpty()) {
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