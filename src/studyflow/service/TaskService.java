package studyflow.service;

import studyflow.model.Priority;
import studyflow.model.Task;
import studyflow.model.TaskStatus;
import studyflow.repository.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
}