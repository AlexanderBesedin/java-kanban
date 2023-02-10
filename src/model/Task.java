package model;

import java.util.Objects;

public class Task { //Класс для создания объектов задач типа Task
    protected int id; //Идентификатор задачи
    protected String name; // Наименование задачи
    protected String description; // Описание задачи
    protected String status; // Статус задачи

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Переопределенные методы equals, hashcode, toString класса Object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() +
                '{' +
                "numID-" + id +
                ", name='" + name + '\'';

        if (description == null) {
            result += ", description='null'";
        } else {
            result += ", description='" + description.substring(0, 12) + "... '";
        }

        result += ", status='" + status + '\'' + '}';
        return result;
    }
}
