package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task { //Класс для создания объектов задач типа Эпик
    private ArrayList<Integer> subtasksInEpic; // Список id подзадач, которые относятся к объекту класса Epic

    public Epic(String name, String description) {
        super(name, description);
        subtasksInEpic = new ArrayList<>();
    }

    public void setSubtaskInEpic(Integer num) { // Сеттер для поля subtasksInEpic
        subtasksInEpic.add(num);
    }

    public ArrayList<Integer> getSubtasksInEpic() { //Геттер для поля subtasksInEpic
        return subtasksInEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksInEpic, epic.subtasksInEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksInEpic);
    }
}
