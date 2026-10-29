package studyflow.repository;

import studyflow.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskRepository {
    private final Map<Long, Task> tasks = new HashMap<>();

    public void save(Task task) {
        tasks.put(task.getId(), task);
    }

    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public boolean deleteById(long id) {
        return tasks.remove(id) != null;
    }

    public boolean existsById(long id) {
        return tasks.containsKey(id);
    }

    public int size() {
        return tasks.size();
    }
}