package model;

import service.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task { //Класс для создания объектов задач типа Task
    protected int id; //Идентификатор задачи
    protected String name; // Наименование задачи
    protected String description; // Описание задачи
    protected Status status; // Статус задачи
    protected LocalDateTime startTime; // Время начала выполнения задачи
    protected Duration duration; // Длительность выполнения задачи


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    // Геттеры и сеттеры
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    // Переопределенные методы equals, hashcode, toString класса Object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName().toUpperCase() +
                '{' +
                "numID-" + id +
                ", name='" + name + '\'';

        result += ", status='" + status + '\'' + '}';

        if (description == null) {
            result += ", description='null'";
        } else {
            result += ", description='" + description.substring(0, 12) + "... '";
        }
        return result;
    }
}