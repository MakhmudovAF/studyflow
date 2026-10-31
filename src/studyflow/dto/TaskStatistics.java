package studyflow.dto;

import studyflow.model.Task;

public record TaskStatistics(
        long totalTasks,
        long todoTasks,
        long inProgressTasks,
        long doneTasks,
        long lowPriorityTasks,
        long mediumPriorityTasks,
        long highPriorityTasks,
        long overdueTasks,
        double completionPercentage,
        Task nearestTask
) {
}