package ru.practicum.kanban.service.manager;

import ru.practicum.kanban.exception.OverlapsTimeException;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.utils.Managers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager { // Класс хранения задач всех типов
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public final static int TIME_PERIOD = 15;
    public final static LocalDateTime START_YEAR = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
    public final static LocalDateTime END_YEAR = START_YEAR.plusYears(1);
    protected int id;
    protected final Map<LocalDateTime, Boolean> timeOverlaps;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    private final Comparator<Task> byTimeCompare;

    public InMemoryTaskManager() {
        this.tasks = new LinkedHashMap<>();
        this.epics = new LinkedHashMap<>();
        this.subtasks = new LinkedHashMap<>();
        this.timeOverlaps = new HashMap<>();
        this.historyManager = Managers.gettHistoryManager();
        this.byTimeCompare = Comparator
                .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getId);

        LocalDateTime timeNode = START_YEAR; // задал стартовую дату года планирования
        timeOverlaps.put(timeNode, true);

        while (timeNode.isBefore(END_YEAR)) {
            timeNode = timeNode.plusMinutes(TIME_PERIOD);
            if (timeNode.isEqual(END_YEAR)) continue;
            timeOverlaps.put(timeNode, true); // Время свободно - true, занято - false
        }
    }

    @Override
    public Task getTask(int id) { // Получить задачу по идентификатору
        Task task = tasks.getOrDefault(id, null);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) { // Получить эпик по идентификатору
        Epic epic = epics.getOrDefault(id, null);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) { // Получить подзадачу по идентификатору
        Subtask subtask = subtasks.getOrDefault(id, null);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getTasks() { // Получить список всех задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() { // Получить список всех эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() { // Получить список всех подзадач
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int id) { // Получить список подзадач выбранного эпика
        if (epics.containsKey(id)) {
            List<Integer> numSubtasks = epics.get(id).getSubtasksIds();
            List<Subtask> list = new ArrayList<>();
            for (Integer subtaskId : numSubtasks) { // Проходим по списку id задач выбранного эпика
                list.add(subtasks.get(subtaskId));
            }
            return list;
        } else {
            return new ArrayList<>(); // Возвращаю пустой список, за вывод сообщения отвечает метод printSubtasksOfEpic
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return Stream.of(subtasks.values(), tasks.values())
                .flatMap(Collection::stream)
                .sorted(byTimeCompare)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void createTask(Task task) throws NullPointerException { // Метод создания задачи
        if (task == null) throw new NullPointerException("Переданная задача не существует.\n");
        try {
            checkTimeOverlaps(task);
        } catch (OverlapsTimeException e) {
            System.out.println(e.getMessage());
            return;
        }
        id++;
        task.setId(id);
        task.setStatus(Status.NEW);
        tasks.put(id, task);
        //System.out.println("Создана задача: \n" + task + '\n');
    }

    @Override
    public void createEpic(Epic epic) throws NullPointerException { // Метод создания эпика
        if (epic == null) throw new NullPointerException("Переданный эпик не существует.\n");
        id++;
        epic.setId(id);
        epic.setStatus(Status.NEW);
        if (epic.getSubtasksIds() == null) epic.setSubtasksIds(new ArrayList<>());
        epics.put(id, epic);
//        System.out.println("Создан эпик: \n" + epic + '\n');
    }

    @Override
    public void createSubtask(Subtask subtask) throws NullPointerException { // Метод создания подзадачи
        if (subtask == null) throw new NullPointerException("Переданная подзадача не существует.\n");
        try {
            checkTimeOverlaps(subtask);
        } catch (OverlapsTimeException e) {
            System.out.println(e.getMessage());
            return;
        }
        id++;
        subtask.setId(id); // Присваиваем id
        subtask.setStatus(Status.NEW); //присваиваем статус NEW
        subtasks.put(id, subtask); // Сохраняем в соответствующую map
        Epic epic = epics.get(subtask.getEpicId());
        epic.setSubtaskInEpic(id); // Записываем в список родительского эпика id подзадачи
        updateEpicStatus(epic); // Обновляем статус родительского эпика
        updateEpicDuration(epic); // Рассчитываем/обновляем время выполнения эпика
        //System.out.println("Создана подзадача: \n" + subtask + '\n');
    }

    @Override
    public void updateTask(Task task) throws NullPointerException, IllegalArgumentException { //Метод обновления задачи
        if (task == null) throw new NullPointerException("Переданная задача не существует.\n");
        if (tasks.containsKey(task.getId())) {
            try {
                checkTimeOverlaps(task);
            } catch (OverlapsTimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            tasks.put(task.getId(), task);
            System.out.println("Обновлена задача: \n" + task + "\n" + "Текущий статус: " + task.getStatus() + '\n');
        } else {
            throw new IllegalArgumentException(
                    "Обновляемая задача c ID = " + task.getId() + " в списке задач не обнаружена.\n"
            );
        }
    }

    @Override
    public void updateEpic(Epic epic) throws NullPointerException, IllegalArgumentException { //метод обновления эпика
        if (epic == null) throw new NullPointerException("Переданный эпик не существует.\n");
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtasksIds() == null) epic.setSubtasksIds(new ArrayList<>());
            updateEpicStatus(epic);
            updateEpicDuration(epic); // Рассчитываем/обновляем время выполнения эпика
            epics.put(epic.getId(), epic);
            System.out.println("Обновлен эпик: \n" + epic + "\n" + "Текущий статус: " + epic.getStatus() + '\n');
        } else {
            throw new IllegalArgumentException(
                    "Обновляемый эпик c ID = " + epic.getId() + " в списке эпиков не обнаружен.\n"
            );
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws NullPointerException, IllegalArgumentException { //Метод обновления подзадачи
        if (subtask == null) throw new NullPointerException("Переданная подзадача не существует.\n");
        if (subtasks.containsKey(subtask.getId())) {
            try {
                checkTimeOverlaps(subtask);
            } catch (OverlapsTimeException e) {
                System.out.println(e.getMessage());
                return;
            }
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId()); //Получаем родительский эпик
            updateEpicStatus(epic); // Обновляем статус родительского эпика
            updateEpicDuration(epic); // Рассчитываем/обновляем время выполнения эпика
            System.out.println("Обновлена подзадача: \n" + subtask + "\n" +
                    "Текущий статус: " + subtask.getStatus() + '\n');
        } else {
            throw new IllegalArgumentException(
                    "Обновляемая подзадача c ID = " + subtask.getId() + " в списке подзадач не обнаружена.\n"
            );
        }
    }

    //Метод проверки пересечений времени выполнения задач за O(1)
    protected void checkTimeOverlaps(Task task) throws OverlapsTimeException {
        /*
         * Метод бронирует все ноды от timeNode >= taskStartTime до timeNode < getEndTime.
         * Нода timeNode = getEndTime НЕ БРОНИРУЕТСЯ для предотвращения появления пробелов t = TIME_PERIOD
         * */

        //Если старт или длительность не заданы - прерываем метод
        if (task.getStartTime() == null || task.getDuration() == null) return;

        LocalDateTime taskStartTime = task.getStartTime();
        LocalDateTime taskEndTime = task.getEndTime();
        Duration duration = task.getDuration();

        //Проверяем, не выходит ли заданное время за границы START_YEAR или END_YEAR
        if (taskStartTime.isBefore(START_YEAR) || taskEndTime.isAfter(END_YEAR)) {
            throw new OverlapsTimeException(String.format("Время старта и завершения задачи не должно выходить " +
                            "за пределы года планирования.%nНачало года: %s%nКонец года: %s",
                    START_YEAR.format(DATE_TIME_FORMATTER), END_YEAR.format(DATE_TIME_FORMATTER)));
        }

        /* Случай, где StartTime свободен в timeOverlaps и duration == TIME_PERIOD
         * S1|-15min-|E1, где S и E - старт и конец задачи, 1 - индекс
         *         S2|---------|E2
         * */
        if (timeOverlaps.get(taskStartTime) && duration.toMinutes() == TIME_PERIOD) {
            timeOverlaps.replace(taskStartTime, true, false);
            timeOverlaps.replace(taskEndTime, true, false);
            return;
        }

        /* Случай, где StartTime и EndTime свободны в timeOverlaps, но внутри их duration может быть другая задача
         * S1|--------------------------|E1
         *        S2|---------|E2
         * */
        if (timeOverlaps.get(taskStartTime) && timeOverlaps.get(taskEndTime)) {
            int countPeriod = (int) (duration.toMinutes() / TIME_PERIOD) - 1;
            List<LocalDateTime> freeInnerNodes = new ArrayList<>(countPeriod);
            List<LocalDateTime> busyInnerNodes = new ArrayList<>(countPeriod);
            LocalDateTime innerTimeNode = taskStartTime.plusMinutes(TIME_PERIOD);

            while (innerTimeNode.isBefore(taskEndTime)) {
                if (timeOverlaps.get(innerTimeNode)) { // Если нода свободна(true) - записываем в freeInnerNodes
                    freeInnerNodes.add(innerTimeNode);
                } else { // Если занята - записываем в busyInnerNodes
                    busyInnerNodes.add(innerTimeNode);
                }
                innerTimeNode = innerTimeNode.plusMinutes(TIME_PERIOD);
            }

            if (busyInnerNodes.isEmpty()) { // Если нет ни одной занятой ноды внутри duration задачи
                timeOverlaps.replace(taskStartTime, true, false);
                // бронируем внутренние ноды между StartTime и EndTime
                freeInnerNodes.forEach(timeNode -> timeOverlaps.replace(timeNode, true, false));
            } else {
                throw new OverlapsTimeException("Выбранное время перекрывает время выполнения других задач.");
            }
            return;
        }
        // Случай, когда пересекается с нодами(false) в timeOverlaps либо StartTime, либо EndTime, либо StartTime и EndTime
        if (!timeOverlaps.get(taskStartTime) || !timeOverlaps.get(taskEndTime)) {
            if (!timeOverlaps.get(taskStartTime) && !timeOverlaps.get(taskEndTime)) {
                throw new OverlapsTimeException("Выбранные время старта и завершения заняты другими задачами");
            } else if (!timeOverlaps.get(taskEndTime)) {
                throw new OverlapsTimeException("Выбранное время завершения задачи пересекается с другой задачей.");
            } else if (!timeOverlaps.get(taskStartTime)) {
                System.out.println();
                throw new OverlapsTimeException("Выбранное время старта занято другой задачей.");
            }
        }
    }

    @Override
    public void updateTaskStatus(int id, Status status) {
        try {
            if (status == null) throw new NullPointerException("Не указан новый статус.");
            if (tasks.containsKey(id)) {
                tasks.get(id).setStatus(status);
                historyManager.add(tasks.get(id));
            } else {
                throw new IllegalArgumentException("Задача с ID = " + id + " не существует.");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateSubtaskStatus(int id, Status status) {
        try {
            if (status == null) throw new NullPointerException("Не указан новый статус.");
            if (subtasks.containsKey(id)) {
                subtasks.get(id).setStatus(status);
                int epicId = subtasks.get(id).getEpicId();
                historyManager.add(subtasks.get(id));
                updateEpicStatus(epics.get(epicId)); // Обновил статус родительского эпика
            } else {
                throw new IllegalArgumentException("Подзадача с ID = " + id + " не существует.");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void updateEpicStatus(Epic epic) {
        Optional.ofNullable(epic.getSubtasksIds())
                .ifPresentOrElse(
                        list -> {
                            List<Status> statusSubtasks = list.stream()
                                    .map(subtasks::get)
                                    .map(Task::getStatus)
                                    .collect(Collectors.toList());

                            boolean isNewStatus = !statusSubtasks.contains(Status.IN_PROGRESS)
                                    && !statusSubtasks.contains(Status.DONE);
                            boolean isDoneStatus = !statusSubtasks.contains(Status.NEW)
                                    && !statusSubtasks.contains(Status.IN_PROGRESS);

                            if (isNewStatus) epic.setStatus(Status.NEW);
                            else if (isDoneStatus) epic.setStatus(Status.DONE);
                            else epic.setStatus(Status.IN_PROGRESS);

                            historyManager.add(epic);
                        },
                        () -> {
                            epic.setStatus(Status.NEW);
                            historyManager.add(epic);
                        }
                );
    }

    protected void updateEpicDuration(Epic epic) { // Метод обновления длительности эпика
        //Тк по условию ТЗ время выполнения задач не может пересекаться,
        // то endTime эпика будет endTime последней подзадачи в списке subtaskList

        // Функция обнуления временных параметров эпика, если у него нет подзадач или у его подзадач не указаны временные параметры
        Consumer<Epic> nullableTimeParams = e -> {
            e.setStartTime(null);
            e.setDuration(null);
            e.setEndTime(null);
        };

        Optional.ofNullable(epic.getSubtasksIds())
                .ifPresentOrElse(
                        list -> {
                            List<Subtask> subtaskslist = list.stream()
                                    .map(subtasks::get)
                                    .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                                    .sorted(byTimeCompare)
                                    .collect(Collectors.toList());
                            if (!subtaskslist.isEmpty()) { // Есть ли подзадачи с указанным временем выполнения
                                epic.setStartTime(subtaskslist.get(0).getStartTime()); // Рассчитали начало эпика
                                epic.setEndTime(subtaskslist.get(subtaskslist.size() - 1).getEndTime()); // Рассчитали конец эпика
                                // Определили длительность эпика = суммарную длительность всех подзадач эпика
                                long minutesSubtasks = subtaskslist.stream()
                                        .map(Task::getDuration).mapToLong(Duration::toMinutes).sum();
                                epic.setDuration(Duration.ofMinutes(minutesSubtasks)); // Задали продолжительность эпика
                            } else nullableTimeParams.accept(epic);
                        },
                        () -> { // обнуляем параметры времени у эпика, если у него нет подзадач
                            if (epic.getStartTime() != null) nullableTimeParams.accept(epic);
                        }
                );
    }

    @Override
    public boolean removeTask(int id) { // Удалить задачу по идентификатору
        if (tasks.containsKey(id)) { // Проверяем наличие искомой задачи в map tasks по ключу
            historyManager.remove(id); // Удаляем из истории просмотров
            System.out.printf("Задача %s УДАЛЕНА.%n", tasks.remove(id));
            return true;
        } else {
            System.out.println("Задача numID-" + id + " не существует. Удаление невозможно.\n");
            return false;
        }
    }

    @Override
    public boolean removeEpic(int id) { // Удалить эпик по идентификатору
        if (epics.containsKey(id)) { // Проверяем наличие эпика с введенным идентификатором
            boolean condition = epics.get(id).getSubtasksIds().isEmpty(); //Условие проверки наличия задач в эпике
            if (!condition) {
                List<Integer> subtaskOfEpic = epics.get(id).getSubtasksIds();
                for (Integer subtaskId : subtaskOfEpic) { // Удаляем подзадачи выбранного эпика
                    historyManager.remove(subtaskId); // Удаляем подзадачи выбранного эпика из истории просмотров
                    subtasks.remove(subtaskId);
                }
            }
            historyManager.remove(id); // Удаляем эпик из истории просмотров
            System.out.printf("Эпик %s УДАЛЕН.%n", epics.remove(id));
            return true;
        } else {
            System.out.println("Эпик numID-" + id + " не существует. Удаление невозможно.\n");
            return false;
        }
    }

    @Override
    public boolean removeSubtask(Integer id) { // Удалить подзадачу по идентификатору
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId); // Получаем родительский эпик
            epic.getSubtasksIds().remove(id); // Удаляем из списка родительского эпика id подзадачи
            updateEpicStatus(epic); // Обновляем статус эпика
            updateEpicDuration(epic); // Рассчитываем/обновляем время выполнения эпика
            historyManager.remove(id); // Удаляем подзадачу из истории просмотров
            System.out.printf("Подзадача %s УДАЛЕНА.%n", subtasks.remove(id));
            return true;
        } else {
            System.out.println("Подзадача numID-" + id + " не существует. Удаление невозможно.\n");
            return false;
        }
    }

    @Override
    public void deleteTasks() { // Удалить все задачи
        if (!tasks.isEmpty()) {
            for (Integer id : tasks.keySet()) {
                historyManager.remove(id); // Удаляем из списка просмотров все задачи task
            }
            tasks.clear();
            //System.out.println("Все задачи удалены.\n");
        }
        //System.out.println("Ни одна задача пока не создана.\n");
    }

    @Override
    public void deleteEpics() { // Удалить все эпики с подзадачами
        if (!epics.isEmpty()) {
            for (Integer id : epics.keySet()) {
                historyManager.remove(id); // Удаляем из списка просмотров все эпики epic
            }

            for (Integer id : subtasks.keySet()) {
                historyManager.remove(id); // Удаляем из списка просмотров все подзадачи subtask
            }

            epics.clear();
            subtasks.clear();
            //System.out.println("Все эпики с подзадачами удалены.\n");
        }
        //System.out.println("Ни один эпик пока не создан.\n");
    }

    @Override
    public void deleteSubtasks() { //Удалить все подзадачи
        if (!subtasks.isEmpty()) {
            for (Integer id : subtasks.keySet()) {
                historyManager.remove(id); // Удаляем из списка просмотров все подзадачи subtask
            }
            subtasks.clear();

            for (Integer id : epics.keySet()) { // После удаления всех подзадач возвращаем статус "NEW" каждому эпику
                Epic epic = epics.get(id);
                epic.getSubtasksIds().clear(); // Удалил подзадачи в списке эпика
                updateEpicStatus(epic); //Изменил статус эпика
                updateEpicDuration(epic);  //Рассчитываем/обновляем время выполнения эпика
            }
            //System.out.println("Подзадачи во всех эпиках удалены.\n");
        }
        //System.out.println("Ни одна подзадача пока не создана.\n");
    }
}