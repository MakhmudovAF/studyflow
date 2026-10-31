package studyflow.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class StudyFlowClient {
    private final HttpClient client;
    private final String baseUrl;

    public StudyFlowClient(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
    }

    public HttpResponse<String> getAllTasks() throws IOException, InterruptedException {
        return getTasks(Map.of());
    }

    public HttpResponse<String> getTaskById(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/" + id))
                .GET()
                .build();

        return send(request);
    }

    public HttpResponse<String> createTask(String title, String description, String priority, String deadline)
            throws IOException, InterruptedException {

        String body = encodeForm(Map.of(
                "title", title,
                "description", description,
                "priority", priority,
                "deadline", deadline
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return send(request);
    }

    public HttpResponse<String> updateTask(long id, Map<String, String> fields) throws IOException, InterruptedException {
        String body = encodeForm(fields);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/" + id))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("PUT", HttpRequest.BodyPublishers.ofString(body))
                .build();

        return send(request);
    }

    public HttpResponse<String> deleteTask(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/" + id))
                .DELETE()
                .build();

        return send(request);
    }

    public HttpResponse<String> getTasks(Map<String, String> parameters) throws IOException, InterruptedException {
        String url = baseUrl + "/tasks";

        if (!parameters.isEmpty()) {
            url += "?" + encodeForm(parameters);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return send(request);
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private String encodeForm(Map<String, String> values) {
        return values.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}