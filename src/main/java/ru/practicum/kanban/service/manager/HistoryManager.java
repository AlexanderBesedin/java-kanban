package ru.practicum.kanban.service.manager;

import ru.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id); // вызвать также при удалении задачи
    List<Task> getHistory();
}

