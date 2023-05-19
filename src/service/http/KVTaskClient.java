package service.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final URI serverURL;
    private final HttpClient client;

    private final String apiToken;

    public KVTaskClient(URI serverURL) {
        this.client = HttpClient.newHttpClient();
        this.serverURL = serverURL;
        this.apiToken = getAPIToken();
    }

    public void put(String key, String json) {
        /*URI url = URI.create(serverURL + "/save/" + key + "/?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();*/
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(serverURL + "save/" + key + "?API_TOKEN=" + apiToken))
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

    public String load(String key) {
        //URI url = URI.create(serverURL + "/load/" + key + "/?API_TOKEN=" + apiToken);
       // HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(serverURL + "load/" + key + "?API_TOKEN=" + apiToken))
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
        URI url = URI.create(serverURL + "/register");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
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
