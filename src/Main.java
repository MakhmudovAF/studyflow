import studyflow.http.StudyFlowServer;
import studyflow.repository.TaskRepository;
import studyflow.service.TaskService;
import studyflow.storage.TaskFileStorage;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskFileStorage fileStorage = new TaskFileStorage(Path.of("data", "tasks.txt"));

        try {
            fileStorage.load().forEach(repository::save);

            TaskService taskService = new TaskService(repository);

            StudyFlowServer server = new StudyFlowServer(8080, taskService);
            server.start();

            System.out.println("StudyFlow запущен");
            System.out.println("http://localhost:8080/tasks");
        } catch (IOException exception) {
            System.out.println("Ошибка запуска приложения: " + exception.getMessage());
        }
    }
}