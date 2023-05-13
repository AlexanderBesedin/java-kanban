package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private final String name = "epic1";
    private final String description = "description1";
    private final int id = 1;
    Status status = Status.NEW;

    @BeforeEach
    public void create() {
        epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        epic.setSubtaskInEpic(3);
        epic.setSubtaskInEpic(5);
    }
    @Test
    void shouldGetSubtask3and5fromEpic() {
        List<Integer> subtasks = List.of(3, 5);
        assertTrue(epic.getSubtasksInEpic().containsAll(subtasks));
    }
    @Test
    void shouldBeEqualsEquals() {
        Epic epic1 = new Epic(name, description);
        epic1.setId(id);
        epic1.setStatus(status);
        epic1.setSubtaskInEpic(3);
        epic1.setSubtaskInEpic(5);

        assertEquals(epic1, epic);
    }

    @Test
    void shouldBeNotEqualsEquals() {
        Epic epic1 = new Epic(name, description);
        epic1.setId(id);
        epic1.setStatus(status);
        epic1.setSubtaskInEpic(3);
        epic1.setSubtaskInEpic(6);

        assertNotEquals(epic1, epic);
    }

    @Test
    void shouldBeEqualsHashCode() {
        Epic epic1 = new Epic(name, description);
        epic1.setId(id);
        epic1.setStatus(status);
        epic1.setSubtaskInEpic(3);
        epic1.setSubtaskInEpic(5);

        assertEquals(epic1.hashCode(), epic.hashCode());
    }

    @Test
    void shouldBeNotEqualsHashCode() {
        Epic epic1 = new Epic(name, description);
        epic1.setId(id);
        epic1.setStatus(status);
        epic1.setSubtaskInEpic(3);
        epic1.setSubtaskInEpic(6);

        assertNotEquals(epic1.hashCode(), epic.hashCode());
    }
}