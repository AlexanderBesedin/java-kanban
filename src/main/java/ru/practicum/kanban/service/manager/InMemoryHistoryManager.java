package ru.practicum.kanban.service.manager;

import ru.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> first;
    private Node<Task> last;
    private final Map<Integer, Node<Task>> historyViews;

    public InMemoryHistoryManager() {
        this.historyViews = new HashMap<>();
    }

    @Override
    public void add(Task task) { // Метод заполнения истории просмотров, где новый просмотр добавляется в КОНЕЦ списка
        if (task == null) return;
        int id = task.getId();
        remove(id); // Удаляем старый просмотр задачи id, если таковой был
        linkLast(task); // создаем новый узел-просмотр таски
        historyViews.put(id, last); // записываем/перезаписываем новый узел в мапу под соответствующим id
    }

    @Override
    public void remove(int id) {
        if (historyViews.containsKey(id)) {
            Node<Task> node = historyViews.get(id);
            removeNode(node); // удаляем старый узел-просмотр
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> newHistory = new ArrayList<>();

        for (Node<Task> i = first; i != null; i = i.next) {
            newHistory.add(i.data);
        }
        return newHistory;
    }

    private void linkLast(Task task) {
        final Node<Task> exLast = last;
        last = new Node<>(task, null, exLast);

        if (exLast == null) {
            first = last;
        } else {
            exLast.next = last;
        }
    }

    private void removeNode(Node<Task> node) {
        // Сохраняем ссылки удаляемого узла для перепривязки соседних узлов
        Node<Task> beforeNode = node.prev;
        Node<Task> afterNode = node.next;

        if (beforeNode != null) { // Проверка условия наличия предыдущего узла
            beforeNode.next = afterNode; // связываем предыдущий узел со следующим
        } else { // проверяем условие, что node == first
            first = afterNode;
        }

        if (afterNode != null) { // Проверка условия наличия следующего узла
            afterNode.prev = node.prev; // связываем следующий узел с предыдущим
        } else { // проверяем условие, что node == last
            last = beforeNode;
        }

        historyViews.remove(node.data.getId()); // Удаляем из коллекции старый просмотр
    }

    private static class Node<Task> {
        Task data;
        Node<Task> next;
        Node<Task> prev;

        Node(Task data, Node<Task> next, Node<Task> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}

