package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask;
    private final String name = "subtask1";
    private final String description = "description1";
    private final int epicId = 2;
    private final int id = 1;
    private final Status status = Status.NEW;
    private final LocalDateTime startTime = LocalDateTime.of(2023,5, 11, 19, 13);
    private final Duration duration = Duration.ofMinutes(50);

    @BeforeEach
    public void create() {
        subtask = new Subtask(name, description, epicId, startTime, duration);
        subtask.setId(id);
        subtask.setStatus(status);
    }
    @Test
    void shouldGetEpicId() {
        assertEquals(epicId, subtask.getEpicId());
    }

    @Test
    void shouldBeEqualsSubtasks() {
        Subtask subtask1 = new Subtask(name, description, epicId, startTime, Duration.ofMinutes(50));
        subtask1.setId(id);
        subtask1.setStatus(status);

        assertEquals(subtask1, subtask);
    }

    @Test
    void shouldBeNotEqualsSubtasks() {
        Subtask subtask1 = new Subtask(name, description, 3, startTime, Duration.ofMinutes(50));
        subtask1.setId(id);
        subtask1.setStatus(status);

        assertNotEquals(subtask1, subtask);
    }

    @Test
    void shouldBeEqualsHashCode() {
        Subtask subtask1 = new Subtask(name, description, epicId, startTime, Duration.ofMinutes(50));
        subtask1.setId(id);
        subtask1.setStatus(status);

        assertEquals(subtask1.hashCode(), subtask.hashCode());
    }

    @Test
    void shouldBeNotEqualsHashCode() {
        Subtask subtask1 = new Subtask(name, description, 3, startTime, Duration.ofMinutes(50));
        subtask1.setId(id);
        subtask1.setStatus(status);

        assertNotEquals(subtask1.hashCode(), subtask.hashCode());
    }
}