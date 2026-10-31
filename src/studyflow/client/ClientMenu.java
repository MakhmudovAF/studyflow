package studyflow.client;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientMenu {
    private final StudyFlowClient client;
    private final Scanner scanner;

    public ClientMenu(StudyFlowClient client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();

            String command = scanner.nextLine().trim();

            try {
                switch (command) {
                    case "1" -> showAllTasks();
                    case "2" -> showTaskById();
                    case "3" -> createTask();
                    case "4" -> updateTask();
                    case "5" -> deleteTask();
                    case "0" -> running = false;
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (IOException exception) {
                System.out.println("Не удалось подключиться к серверу: " + exception.getMessage());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                System.out.println("Запрос был прерван");
                running = false;
            } catch (NumberFormatException exception) {
                System.out.println("Необходимо ввести число");
            }

            System.out.println();
        }

        System.out.println("StudyFlow Client завершён");
    }

    private void printMenu() {
        System.out.println("""
                1. Показать все задачи
                2. Получить задачу по ID
                3. Создать задачу
                4. Изменить задачу
                5. Удалить задачу
                0. Выход
                """);
        System.out.print("> ");
    }

    private void showAllTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = client.getAllTasks();
        printResponse(response);
    }

    private void showTaskById() throws IOException, InterruptedException {
        long id = readId();

        HttpResponse<String> response = client.getTaskById(id);
        printResponse(response);
    }

    private void createTask() throws IOException, InterruptedException {
        System.out.print("Название: ");
        String title = scanner.nextLine();

        System.out.print("Описание: ");
        String description = scanner.nextLine();

        System.out.print("Приоритет (LOW/MEDIUM/HIGH): ");
        String priority = scanner.nextLine().trim();

        System.out.print("Дедлайн (YYYY-MM-DD): ");
        String deadline = scanner.nextLine().trim();

        HttpResponse<String> response = client.createTask(title, description, priority, deadline);
        printResponse(response);
    }

    private void updateTask() throws IOException, InterruptedException {
        long id = readId();

        Map<String, String> fields = new HashMap<>();

        System.out.println("Оставьте поле пустым, если не хотите его изменять.");

        System.out.print("Новое название: ");
        putIfNotBlank(fields, "title", scanner.nextLine());

        System.out.print("Новое описание: ");
        putIfNotBlank(fields, "description", scanner.nextLine());

        System.out.print("Новый статус (TODO/IN_PROGRESS/DONE): ");
        putIfNotBlank(fields, "status", scanner.nextLine());

        System.out.print("Новый приоритет (LOW/MEDIUM/HIGH): ");
        putIfNotBlank(fields, "priority", scanner.nextLine());

        System.out.print("Новый дедлайн (YYYY-MM-DD): ");
        putIfNotBlank(fields, "deadline", scanner.nextLine());

        HttpResponse<String> response = client.updateTask(id, fields);
        printResponse(response);
    }

    private void deleteTask() throws IOException, InterruptedException {
        long id = readId();

        HttpResponse<String> response = client.deleteTask(id);

        if (response.statusCode() == 204) {
            System.out.println("Задача удалена");
            return;
        }

        printResponse(response);
    }

    private void putIfNotBlank(Map<String, String> fields, String key, String value) {
        if (!value.isBlank()) {
            fields.put(key, value.trim());
        }
    }

    private long readId() {
        System.out.print("ID задачи: ");
        return Long.parseLong(scanner.nextLine().trim());
    }

    private void printResponse(HttpResponse<String> response) {
        System.out.println();
        System.out.println("HTTP " + response.statusCode());

        if (!response.body().isBlank()) {
            System.out.println(response.body());
        }
    }
}