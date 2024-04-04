package ru.practicum.kanban.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task { //Класс объектов задач типа Эпик
    private List<Integer> subtasksIds; // Список id подзадач, которые относятся к объекту класса Epic
    private LocalDateTime endTime; // Расчетное время завершения эпика

    public Epic(String name, String description) {
        super(name, description);
        this.setSubtasksIds(new ArrayList<>());
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void setSubtaskInEpic(Integer num) { // Сеттер для поля subtasksInEpic
        subtasksIds.add(num);
    }

    public List<Integer> getSubtasksIds() { //Геттер для поля subtasksInEpic
        return subtasksIds;
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
        return Objects.equals(subtasksIds, epic.subtasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName().toUpperCase())
                .append("{id=").append(id)
                .append(", name='").append(name).append('\'')
                .append(", description='").append(description).append('\'')
                .append(", status=").append(status)
                .append(", subtasksIds=").append(subtasksIds)
                .append(", startTime=").append(startTime)
                .append(", endTime=").append(endTime)
                .append(", duration=").append(duration)
                .append('}').toString();
    }
}
