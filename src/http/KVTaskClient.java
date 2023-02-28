package http;

import java.net.URI;
import java.net.http.HttpClient;

public class KVTaskClient {

    private URI uri;
    private String apiToken;

    private HttpClient httpClient;

    public KVTaskClient(URI uri) {
        this.uri = uri;
        httpClient = HttpClient.newHttpClient();
    }

    public void register() {

    }

}
