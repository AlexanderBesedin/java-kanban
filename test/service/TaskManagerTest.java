package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected String name = "name";
    protected String description = "some description";
    protected TimeDurationUtil timeFormat = new TimeDurationUtil(
            InMemoryTaskManager.TIME_PERIOD,
            InMemoryTaskManager.DATE_TIME_FORMATTER);

    protected Task makeTask(String name, String description) {
        Task task = new Task(name, description);
        taskManager.createTask(task);
        return task;
    }

    protected Epic makeEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        return epic;
    }

    protected Subtask makeSubtask(String name, String description) {
        Epic epic = makeEpic(name, description); //#1
        Subtask subtask = new Subtask(name, description, epic.getId());
        taskManager.createSubtask(subtask); //#2
        return subtask;
    }

    @Test
    void shouldCreateTask() { //Default case
        makeTask(name, description);
        Task task = makeTask(name, description);
        assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    void shouldCreateTaskWhenEmptyMap() { //Empty case
        Task task = makeTask(name, description);
        assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    void shouldCreateEpic() { //Default case
        makeEpic(name, description);
        Epic epic = makeEpic(name, description);
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    void shouldCreateEpicWhenEmptyMap() { //Empty case
        Epic epic = makeEpic(name, description);
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    void shouldCreateSubtask() { //Default case
        makeSubtask(name, description);
        Subtask subtask = makeSubtask(name, description);
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }
    @Test
    void shouldCreateSubtaskWhenEmptyMap() { //Empty case
        Subtask subtask = makeSubtask(name, description);
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldThrowsIfCreateTaskNull() { //Null case Task
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> {
                    taskManager.createTask(null);
                }
        );
        assertEquals("Переданная задача не существует.\n", exception.getMessage());
    }

    @Test
    void shouldThrowsIfCreateEpicNull() { //Null case Epic
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> {
                    taskManager.createEpic(null);
                }
        );
        assertEquals("Переданный эпик не существует.\n", exception.getMessage());
    }

    @Test
    void shouldThrowsIfCreateSubtaskNull() { //Null case Subtask
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> {
                    taskManager.createSubtask(null);
                }
        );
        assertEquals("Переданная подзадача не существует.\n", exception.getMessage());
    }

    @Test
    void shouldUpdateTask() { //Default case
        Task task = makeTask(name, description);
        task.setDescription("description1");
        taskManager.updateTask(task);
        assertEquals("TASK{numID-1, name='name', status='NEW'}, description='description1... '",
                task.toString());
    }

    @Test
    void shouldThrowIfUpdateTaskWhenMapEmpty() { //Empty case
        Task task = new Task(name, description);
        task.setId(1);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateTask(task)
        );
        assertEquals(
                "Обновляемая задача c ID = " + task.getId() + " в списке задач не обнаружена.\n",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowIfUpdateTaskWithoutID() { //"0" case
        Task task = makeTask(name, description);
        task.setDescription("description1");
        task.setId(0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateTask(task)
        );
        assertEquals(
                "Обновляемая задача c ID = " + task.getId() + " в списке задач не обнаружена.\n",
                exception.getMessage()
        );
    }

    @Test
    void shouldUpdateEpic() { //Default case
        Epic epic = makeEpic(name, description);
        epic.setDescription("description1");
        taskManager.updateEpic(epic);
        assertEquals("EPIC{numID-1, name='name', status='NEW'}, description='description1... '",
                epic.toString());
    }

    @Test
    void shouldThrowIfUpdateEpicWhenMapEmpty() { //Empty case
        Epic epic = new Epic(name, description);
        epic.setId(1);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateEpic(epic)
        );
        assertEquals(
                "Обновляемый эпик c ID = " + epic.getId() + " в списке эпиков не обнаружен.\n",
                exception.getMessage()
        );
    }
    @Test
    void shouldThrowIfUpdateEpicWithoutID() { //"0" case
        Epic epic = new Epic(name, description);
        epic.setDescription("description1");
        epic.setId(0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateEpic(epic)
        );
        assertEquals(
                "Обновляемый эпик c ID = " + epic.getId() + " в списке эпиков не обнаружен.\n",
                exception.getMessage()
        );
    }

    @Test
    void shouldUpdateSubtask() { //Default case
        Subtask subtask = makeSubtask(name, description);
        subtask.setDescription("description1");
        taskManager.updateSubtask(subtask);
        assertEquals("SUBTASK{numID-2, name='name', status='NEW'}, description='description1... '",
                subtask.toString());
    }

    @Test
    void shouldThrowIfUpdateSubtaskWhenMapEmpty() { //Empty case
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        subtask.setId(4);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateSubtask(subtask)
        );
        assertEquals(
                "Обновляемая подзадача c ID = " + subtask.getId() + " в списке подзадач не обнаружена.\n",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowIfUpdateSubtaskWithoutID() { //"0" case
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        subtask.setId(0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.updateSubtask(subtask)
        );
        assertEquals(
                "Обновляемая подзадача c ID = " + subtask.getId() + " в списке подзадач не обнаружена.\n",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowsWhenUpdateTaskNull() { //Null case
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> {
                    taskManager.updateTask(null);
                }
        );
        assertEquals("Переданная задача не существует.\n", exception.getMessage());
    }

    @Test
    void shouldThrowsIfUpdateEpicNull() { //Null case Epic
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> {
                    taskManager.updateEpic(null);
                }
        );
        assertEquals("Переданный эпик не существует.\n", exception.getMessage());
    }

    @Test
    void shouldThrowsIfUpdateSubtaskNull() { //Null case Subtask
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> {
                    taskManager.updateSubtask(null);
                }
        );
        assertEquals("Переданная подзадача не существует.\n", exception.getMessage());
    }

    @Test
    void shouldGetTask() { //Default case
        Task task = makeTask(name,description);
        assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    void shouldGetNullIfTaskMapEmpty() { //Empty case
        Task task = new Task(name,description);
        task.setId(1);
        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    void shouldGetNullIfTaskNotExist() { //Incorrect argument case
        makeTask(name,description); //#1
        assertNull(taskManager.getTask(10));
    }

    @Test
    void shouldGetEpic() { //Default case
        Epic epic = makeEpic(name, description);
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    void shouldGetNullIfEpicMapEmpty() { //Empty case
        Epic epic = new Epic(name,description);
        epic.setId(1);
        assertNull(taskManager.getEpic(epic.getId()));
    }

    @Test
    void shouldGetNullIfEpicNotExist() { //Incorrect argument case
        makeEpic(name,description); //#1
        assertNull(taskManager.getEpic(2));
    }

    @Test
    void shouldGetSubtask() { //Default case
        Subtask subtask = makeSubtask(name, description);
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldGetNullIfSubtaskMapEmpty() { //Empty case
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        subtask.setId(2);
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldGetNullIfSubtaskNotExist() {  //Incorrect argument case
        makeSubtask(name,description); //Epic#1, Subtask#2
        assertNull(taskManager.getSubtask(3));
    }

    @Test
    void shouldGetListSubtasksOfChosenExistEpic() {
        makeSubtask(name, description); //Epic#1, Subtask#2
        Subtask subtask = new Subtask("name", "other description", taskManager.getEpic(1).getId());
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = List.of(taskManager.getSubtask(2), taskManager.getSubtask(3));
        assertTrue(taskManager.getSubtasksOfEpic(1).containsAll(subtasks));
    }

    @Test
    void shouldGetEmptyListSubtasksIfChosenEpicNotExist() {
        assertTrue(taskManager.getSubtasksOfEpic(1).isEmpty());
    }

    @Test
    void shouldGetListTasks() {
        makeTask(name,description); //Task#1
        makeTask(name,description); //Task#2
        List<Task> tasks = List.of(taskManager.getTask(1), taskManager.getTask(2));
        assertTrue(taskManager.getListTasks().containsAll(tasks));
    }

    @Test
    void shouldGetEmptyListIfTasksNoCreated() {
        assertTrue(taskManager.getListTasks().isEmpty());
    }

    @Test
    void shouldGetListEpics() {
        makeEpic(name,description); //Epic#1
        makeEpic(name,description); //Epic#2
        List<Epic> epics = List.of(taskManager.getEpic(1), taskManager.getEpic(2));
        assertTrue(taskManager.getListEpics().containsAll(epics));
    }

    @Test
    void shouldGetEmptyListIfEpicsNoCreated() {
        assertTrue(taskManager.getListEpics().isEmpty());
    }

    @Test
    void shouldGetListSubtasks() {
        makeSubtask(name, description); //Epic#1, Subtask#2
        makeSubtask(name, description); //Epic#3, Subtask#4
        List<Subtask> subtasks = List.of(taskManager.getSubtask(2), taskManager.getSubtask(4));
        assertTrue(taskManager.getListSubtasks().containsAll(subtasks));
    }

    @Test
    void shouldGetEmptyListIfSubtasksNoCreated() {
        assertTrue(taskManager.getListSubtasks().isEmpty());
    }

    @Test
    void shouldGetHistoryOfViewsByRequest() {
        makeTask(name, description);//Task#1
        makeEpic(name, description);//Epic#2
        makeSubtask(name, description);//Epic#3, Subtask#4
        List<Task> views = List.of(
                taskManager.getEpic(3),
                taskManager.getTask(1),
                taskManager.getSubtask(4),
                taskManager.getEpic(2)
        );
        assertEquals(taskManager.getHistory().toString(),views.toString());
    }

    @Test
    void shouldGetEmptyHistoryOfViewsWithoutRequest() {
        makeTask(name, description);//Task#1
        makeEpic(name, description);//Epic#2
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldGetEmptyHistoryOfViewsAfterClear() {
        makeTask(name, description);//Task#1
        makeEpic(name, description);//Epic#2
        taskManager.getEpic(2);
        taskManager.getTask(1);
        taskManager.clearTasks();
        taskManager.clearEpics();

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldGetListPrioritizedTasksByTime() { // Default case
        Epic epic = new Epic(name, description);//id#1
        taskManager.createEpic(epic);
        Subtask task1 = new Subtask(//id#2
                name, description, 1,
                timeFormat.getFormatStartTime("12.05.2023 09:37"),
                timeFormat.getFormatDuration("55")
        );
        taskManager.createSubtask(task1);
        Task task2 = new Task(//id#3
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 10:30"),
                timeFormat.getFormatDuration("90")
        );
        taskManager.createTask(task2);
        Task task3 = new Task(//id#4
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:08"),
                timeFormat.getFormatDuration("30")
        );
        taskManager.createTask(task3);

        List<Task> list = List.of(
                task1,
                task2,
                task3,
                makeTask(name, description) // StartTime = null
        );

        assertEquals(list.toString(), taskManager.getPrioritizedTasks().toString());
    }

    @Test
    void shouldGetListPrioritizedTasksWithoutStartTime() { //Null-time case
        List<Task> list = List.of(
                makeTask(name, description),
                makeTask(name, description),
                makeTask(name, description)
        );
        assertEquals(list.toString(), taskManager.getPrioritizedTasks().toString());
    }

    @Test
    void shouldGetEmptyListPrioritizedTasksIfStorageMapsEmpty() { //Empty case
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldRoundAndGetStartTimeByFormat() { //Default case1
        String time = "12.05.2023 12:22";
        String expectedTime = "12.05.2023 12:15";
        assertEquals(expectedTime,
                timeFormat.getFormatStartTime(time).format(InMemoryTaskManager.DATE_TIME_FORMATTER));
    }

    @Test
    void shouldRoundAndGetStartTimeIsMinutesUnder15() { //Default case2
        String time = "12.05.2023 12:05";
        String expectedTime = "12.05.2023 12:15";
        assertEquals(expectedTime,
                timeFormat.getFormatStartTime(time).format(InMemoryTaskManager.DATE_TIME_FORMATTER));
    }

    @Test
    void shouldGetNullWhenStartTimeIncorrectFormat() { //Incorrect argument case
        String time = "12/05/2023 12:22";
        assertNull(timeFormat.getFormatStartTime(time));
    }

    @Test
    void shouldGetNullWhenStartTimeIsNull() { //Null case
        assertNull(timeFormat.getFormatStartTime(null));
    }

    @Test
    void shouldRoundAndGetDurationByFormat() {  //Default case1
        String duration = "38";
        String expectedDuration = "45";
        assertEquals(expectedDuration,
                String.valueOf(timeFormat.getFormatDuration(duration).toMinutes()));
    }

    @Test
    void shouldRoundAndGetDurationUnder15() {  //Default case2
        String duration = "3";
        String expectedDuration = "15";
        assertEquals(expectedDuration,
                String.valueOf(timeFormat.getFormatDuration(duration).toMinutes()));
    }

    @Test
    void shouldGetNullWhenDurationIsNegative() { //Incorrect argument case1
        String duration = "-38";
        assertNull(timeFormat.getFormatDuration(duration));
    }

    @Test
    void shouldGetNullWhenDurationIsNotNumber() { //Incorrect argument case2
        String duration = "-38asd";
        assertNull(timeFormat.getFormatDuration(duration));
    }

    @Test
    void shouldGetNullWhenDurationIsNull() { //Null case
        assertNull(timeFormat.getFormatDuration(null));
    }

    //Проверка метода checkTimeOverlaps через createTask
    @Test
    void shouldCreateTasksIfTimeNotOverlaps() { //Задачи не пересекаются, все задачи могут быть созданы
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:00"), timeFormat.getFormatDuration("60")
        );
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 14:00"), timeFormat.getFormatDuration("30")
        );

        taskManager.createTask(task1);// id#1
        taskManager.createTask(task2);// id#2
        assertEquals(task2, taskManager.getTask(task2.getId()));
    }

    @Test  //Новая задача создается, если ее StartTime равен EndTime другой задачи
    void shouldCreateTaskIfStartTimeEqualPrevEndTime() {
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:00"), timeFormat.getFormatDuration("60")
        );
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 13:00"), timeFormat.getFormatDuration("30")
        );

        taskManager.createTask(task1);// id#1
        taskManager.createTask(task2);// id#2
        assertNotNull(taskManager.getTask(task2.getId()));
    }

    @Test //Между свободными StartTime и EndTime новой задачи есть другая задача
    void shouldDoNotCreateTaskIfTimeOverlapsOtherTask() {
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:00"), timeFormat.getFormatDuration("60")
        );
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 11:30"), timeFormat.getFormatDuration("180")
        );

        taskManager.createTask(task1);// id#1
        taskManager.createTask(task2);// id#2
        assertNull(taskManager.getTask(task2.getId()));
    }

    @Test
    void shouldDoNotCreateTaskIfTimeBooked() { //Выбранные StartTime и EndTime новой задачи уже заняты другой задачей
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 11:30"), timeFormat.getFormatDuration("180")
        );
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:00"), timeFormat.getFormatDuration("60")
        );

        taskManager.createTask(task1);// id#1
        taskManager.createTask(task2);// id#2
        assertNull(taskManager.getTask(task2.getId()));
    }

    @Test
    void shouldDoNotCreateTaskIfStartTimeBooked() { //Выбранное StartTime новой задачи уже занято другой задачей
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 11:30"), timeFormat.getFormatDuration("90")
        );
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 12:45"), timeFormat.getFormatDuration("60")
        );

        taskManager.createTask(task1);// id#1
        taskManager.createTask(task2);// id#2
        assertNull(taskManager.getTask(task2.getId()));
    }

    @Test
    void shouldDoNotCreateTaskIfEndTimeBooked() { //Выбранное EndTime новой задачи уже заняты другой задачей
        Task task1 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 10:30"), timeFormat.getFormatDuration("180")
        );
        Task task2 = new Task(
                name, description,
                timeFormat.getFormatStartTime("12.05.2023 09:00"), timeFormat.getFormatDuration("90")
        );

        taskManager.createTask(task1);// id#1
        taskManager.createTask(task2);// id#2
        assertNull(taskManager.getTask(task2.getId()));
    }

    @Test
    void shouldCreateTaskIfStartTimeEqualStartYear() { //Выбранное StartTime новой задачи равно START_YEAR
        Task task = new Task(
                name, description, InMemoryTaskManager.START_YEAR, timeFormat.getFormatDuration("180")
        );
        taskManager.createTask(task);// id#1
        assertNotNull(taskManager.getTask(task.getId()));
    }
    @Test
    void shouldDoNotCreateTaskIfStartTimeBeforeStartYear() { //Выбранное StartTime новой задачи ранее границы START_YEAR
        Task task = new Task(
                name, description,
                InMemoryTaskManager.START_YEAR.minusMinutes(15), timeFormat.getFormatDuration("180")
        );
        taskManager.createTask(task);// id#1
        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    void shouldDoNotCreateTaskIfStartTimeEqualEndYear() { //Выбранное StartTime новой задачи равно границе END_YEAR
        Task task = new Task(
                name, description,
                InMemoryTaskManager.END_YEAR, timeFormat.getFormatDuration("180")
        );
        taskManager.createTask(task);// id#1
        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    void shouldDoNotCreateTaskIfStartTimeAfterEndYear() { //Выбранное StartTime новой задачи позднее границы END_YEAR
        Task task = new Task(
                name, description,
                InMemoryTaskManager.END_YEAR.plusMinutes(15), timeFormat.getFormatDuration("180")
        );
        taskManager.createTask(task);// id#1
        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    void shouldCreateTaskIfEndTimeEqualEndYear() { //Выбранное EndTime новой задачи равно границе END_YEAR
        Task task = new Task(
                name, description,
                InMemoryTaskManager.END_YEAR.minusMinutes(15), timeFormat.getFormatDuration("15")
        );
        taskManager.createTask(task);// id#1
        assertNotNull(taskManager.getTask(task.getId()));
    }

    @Test
    void shouldDoNotCreateTaskIfEndTimeAfterEndYear() { //Выбранное EndTime новой задачи позднее границы END_YEAR
        Task task = new Task(
                name, description,
                InMemoryTaskManager.END_YEAR.minusMinutes(15), timeFormat.getFormatDuration("30")
        );
        taskManager.createTask(task);// id#1
        assertNull(taskManager.getTask(task.getId()));
    }

    //Проверка расчета времени эпика по его подзадачам
    @Test
    void shouldBeEpicTimeIsNull() { // StartTime и EndTime эпика должен быть равен null
        makeSubtask(name, description); //Epic#1
        new Subtask(name, description, 1);
        assertNull(taskManager.getEpic(1).getStartTime());
    }

    @Test // StartTime эпика должен быть равен StartTime самой ранней подзадачи
    void shouldBeEpicStartTimeEqualStartTimeEarlySubtask() {
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(
                name, description, epic.getId(),
                timeFormat.getFormatStartTime("12.05.2023 09:00"), timeFormat.getFormatDuration("90")
                );
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(
            new Subtask(
                    name, description, epic.getId(),
                    timeFormat.getFormatStartTime("12.05.2023 11:00"), timeFormat.getFormatDuration("90")
            )
        );
        assertEquals(subtask.getStartTime(), epic.getStartTime());
    }

    @Test // Endtime эпика должен быть равен EndTime самой поздней подзадачи
    void shouldBeEpicEndTimeEqualEndTimeLateSubtask() {
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(
                name, description, epic.getId(),
                timeFormat.getFormatStartTime("12.05.2023 11:00"), timeFormat.getFormatDuration("90")
        );
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(
            new Subtask(
                name, description, epic.getId(),
                    timeFormat.getFormatStartTime("12.05.2023 09:00"), timeFormat.getFormatDuration("90")
            )
        );
        assertEquals(subtask.getEndTime(), epic.getEndTime());
    }

    @Test // StartTime эпика должен быть равен StartTime самой ранней подзадачи
    void shouldBeEpicDurationEqualSumDurationSubtasks() {
        Epic epic = makeEpic(name, description);
        Duration duration1 = Duration.ofMinutes(90);
        Duration duration2 = Duration.ofMinutes(30);
        Duration expected = duration1.plus(duration2);
        taskManager.createSubtask(new Subtask(
                name, description, epic.getId(),
                timeFormat.getFormatStartTime("12.05.2023 09:00"), duration1
        ));
        taskManager.createSubtask(new Subtask(
                name, description, epic.getId(),
                timeFormat.getFormatStartTime("12.05.2023 11:00"), duration2
        ));
        assertEquals(expected, epic.getDuration());
    }

    @Test
    void shouldChangeTaskStatus() { //Default case
        Task task = makeTask(name, description);
        Status status = Status.IN_PROGRESS;
        taskManager.changeTaskStatus(task.getId(), status);
        assertEquals(status, taskManager.getTask(task.getId()).getStatus());
    }
    @Test
    void shouldNoChangeTaskStatusIfIncorrectId() { //Incorrect argument ID case
        Task task = makeTask(name, description);
        Status expected = Status.IN_PROGRESS;
        taskManager.changeTaskStatus(2, expected);
        assertNotEquals(expected, task.getStatus());
    }

    @Test
    void shouldNoChangeTaskStatusWhoIsNull() { //Null argument Status case
        Task task = makeTask(name, description);
        taskManager.changeTaskStatus(task.getId(), null);
        assertNotNull(task.getStatus());
    }

    @Test
    void shouldChangeSubtaskStatus() { //Default case
        Subtask subtask = makeSubtask(name, description);
        Status status = Status.IN_PROGRESS;
        taskManager.changeSubtaskStatus(subtask.getId(), status);
        assertEquals(status, taskManager.getSubtask(subtask.getId()).getStatus());
    }
    @Test
    void shouldNoChangeSubtaskStatusIfIncorrectId() {  //Incorrect argument ID case
        Subtask subtask = makeSubtask(name, description);
        Status status = Status.IN_PROGRESS;
        taskManager.changeSubtaskStatus(3, status);
        assertNotEquals(status, taskManager.getSubtask(subtask.getId()).getStatus());
    }

    @Test
    void shouldNoChangeSubtaskStatusWhoIsNull() { //Null argument Status case
        Subtask subtask = makeSubtask(name, description);
        taskManager.changeSubtaskStatus(subtask.getId(), null);
        assertNotNull(subtask.getStatus());
    }

    //Тесты на изменение статуса ЭПИКА
    @Test
    void shouldBeEpicStatusNewIfNoSubtasks() { //Пустой список подзадач
        assertEquals(Status.NEW, makeEpic(name, description).getStatus());
    }

    @Test
    void shouldBeEpicStatusNewIfSubtasksAreNew() { //Все подзадачи со статусом NEW
        makeSubtask(name, description); //Epic#1, Subtask#2
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#3
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#4
        assertEquals(Status.NEW, taskManager.getEpic(1).getStatus());
    }

    @Test
    void shouldBeEpicStatusDoneIfSubtasksAreDone() { //Все подзадачи со статусом DONE
        makeSubtask(name, description); //Epic#1, Subtask#2
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#3
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#4
        for (Subtask subtask : taskManager.getListSubtasks()) {
            taskManager.changeSubtaskStatus(subtask.getId(), Status.DONE);
        }
        assertEquals(Status.DONE, taskManager.getEpic(1).getStatus());
    }

    @Test
    void shouldBeEpicStatusInProgressIfSubtasksAreDoneAndNew() { //Подзадачи со статусом DONE и NEW
        Subtask subtask = makeSubtask(name, description); //Epic#1, Subtask#2
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#3
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#4
        for (Subtask sub : taskManager.getListSubtasks()) {
            taskManager.changeSubtaskStatus(sub.getId(), Status.DONE);
        }
        taskManager.changeSubtaskStatus(subtask.getId(), Status.NEW);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    void shouldBeEpicStatusInProgressIfSubtasksAreInProgress() { //Все подзадачи со статусом IN_PROGRESS
        makeSubtask(name, description); //Epic#1, Subtask#2
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#3
        taskManager.createSubtask(new Subtask(name, description, 1)); //Subtask#4
        for (Subtask subtask : taskManager.getListSubtasks()) {
            taskManager.changeSubtaskStatus(subtask.getId(), Status.IN_PROGRESS);
        }
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    void shouldRemoveTask() {
        Task task = makeTask(name,description);
        int id = task.getId();
        taskManager.removeTask(id);
        assertNull(taskManager.getTask(id));
    }

    @Test
    void shouldDoNotRemoveTask() {
        Task task = new Task(name, description); //Создаем, но не добавляем в мапу задачу
        int id = task.getId();
        taskManager.removeTask(id);
        assertNull(taskManager.getTask(id));
    }

    @Test
    void shouldRemoveEpic() {
        Epic epic = makeEpic(name, description);
        int id = epic.getId();
        taskManager.removeEpic(id);
        assertNull(taskManager.getEpic(id));
    }

    @Test
    void shouldDoNotRemoveEpic() {
        Epic epic = new Epic(name, description);
        int id = epic.getId();
        taskManager.removeEpic(id);
        assertNull(taskManager.getEpic(id));
    }

    @Test
    void shouldRemoveSubtask() {
        Subtask subtask = makeSubtask(name, description);
        int id = subtask.getId();
        taskManager.removeSubtask(id);
        assertNull(taskManager.getEpic(id));
    }

    @Test
    void shouldDoNotRemoveSubtask() {
        Epic epic = makeEpic(name, description);
        Subtask subtask = new Subtask(name, description, epic.getId());
        int id = subtask.getId();
        taskManager.removeSubtask(id);
        assertNull(taskManager.getEpic(id));
    }

    @Test
    void shouldClearTasks() {
        makeTask(name, description);
        makeTask(name, description);
        makeTask(name, description);
        taskManager.clearTasks();
        assertTrue(taskManager.getListTasks().isEmpty());
    }

    @Test
    void shouldDoNotClearTasks() {
        new Task(name, description);
        new Task(name, description);
        new Task(name, description);
        taskManager.clearTasks();
        assertTrue(taskManager.getListTasks().isEmpty());
    }

    @Test
    void shouldClearEpics() {
        Epic epic = makeEpic(name, description);
        taskManager.createSubtask(new Subtask(name, description, epic.getId()));
        taskManager.createSubtask(new Subtask(name, description, epic.getId()));

        makeEpic(name, description);
        makeEpic(name, description);

        taskManager.clearEpics();
        assertTrue(taskManager.getListEpics().isEmpty() && taskManager.getListSubtasks().isEmpty());
    }

    @Test
    void shouldDoNotClearEpics() {
        new Epic(name, description);
        new Epic(name, description);
        new Epic(name, description);

        taskManager.clearEpics();
        assertTrue(taskManager.getListEpics().isEmpty());
    }

    @Test
    void shouldClearSubtasks() {
        Epic epic = makeEpic(name, description);
        taskManager.createSubtask(new Subtask(name, description, epic.getId()));
        taskManager.createSubtask(new Subtask(name, description, epic.getId()));

        taskManager.clearSubtasks();
        assertTrue(taskManager.getListSubtasks().isEmpty() && !taskManager.getListEpics().isEmpty());
    }

    @Test
    void shouldDoNotClearSubtasks() {
        Epic epic = makeEpic(name, description);
        new Subtask(name, description, epic.getId());
        new Subtask(name, description, epic.getId());

        taskManager.clearSubtasks();
        assertTrue(taskManager.getListSubtasks().isEmpty() && !taskManager.getListEpics().isEmpty());
    }
}