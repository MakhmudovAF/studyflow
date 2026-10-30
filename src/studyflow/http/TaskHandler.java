package studyflow.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import studyflow.exception.TaskNotFoundException;
import studyflow.model.Task;
import studyflow.service.TaskService;
import studyflow.util.JsonUtil;

import java.io.IOException;

public class TaskHandler implements HttpHandler {
    private final TaskService taskService;

    public TaskHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                HttpResponseUtil.sendJson(exchange, 405, JsonUtil.errorToJson("Метод не поддерживается"));
                return;
            }

            handleGet(exchange);
        } catch (TaskNotFoundException exception) {
            HttpResponseUtil.sendJson(exchange, 404, JsonUtil.errorToJson(exception.getMessage()));
        } catch (Exception exception) {
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

    private void getAllTasks(HttpExchange exchange) throws IOException {
        String json = JsonUtil.tasksToJson(taskService.getAllTasks());
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