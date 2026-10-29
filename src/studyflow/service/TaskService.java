package studyflow.service;

import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;
import studyflow.repository.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskService {

    private final Repository<Task, Long> taskRepository;
    private long nextId = 1;

    public TaskService(Repository<Task, Long> taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, String description, Priority priority, LocalDate deadline) {
        Task task = new Task(nextId++, title, description, TaskStatus.TODO, priority, deadline);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public boolean deleteTask(long id) {
        return taskRepository.deleteById(id);
    }

    public boolean taskExists(long id) {
        return taskRepository.existsById(id);
    }

    public int getTaskCount() {
        return taskRepository.size();
    }

    public boolean updateStatus(long id, TaskStatus status) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isEmpty()) {
            return false;
        }

        optionalTask.get().setStatus(status);
        return true;
    }

    public boolean updatePriority(long id, Priority priority) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isEmpty()) {
            return false;
        }

        optionalTask.get().setPriority(priority);
        return true;
    }

    public boolean updateTitle(long id, String title) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isEmpty()) {
            return false;
        }

        optionalTask.get().setTitle(title);
        return true;
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
}