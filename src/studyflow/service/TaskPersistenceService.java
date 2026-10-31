package studyflow.service;

import studyflow.storage.TaskFileStorage;

import java.io.IOException;

public class TaskPersistenceService {
    private final TaskService taskService;
    private final TaskFileStorage fileStorage;

    public TaskPersistenceService(TaskService taskService, TaskFileStorage fileStorage) {
        this.taskService = taskService;
        this.fileStorage = fileStorage;
    }

    public void save() throws IOException {
        fileStorage.save(taskService.getAllTasks());
    }
}