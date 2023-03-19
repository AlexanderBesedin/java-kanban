package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> first;
    private Node<Task> last;
    private static Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) { // Метод заполнения истории просмотров, где новый просмотр добавляется в КОНЕЦ списка
        if (task == null) return;
        int id = task.getId();
        remove(id); // Удаляем старый просмотр задачи id, если таковой был
        linkLast(task); // создаем новый узел-просмотр таски
        historyMap.put(id, last); // записываем/перезаписываем новый узел в мапу под соответствующим id
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            Node<Task> node = historyMap.get(id);
            removeNode(node); // удаляем старый узел-просмотр
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void printHistory() {
        System.out.println("История просмотров задач:");
        for (Task task : getTasks()) {
            System.out.println("  " + task);
        }
        System.out.println('\n');
    }

    private void linkLast(Task task) {
        final Node<Task> exLast = last;
        final Node<Task> newNode = new Node<>(task, null, exLast);
        last = newNode;
        if (exLast == null) {
            first = newNode;
        } else {
            exLast.next = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> newHistory = new ArrayList<>();

        for (Node<Task> i = first; i != null; i = i.next) {
            newHistory.add(i.data);
        }
        return newHistory;
    }

    private void removeNode(Node<Task> node) {
        // Сохраняем ссылки удаляемого узла для перепривязки соседних узлов
        Node<Task> beforeNode = node.prev;
        Node<Task> afterNode = node.next;

        if (beforeNode != null && beforeNode.next.equals(node)) { // Проверка условия наличия предыдущего узла
            if (afterNode == null) { // проверяем условие, что node == last
                last = beforeNode;
            }
            beforeNode.next = afterNode; // связываем предыдущий узел со следующим
        }
        if (afterNode != null) { // Проверка условия наличия следующего узла
            if (beforeNode == null) { // проверяем условие, что node == first
                first = afterNode;
            }
            afterNode.prev = node.prev; // связываем следующий узел с предыдущим
        }
        if (node.prev == null && node.next == null) { // удаляем ссылки последнего узла
            first = null;
            last = null;
            historyMap.clear(); // Очищаем всю коллекцию просмотров
        }
        historyMap.remove(node.data.getId()); // Удаляем из коллекции старый просмотр
        node = null; // удаляем ссылку узла-просмотра для его дальнейшего удаления из памяти сборщиком мусора
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

