package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {

    private final URI uri;
    private final String apiToken;
    private HttpClient httpClient;

    public KVTaskClient(URI uri) throws IOException, InterruptedException {
        this.uri = uri;
        URI uriForRequest = URI.create(uri.toString() + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uriForRequest)
                .header("Content-type", "application/json;charset=utf-8")
                .build();

        httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(UTF_8);
        HttpResponse<String> response = httpClient.send(request, handler);
        apiToken = response.body();
    }

    public void put(String key, String json) {
        URI uriForPut = URI.create(uri.toString() + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uriForPut)
                .header("Content-type", "application/json;charset=utf-8")
                .build();
        httpClient = HttpClient.newHttpClient();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(UTF_8);
            HttpResponse<String> response = httpClient.send(request, handler);
            if (response.statusCode() != 200) {
                System.out.println("Ошибка при сохранении данных");
            }
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public String load(String key) {
        URI uriForLoad = URI.create(uri.toString() + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uriForLoad)
                .header("Content-type", "application/json;charset=utf-8")
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(UTF_8);
            HttpResponse<String> response = httpClient.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            return "Данные не удалось восстановить";
        }
    }
}
