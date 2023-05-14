package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private final String name = "task1";
    private final String description = "description1";

    @BeforeEach
    public void create() {
        task = new Task(name, description);
    }

    @Test
    void shouldSetAndGetId() {
        task.setId(1);
        assertEquals(1, task.getId());
    }

    @Test
    void shouldGetName() {
        assertEquals(name, task.getName());
    }

    @Test
    void shouldSetName() {
        String name = "task2";
        task.setName(name);
        assertEquals(name, task.getName());
    }

    @Test
    void shouldGetDescription() {
        assertEquals(description, task.getDescription());
    }

    @Test
    void shouldSetDescription() {
        String description = "description2";
        task.setDescription(description);
        assertEquals(description,task.getDescription());
    }

    @Test
    void shouldSetAndGetStatus() {
        Status status = Status.NEW;
        task.setStatus(status);
        assertEquals(status, task.getStatus());
    }

    @Test
    void shouldSetAnGetStartTime() {
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        task.setStartTime(startTime);
        assertEquals(startTime, task.getStartTime());
    }

    @Test
    void shouldSetAnGetDuration() {
        Duration duration = Duration.ofMinutes(50);
        task.setDuration(duration);
        assertEquals(duration, task.getDuration());
    }

    @Test
    void shouldGetNotNullEndTime() {
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        task.setStartTime(startTime);
        Duration duration = Duration.ofMinutes(50);
        task.setDuration(duration);
        assertNotNull(task.getEndTime());
    }
    @Test
    void shouldGetNullEndTimeIfDurationNull() {
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        task.setStartTime(startTime);
        assertNull(task.getEndTime());
    }

    @Test
    void shouldGetNullEndTimeIfStartTimeNull() {
        Duration duration = Duration.ofMinutes(50);
        task.setDuration(duration);
        assertNull(task.getEndTime());
    }

    @Test
    void shouldBeEqualsTasks() {
        int id = 1;
        Status status = Status.NEW;
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        Duration duration = Duration.ofMinutes(50);

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        Task task1 = new Task(name, description, startTime, duration);
        task1.setId(id);
        task1.setStatus(status);

        assertEquals(task1, task);
    }

    @Test
    void shouldBeNotEqualsTasks() {
        int id = 1;
        Status status = Status.NEW;
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        Duration duration = Duration.ofMinutes(50);

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        Task task1 = new Task(name, description, startTime, Duration.ofMinutes(51));
        task1.setId(id);
        task1.setStatus(status);

        assertNotEquals(task1, task);
    }

    @Test
    void shouldBeEqualsHashCode() {
        int id = 1;
        Status status = Status.NEW;
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        Duration duration = Duration.ofMinutes(50);

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        Task task1 = new Task(name, description, startTime, duration);
        task1.setId(id);
        task1.setStatus(status);

        assertEquals(task1.hashCode(), task.hashCode());
    }

    @Test
    void shouldBeNotEqualsHashCode() {
        int id = 1;
        Status status = Status.NEW;
        LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 17, 7);
        Duration duration = Duration.ofMinutes(50);

        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        Task task1 = new Task(name, description, startTime, duration);
        task1.setId(1);
        task1.setStatus(Status.IN_PROGRESS);

        assertNotEquals(task1.hashCode(), task.hashCode());
    }

    @Test
    void testToString() {
        String expected = "TASK{numID-1, name='task1', status='NEW'}, description='description1... '";
        task.setId(1);
        task.setStatus(Status.NEW);
        assertEquals(expected, task.toString());
    }
}