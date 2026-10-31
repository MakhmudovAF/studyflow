package studyflow.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import studyflow.exception.TaskNotFoundException;
import studyflow.exception.ValidationException;
import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;
import studyflow.service.TaskPersistenceService;
import studyflow.service.TaskService;
import studyflow.util.FormDataParser;
import studyflow.util.JsonUtil;
import studyflow.util.QueryParamsParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class TaskHandler implements HttpHandler {
    private final TaskService taskService;
    private final TaskPersistenceService persistenceService;

    public TaskHandler(TaskService taskService, TaskPersistenceService persistenceService) {
        this.taskService = taskService;
        this.persistenceService = persistenceService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "PUT" -> handlePut(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> {
                    exchange.getResponseHeaders().set("Allow", "GET, POST, PUT, DELETE");
                    HttpResponseUtil.sendJson(exchange, 405, JsonUtil.errorToJson("Метод не поддерживается"));
                }
            }
        } catch (TaskNotFoundException exception) {
            HttpResponseUtil.sendJson(exchange, 404, JsonUtil.errorToJson(exception.getMessage()));
        } catch (ValidationException exception) {
            HttpResponseUtil.sendJson(exchange, 400, JsonUtil.errorToJson(exception.getMessage()));
        } catch (Exception exception) {
            exception.printStackTrace();
            HttpResponseUtil.sendJson(exchange, 500, JsonUtil.errorToJson("Внутренняя ошибка сервера"));
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if ("/tasks".equals(path) || "/tasks/".equals(path)) {
            getAllTasks(exchange);
            return;
        }

        if (path.startsWith("/tasks/")) {
            getTaskById(exchange, path);
            return;
        }

        HttpResponseUtil.sendJson(exchange, 404, JsonUtil.errorToJson("Ресурс не найден"));
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (!"/tasks".equals(path) && !"/tasks/".equals(path)) {
            HttpResponseUtil.sendJson(exchange, 404, JsonUtil.errorToJson("Ресурс не найден"));
            return;
        }

        Map<String, String> values = readForm(exchange);

        String title = values.get("title");
        String description = values.getOrDefault("description", "");
        Priority priority = parsePriority(values.get("priority"));
        LocalDate deadline = parseDeadline(values.get("deadline"));

        Task task = taskService.createTask(title, description, priority, deadline);

        persistenceService.save();

        exchange.getResponseHeaders().set("Location", "/tasks/" + task.getId());
        HttpResponseUtil.sendJson(exchange, 201, JsonUtil.taskToJson(task));
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        long id = extractTaskId(exchange);

        Map<String, String> values = readForm(exchange);

        String title = values.get("title");
        String description = values.get("description");
        TaskStatus status = values.containsKey("status") ? parseStatus(values.get("status")) : null;
        Priority priority = values.containsKey("priority") ? parsePriority(values.get("priority")) : null;
        LocalDate deadline = values.containsKey("deadline") ? parseDeadline(values.get("deadline")) : null;

        Task task = taskService.updateTask(id, title, description, status, priority, deadline);

        persistenceService.save();

        HttpResponseUtil.sendJson(exchange, 200, JsonUtil.taskToJson(task));
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        long id = extractTaskId(exchange);

        taskService.deleteTask(id);
        persistenceService.save();

        HttpResponseUtil.sendEmpty(exchange, 204);
    }

    private Map<String, String> readForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return FormDataParser.parse(body);
    }

    private long extractTaskId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();

        if (!path.startsWith("/tasks/")) {
            throw new ValidationException("Не указан идентификатор задачи");
        }

        String idPart = path.substring("/tasks/".length());

        if (idPart.isBlank() || idPart.contains("/")) {
            throw new ValidationException("Некорректный идентификатор задачи");
        }

        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Некорректный идентификатор задачи");
        }
    }

    private Priority parsePriority(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Поле priority обязательно");
        }

        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Некорректный приоритет: " + value);
        }
    }

    private TaskStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Статус задачи не должен быть пустым");
        }

        try {
            return TaskStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Некорректный статус: " + value);
        }
    }

    private LocalDate parseDeadline(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Поле deadline обязательно");
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new ValidationException("Некорректная дата: " + value + ". Используйте формат YYYY-MM-DD");
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryParamsParser.parse(exchange.getRequestURI().getRawQuery());

        TaskStatus status = params.containsKey("status") ? parseStatus(params.get("status")) : null;
        Priority priority = params.containsKey("priority") ? parsePriority(params.get("priority")) : null;
        String search = params.get("search");
        String sort = params.get("sort");

        String json = JsonUtil.tasksToJson(taskService.findTasks(status, priority, search, sort));
        HttpResponseUtil.sendJson(exchange, 200, json);
    }

    private void getTaskById(HttpExchange exchange, String path) throws IOException {
        String idPart = path.substring("/tasks/".length());

        if (idPart.isBlank() || idPart.contains("/")) {
            HttpResponseUtil.sendJson(exchange, 404, JsonUtil.errorToJson("Ресурс не найден"));
            return;
        }

        try {
            long id = Long.parseLong(idPart);
            Task task = taskService.getTaskById(id);

            HttpResponseUtil.sendJson(exchange, 200, JsonUtil.taskToJson(task));
        } catch (NumberFormatException exception) {
            HttpResponseUtil.sendJson(exchange, 400, JsonUtil.errorToJson("Некорректный идентификатор задачи"));
        }
    }
}