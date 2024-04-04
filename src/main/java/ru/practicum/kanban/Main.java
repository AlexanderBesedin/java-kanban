package ru.practicum.kanban;

import ru.practicum.kanban.http.HttpTaskServer;
import ru.practicum.kanban.http.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer().start();
    }
}