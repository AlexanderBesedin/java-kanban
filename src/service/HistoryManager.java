package service;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id); // вызвать также при удалении задачи
    List<Task> getHistory();
    void printHistory();
}

