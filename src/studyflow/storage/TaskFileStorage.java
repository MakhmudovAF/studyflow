package studyflow.storage;

import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskFileStorage {
    private static final String DELIMITER = "\\|";
    private static final String OUTPUT_DELIMITER = "|";

    private final Path path;

    public TaskFileStorage(Path path) {
        this.path = path;
    }

    public void save(List<Task> tasks) throws IOException {
        createParentDirectory();

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                writer.write(taskToLine(task));
                writer.newLine();
            }
        }
    }

    public List<Task> load() throws IOException {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    tasks.add(lineToTask(line));
                } catch (RuntimeException exception) {
                    throw new IOException("Некорректные данные в строке " + lineNumber, exception);
                }
            }
        }

        return tasks;
    }

    private String taskToLine(Task task) {
        return task.getId() + OUTPUT_DELIMITER
                + encode(task.getTitle()) + OUTPUT_DELIMITER
                + encode(task.getDescription()) + OUTPUT_DELIMITER
                + task.getStatus() + OUTPUT_DELIMITER
                + task.getPriority() + OUTPUT_DELIMITER
                + task.getDeadline();
    }

    private Task lineToTask(String line) {
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length != 6) {
            throw new IllegalArgumentException("Ожидалось 6 полей, получено: " + parts.length);
        }

        long id = Long.parseLong(parts[0]);
        String title = decode(parts[1]);
        String description = decode(parts[2]);
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        Priority priority = Priority.valueOf(parts[4]);
        LocalDate deadline = LocalDate.parse(parts[5]);

        return new Task(id, title, description, status, priority, deadline);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void createParentDirectory() throws IOException {
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}