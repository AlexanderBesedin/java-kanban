package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) { // Метод заполнения истории просмотров, где новый просмотр добавляется в КОНЕЦ списка
        if (history.size() > 10) { // проверяем список на заполненность до 10 значений
            history.remove(0);  // удаляем самый старый просмотр и добавляем новый в начало списка
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void printHistory() {
        List<Task> history = getHistory();
        System.out.println("История просмотров задач:");
        for (Task task : history) {
            System.out.println("  " + task);
        }
        System.out.println('\n');
    }
}
