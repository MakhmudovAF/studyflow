package studyflow.service;

import studyflow.dto.TaskStatistics;
import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;
import studyflow.repository.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {
    private final Repository<Task, Long> taskRepository;

    public StatisticsService(Repository<Task, Long> taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskStatistics getStatistics() {
        List<Task> tasks = taskRepository.findAll();

        Map<TaskStatus, Long> statusCounts = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        Map<Priority, Long> priorityCounts = tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        long totalTasks = tasks.size();
        long todoTasks = statusCounts.getOrDefault(TaskStatus.TODO, 0L);
        long inProgressTasks = statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L);
        long doneTasks = statusCounts.getOrDefault(TaskStatus.DONE, 0L);

        long lowPriorityTasks = priorityCounts.getOrDefault(Priority.LOW, 0L);
        long mediumPriorityTasks = priorityCounts.getOrDefault(Priority.MEDIUM, 0L);
        long highPriorityTasks = priorityCounts.getOrDefault(Priority.HIGH, 0L);

        long overdueTasks = countOverdueTasks(tasks);
        double completionPercentage = calculateCompletionPercentage(totalTasks, doneTasks);
        Task nearestTask = findNearestTask(tasks);

        return new TaskStatistics(
                totalTasks,
                todoTasks,
                inProgressTasks,
                doneTasks,
                lowPriorityTasks,
                mediumPriorityTasks,
                highPriorityTasks,
                overdueTasks,
                completionPercentage,
                nearestTask
        );
    }

    private long countOverdueTasks(List<Task> tasks) {
        LocalDate today = LocalDate.now();

        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> task.getDeadline().isBefore(today))
                .count();
    }

    private double calculateCompletionPercentage(long totalTasks, long doneTasks) {
        if (totalTasks == 0) {
            return 0.0;
        }

        return doneTasks * 100.0 / totalTasks;
    }

    private Task findNearestTask(List<Task> tasks) {
        LocalDate today = LocalDate.now();

        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> !task.getDeadline().isBefore(today))
                .min(Comparator.comparing(Task::getDeadline))
                .orElse(null);
    }
}