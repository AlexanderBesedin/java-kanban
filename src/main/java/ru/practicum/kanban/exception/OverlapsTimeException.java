package ru.practicum.kanban.exception;

public class OverlapsTimeException extends RuntimeException {
    public OverlapsTimeException(String message) {
        super(message);
    }
}
