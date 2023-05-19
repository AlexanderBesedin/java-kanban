package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task { //Класс для создания объектов задач типа Эпик
    private List<Integer> subtasksInEpic; // Список id подзадач, которые относятся к объекту класса Epic
    private LocalDateTime endTime; // Расченое время завершения эпика

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
        subtasksInEpic = new ArrayList<>();
    }

    public void setSubtaskInEpic(Integer num) { // Сеттер для поля subtasksInEpic
        subtasksInEpic.add(num);
    }

    public List<Integer> getSubtasksInEpic() { //Геттер для поля subtasksInEpic
        return subtasksInEpic;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
