package studyflow.util;

import studyflow.dto.TaskStatistics;
import studyflow.model.Task;

import java.util.List;
import java.util.stream.Collectors;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String taskToJson(Task task) {
        return """
                {
                  "id": %d,
                  "title": %s,
                  "description": %s,
                  "status": "%s",
                  "priority": "%s",
                  "deadline": "%s"
                }
                """.formatted(
                task.getId(),
                quote(task.getTitle()),
                quote(task.getDescription()),
                task.getStatus(),
                task.getPriority(),
                task.getDeadline()
        );
    }

    public static String tasksToJson(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "[]";
        }

        return tasks.stream()
                .map(JsonUtil::taskToJson)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    public static String errorToJson(String message) {
        return """
                {
                  "error": %s
                }
                """.formatted(quote(message));
    }

    private static String quote(String value) {
        if (value == null) {
            return "null";
        }

        return "\"" + escape(value) + "\"";
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String statisticsToJson(TaskStatistics statistics) {
        String nearestTaskJson = statistics.nearestTask() == null
                ? "null"
                : taskToJson(statistics.nearestTask());

        return """
                {
                  "totalTasks": %d,
                  "todoTasks": %d,
                  "inProgressTasks": %d,
                  "doneTasks": %d,
                  "lowPriorityTasks": %d,
                  "mediumPriorityTasks": %d,
                  "highPriorityTasks": %d,
                  "overdueTasks": %d,
                  "completionPercentage": %s,
                  "nearestTask": %s
                }
                """.formatted(
                statistics.totalTasks(),
                statistics.todoTasks(),
                statistics.inProgressTasks(),
                statistics.doneTasks(),
                statistics.lowPriorityTasks(),
                statistics.mediumPriorityTasks(),
                statistics.highPriorityTasks(),
                statistics.overdueTasks(),
                Double.toString(statistics.completionPercentage()),
                nearestTaskJson
        );
    }
}