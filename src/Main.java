import studyflow.model.Priority;
import studyflow.model.TaskStatus;
import studyflow.repository.TaskRepository;
import studyflow.service.TaskService;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TaskRepository repository = new TaskRepository();
        TaskService service = new TaskService(repository);

        service.createTask(
                "Изучить Collections",
                "Разобраться с List, Set и Map",
                Priority.HIGH,
                LocalDate.of(2026, 11, 5)
        );

        service.createTask(
                "Изучить Stream API",
                "Разобраться с filter, map и sorted",
                Priority.MEDIUM,
                LocalDate.of(2026, 11, 10)
        );

        System.out.println("Исходные задачи:");
        service.getAllTasks().forEach(System.out::println);

        service.updateStatus(1L, TaskStatus.IN_PROGRESS);
        service.updatePriority(2L, Priority.HIGH);
        service.updateTitle(2L, "Освоить Stream API");

        System.out.println();
        System.out.println("После изменений:");
        service.getAllTasks().forEach(System.out::println);
    }
}