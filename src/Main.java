import studyflow.model.Priority;
import studyflow.repository.TaskRepository;
import studyflow.service.TaskService;
import studyflow.storage.TaskFileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskFileStorage fileStorage = new TaskFileStorage(Path.of("data", "tasks.txt"));

        try {
            fileStorage.load().forEach(repository::save);

            TaskService service = new TaskService(repository);

            if (service.getTaskCount() == 0) {
                service.createTask(
                        "Изучить Collections",
                        "List, Set и Map",
                        Priority.HIGH,
                        LocalDate.now().plusDays(5)
                );

                service.createTask(
                        "Изучить Stream API",
                        "filter, map и sorted",
                        Priority.MEDIUM,
                        LocalDate.now().plusDays(10)
                );
            }

            System.out.println("Задачи:");
            service.getAllTasks().forEach(System.out::println);

            fileStorage.save(service.getAllTasks());
        } catch (IOException exception) {
            System.out.println("Ошибка работы с файлом: " + exception.getMessage());
        }
    }
}