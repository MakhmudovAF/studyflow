package studyflow.service;

import studyflow.exception.TaskNotFoundException;
import studyflow.exception.ValidationException;
import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;
import studyflow.repository.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskService {

    private final Repository<Task, Long> taskRepository;
    private long nextId;

    public TaskService(Repository<Task, Long> taskRepository) {
        this.taskRepository = taskRepository;
        this.nextId = taskRepository.findAll().stream()
                .mapToLong(Task::getId)
                .max()
                .orElse(0L) + 1;
    }

    public Task createTask(String title, String description, Priority priority, LocalDate deadline) {
        validateTask(title, priority, deadline);

        Task task = new Task(nextId++, title, description, TaskStatus.TODO, priority, deadline);
        return taskRepository.save(task);
    }

    public Task getTaskById(long id) {
        return getTaskOrThrow(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void deleteTask(long id) {
        getTaskOrThrow(id);
        taskRepository.deleteById(id);
    }

    public boolean taskExists(long id) {
        return taskRepository.existsById(id);
    }

    public int getTaskCount() {
        return taskRepository.size();
    }

    public void updateStatus(long id, TaskStatus status) {
        validateStatus(status);

        Task task = getTaskOrThrow(id);
        task.setStatus(status);
    }

    public void updatePriority(long id, Priority priority) {
        validatePriority(priority);

        Task task = getTaskOrThrow(id);
        task.setPriority(priority);
    }

    public void updateTitle(long id, String title) {
        validateTitle(title);

        Task task = getTaskOrThrow(id);
        task.setTitle(title);
    }

    public void updateDeadline(long id, LocalDate deadline) {
        validateDeadline(deadline);

        Task task = getTaskOrThrow(id);
        task.setDeadline(deadline);
    }

    public Task updateTask(long id, String title, String description, TaskStatus status, Priority priority, LocalDate deadline) {
        Task task = getTaskOrThrow(id);

        if (title == null && description == null && status == null && priority == null && deadline == null) {
            throw new ValidationException("Не передано ни одного поля для обновления");
        }

        if (title != null) {
            validateTitle(title);
        }

        if (deadline != null) {
            validateDeadline(deadline);
        }

        if (title != null) {
            task.setTitle(title);
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (status != null) {
            task.setStatus(status);
        }

        if (priority != null) {
            task.setPriority(priority);
        }

        if (deadline != null) {
            task.setDeadline(deadline);
        }

        return task;
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    public List<Task> getTasksByPriority(Priority priority) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .toList();
    }

    public List<Task> getTasksSortedByDeadline() {
        return taskRepository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDeadline))
                .toList();
    }

    public List<Task> getTasksSortedByDeadlineDesc() {
        return taskRepository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDeadline).reversed())
                .toList();
    }

    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();

        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> task.getDeadline().isBefore(today))
                .sorted(Comparator.comparing(Task::getDeadline))
                .toList();
    }

    public Optional<Task> getNearestTask() {
        LocalDate today = LocalDate.now();

        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> !task.getDeadline().isBefore(today))
                .min(Comparator.comparing(Task::getDeadline));
    }

    public List<Task> searchByTitle(String query) {
        String normalizedQuery = query.toLowerCase();

        return taskRepository.findAll().stream()
                .filter(task -> task.getTitle().toLowerCase().contains(normalizedQuery))
                .toList();
    }

    public long getCompletedTaskCount() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
    }

    public double getCompletionPercentage() {
        List<Task> tasks = taskRepository.findAll();

        if (tasks.isEmpty()) {
            return 0.0;
        }

        long completed = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();

        return completed * 100.0 / tasks.size();
    }

    public Map<TaskStatus, List<Task>> groupTasksByStatus() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getStatus));
    }

    public Map<TaskStatus, Long> countTasksByStatus() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
    }

    public Map<Priority, Long> countTasksByPriority() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
    }

    public List<Task> getUpcomingTasks(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> !task.getDeadline().isBefore(today))
                .filter(task -> !task.getDeadline().isAfter(endDate))
                .sorted(Comparator.comparing(Task::getDeadline))
                .toList();
    }

    private Task getTaskOrThrow(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void validateTask(String title, Priority priority, LocalDate deadline) {
        validateTitle(title);
        validatePriority(priority);
        validateDeadline(deadline);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Название задачи не должно быть пустым");
        }
    }

    private void validatePriority(Priority priority) {
        if (priority == null) {
            throw new ValidationException("Приоритет задачи не должен быть null");
        }
    }

    private void validateStatus(TaskStatus status) {
        if (status == null) {
            throw new ValidationException("Статус задачи не должен быть null");
        }
    }

    private void validateDeadline(LocalDate deadline) {
        if (deadline == null) {
            throw new ValidationException("Дедлайн задачи не должен быть null");
        }

        if (deadline.isBefore(LocalDate.now())) {
            throw new ValidationException("Дедлайн задачи не может находиться в прошлом");
        }
    }

    public List<Task> findTasks(TaskStatus status, Priority priority, String search, String sort) {
        var stream = taskRepository.findAll().stream();

        if (status != null) {
            stream = stream.filter(task -> task.getStatus() == status);
        }

        if (priority != null) {
            stream = stream.filter(task -> task.getPriority() == priority);
        }

        if (search != null && !search.isBlank()) {
            String normalizedSearch = search.toLowerCase(Locale.ROOT);

            stream = stream.filter(task ->
                    task.getTitle().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                            || task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedSearch)
            );
        }

        if (sort != null) {
            stream = switch (sort) {
                case "deadline" -> stream.sorted(Comparator.comparing(Task::getDeadline));
                case "title" -> stream.sorted(Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER));
                case "priority" -> stream.sorted(Comparator.comparing(Task::getPriority).reversed());
                default -> throw new ValidationException("Неизвестный способ сортировки: " + sort);
            };
        }

        return stream.toList();
    }
}