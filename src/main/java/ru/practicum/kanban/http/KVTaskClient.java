package ru.practicum.kanban.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Класс-клиент, инкапсулирующий функции KVServer.
// При создании объекта конструктор принимает URL к KVServer, на котором проходит регистрация с выдачей токена API_TOKEN.
// С полученным API_TOKEN отдельный клиент проходит аутентификацию для работы с конкретным хранилищем сервера KVServer.
public class KVTaskClient {
    private final String url;
    private final HttpClient client;
    private final String apiToken;

    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        this.apiToken = getAPIToken();
    }

// Сохраняет состояние менеджера задач через запрос  POST /save/<key>?API_TOKEN=
    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.printf("Ошибка сохранения данных, код ответа {%d}%n", response.statusCode());
                return;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова."
            );
        } catch (IOException | InterruptedException e) {
            System.out.printf("Ошибка сохранения по ключу: {%s}%n", key);
        }
        System.out.printf("Сохранен json по указанному ключу: {%s}%n", key);
    }

//  Возвращает состояние менеджера задач через запрос GET /load/<key>?API_TOKEN=
    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.printf("Ошибка получения данных, код ответа {%d}%n", response.statusCode());
                return null;
            }
            System.out.printf("Получен json по указанному ключу: {%s}%n", key);
            return response.body();
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова."
            );
            return null;
        } catch (IOException | InterruptedException e) {
            System.out.printf("Ошибка загрузки по ключу: {%s}%n", key);
            return null;
        }
    }

    private String getAPIToken() {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url + "register")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.printf("Ошибка загрузки данных, код ответа {%d}%n", response.statusCode());
                return null;
            }
            return response.body();
        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова."
            );
            return null;
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка получения apiToken");
            return null;
        }
    }
}
